/*

Copyright (c) 2005, 2006 David Lambert

This file is part of Factory.

Factory is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Factory is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Factory; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/entities/xml/EntityServerXML.java,v $
$Revision: 1.34 $
$Date: 2007/03/04 21:04:44 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.entities.xml;

import java.util.Collections;
import java.util.List;

import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.xml.ReaderServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * XML Server-side parser used to manage entities.
 * @author David Lambert
 */
public abstract class EntityServerXML extends ReaderServerXML {

    /** Entity to be created or updated. */
    protected Entity entity;

    /** Draft of the entity.
     * This object is instanciated and used during the parsing and
     * then is used in order to update the entity. */
    protected Entity draft;

    /** List that manages the entity. */
    protected List list;

    /** Indicates if server data needs to be saved. */
    private boolean isDirty;

    /** Constructor. */
    protected EntityServerXML(TransactionXML transaction) {
        super(transaction);
    }

    /** Constructor. */
    protected EntityServerXML(TransactionXML transaction, Entity entity, 
                              List list) {
        this(transaction);
        this.entity = entity;
        this.list = list;
    }

    /** Starts a tag. */
    protected void startsTag(String tag) {
        if (entity != null) {
            BaseEntityServerXML parser = entity.xmlIn(transaction, tag);
            if (parser != null)
                parser.xmlIn(this);
        }
    }

    /** Interprets a tag. */
    protected void getTag(String tag, String text) {
        entity.xmlIn(xml, transaction, tag, text);
    }

    /** Ends the tag interpretation. */
    protected void end() {
        isDirty = false;
        if (list == null || entity == null)
            return;

        synchronized (entity) {
            switch (transaction.getCode()) {
            case TransactionXML.NEW:
            case TransactionXML.UPDATE:
                if(entity.xmlValidate(xml, transaction, list))
                    return;
                if(xml.isInError())
                    return;
                break;

            case TransactionXML.REPLICATE:
                if (entity.isDemo())
                    return;
                break;
            }

            int index = list.indexOf(entity);
            draft = (index >= 0) ? (Entity)list.get(index) : null;

            switch (transaction.getCode()) {
            case TransactionXML.DEFAULT:
                transaction.setCode(TransactionXML.DETAIL);
                entity.defaults(transaction);
                entity.xmlOut(xml, transaction, true);
                break;

            case TransactionXML.GET:
                transaction.setCode(TransactionXML.DETAIL);
                if (draft != null)
                    draft.xmlOut(xml, transaction, true);
                break;

            case TransactionXML.SAVE:
                if (draft != null)
                    draft.xmlOut(xml, transaction, true);
                break;

            case TransactionXML.LOAD:
                if (draft != null) {
                    if (entity.getUpdated() != null && 
                        (draft.getUpdated() == null || 
                         ((draft.getUpdated() != null && 
                           entity.getUpdated().after(draft.getUpdated()))))) {
                        synchronized (draft) {
                            draft.update(transaction, entity);
                        }
                    }
                }
                else
                    list.add(entity);
                break;

            case TransactionXML.NEW:
                synchronized (list) {
                    list.add(entity);
                    Collections.sort(list);
                }
                entity.create(transaction);
                transaction.setCode(TransactionXML.SUMMARY);
                entity.xmlOut(xml, transaction, true);
                xml.xmlMessage(FactoryWriterXML.MESSAGE, 
                                                     "message:created", "", 
                                                     entity.getName());
                isDirty = true;
                break;

            case TransactionXML.UPDATE:
                if (draft != null) {
                    if (draft.getRevision() != entity.getRevision()) {
                        xml.xmlMessage(FactoryWriterXML.ERROR, 
															"error:lock", "");
                        return;
                    }
                    synchronized (draft) {
                        draft.update(transaction, entity);
                    }
                } else
                    synchronized (list) {
                        list.add(entity);
                        Collections.sort(list);
                    }

                transaction.setCode(TransactionXML.SHORTSUMMARY);
                entity.xmlOut(xml, transaction, true);
                xml.xmlMessage(FactoryWriterXML.MESSAGE, 
                                                     "message:updated", "", 
                                                     entity.getName());
                isDirty = true;
                break;

            case TransactionXML.DELETE:
                if(entity.xmlValidateDelete(xml, transaction))
                    return;

                synchronized (list) {
                    list.remove(entity);
                }
                xml.xmlMessage(FactoryWriterXML.MESSAGE, 
                                                     "message:deleted", "", 
                                                     entity.getName());
                isDirty = true;
                break;

            case TransactionXML.REPLICATE:
                if (draft != null) {
                    if (entity.getUpdated() != null && 
                        (draft.getUpdated() == null || 
                         ((draft.getUpdated() != null && 
                           entity.getUpdated().after(draft.getUpdated()))))) {
                        synchronized (draft) {
                            draft.update(transaction, entity);
							synchronized (list) {
								draft.afterReplication(xml, transaction, list);
							}
                        }
                        xml.xmlMessage(FactoryWriterXML.MESSAGE, 
                                         transaction.getSession().isRemote() ? 
                                         "message:replicated:updated:remote" : 
                                         "message:replicated:updated:local", 
                                         entity.getName());
                        isDirty = true;
                    } else
                        xml.xmlMessage(FactoryWriterXML.TRACE, 
                                         "message:replicated:uptodate", 
                                         entity.getName());
                } else {
                    synchronized (list) {
                        list.add(entity);
                        Collections.sort(list);
						entity.afterReplication(xml, transaction, list);
                    }
                    xml.xmlMessage(FactoryWriterXML.MESSAGE, 
                                         transaction.getSession().isRemote() ? 
                                         "message:replicated:created:remote" : 
                                         "message:replicated:created:local", 
                                         entity.getName());
                    isDirty = true;
                }
                break;
            }
        }
        entity = null;
        afterEnd();
    }

    /** Triggered after the end of the tag interpretation. */
    protected void afterEnd() {
    }

    /** Runs the server that parses sub-entities. */
    protected final void runSubServerXML(BaseEntity subEntity, List list) {
        new BaseEntityServerXML(transaction, subEntity, list).xmlIn(this);
    }

    /** Indicates if server data needs to be saved. */
    protected boolean isDirty() {
        return isDirty;
    }
}
