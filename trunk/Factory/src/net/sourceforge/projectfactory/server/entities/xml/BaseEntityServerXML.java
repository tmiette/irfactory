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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/entities/xml/BaseEntityServerXML.java,v $
$Revision: 1.11 $
$Date: 2007/01/17 22:12:21 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.entities.xml;

import java.util.List;

import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.xml.ReaderServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * XML Server-side parser used to manage base entities.
 * @author David Lambert
 */
public class BaseEntityServerXML extends ReaderServerXML {

    /** Entity used during parsing. */
    protected BaseEntity subEntity;

    /** List that references the entity. */
    protected List list;

    /** Constructor. */
    public BaseEntityServerXML(TransactionXML transaction, 
                               BaseEntity subEntity, List list) {
        super(transaction);
        this.subEntity = subEntity;
        this.list = list;
    }

    /**	Assigns a new sub-entity and a list. */
    public void set(BaseEntity subEntity, List list) {
        this.subEntity = subEntity;
        this.list = list;
    }

    /** Interprets a tag. */
    protected void getTag(String tag, String text) {
        if (subEntity != null) {
            subEntity.xmlIn(xml, transaction, tag, text);
        }
    }

    /** Ends the tag interpretation. */
    protected void end() {
        if (list != null) {
            if (list.contains(subEntity)) {
                xml.xmlMessage(FactoryWriterXML.ERROR, 
                               "error:duplicate:name:sub", 
                               subEntity.getName());
            } else {
                list.add(subEntity);
            }
        }
    }
}
