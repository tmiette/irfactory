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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actions/FactoryAction.java,v $
$Revision: 1.16 $
$Date: 2007/02/07 18:12:42 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.actions;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.projectfactory.server.ApplicationServerBase;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.SubEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Aggregates all the actions.
 * @author David Lambert
 */
public class ActionList extends ApplicationServerBase {

    /** List of actions. */
    public List<Action> actions = new ArrayList(10);

    /** Constructor. */
    public ActionList(ApplicationServerBase serverBase) {
        super(serverBase);
    }

    /** Clears objects. */
    public void clear() {
        actions.clear();
    }

    /** Sorts objects. */
    public void sort() {
        Collections.sort(actions);
    }

    /** Saves all entities. */
    public void saveAll(WriterXML xml, TransactionXML transaction, 
                        boolean demo, String iid) {
        saveActions(xml, transaction, demo, iid);
    }

    /** Saves all projects. */
    public void saveActions(WriterXML xml, TransactionXML transaction, 
                            boolean demo, String iid) {
        saveEntity(xml, transaction, actions, demo, iid);
    }

    /** Saves all objects in file(s). */
    public void saveAll(WriterXML xml, TransactionXML transaction) {
        String filename = transaction.getServer().getPath() + XMLWrapper.SLASH + "actions.xml";
        try {
            WriterXML outputXml = 
                new WriterXML(filename, "factory");
            saveActions(outputXml, transaction, false, null);
            outputXml.end();
            traceSaving(xml, filename);
        } catch (IOException ex) {
            traceError(xml, filename);
        }
    }

    /** Adds files to be opened. */
    public void addFileList(List<String> files, String prefix) {
        files.add(prefix + "actions.xml");
    }

    /** Adds classes used for replication. */
    public void addClassList(List classes) {
        classes.add("action");
    }

    /** Sends the object names to the client based on a list. */
    public boolean isAvailable(TransactionXML transaction, String category, 
                               String filter, Entity entity) {
        if (category.equals("actions")) {
            Action action = (Action)entity;
            if (action.isAnswer())
                return false;
            if (filter.equals("filter:actions:my:all") || 
                filter.equals("filter:actions:my:pending") || 
                filter.equals("filter:actions:my:complete")) {
                if(!action.isForActor(transaction, 
                                    transaction.getSession().getOperator()))
                    return false;
            }
            if (filter.equals("filter:actions:myteams:all") || 
                filter.equals("filter:actions:myteams:pending") || 
                filter.equals("filter:actions:myteams:complete")) {
                if(!action.isForActorMembers(transaction, 
                                    transaction.getSession().getOperator()))
                    return false;
            }
            if (filter.equals("filter:actions:pending") || 
                filter.equals("filter:actions:my:pending") || 
                filter.equals("filter:actions:myteams:pending")) {
                if(!action.isPending())
                    return false;
			}
            if (filter.equals("filter:actions:complete") || 
                filter.equals("filter:actions:my:complete") || 
                filter.equals("filter:actions:myteams:complete")) {
                if(!action.isComplete())
                    return false;
            }
        }
        return true;
    }

    /** Creates a list based on category. */
    public void listByCategory(TransactionXML transaction, List lists, 
                               String category, String filter) {
        if (category.equals("actions"))
            lists.add(actions);
    }

    /** Creates the list based on class. */
    public void listByClass(TransactionXML transaction, List lists, 
                            String classname) {
        if (classname.equals("action"))
            lists.add(actions);
    }

    /** Returns the appropriate parser in order to read an element. */
    public SubEntityServerXML getParser(TransactionXML transaction, String tag) {
        if (tag.equals("action"))
            return new SubEntityServerXML(this, transaction, new Action(), actions);
        return null;
    }
}
