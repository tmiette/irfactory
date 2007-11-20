/*

Copyright (c) 2006 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/FactoryServerBase.java,v $
$Revision: 1.17 $
$Date: 2007/02/11 20:55:24 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.SubEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * Base class used for sub-servers.
 * @author David Lambert
 */
public abstract class FactoryServerBase {

    /** Dirty flag. */
    private boolean dirty;

    /** List of servers (sub-servers). */    
    protected List<FactoryServerBase> servers = new ArrayList(5);
    
    /** Constructor. */
    public FactoryServerBase(FactoryServerBase serverBase) {
        super();
        if(serverBase != null)
            serverBase.servers.add(this);
    }
    
    /** Sets the 'dirty' flag. */
    public void setDirty() {
        dirty = true;
    }

    /** Sets the 'dirty' flag to no. */
    public void setNotDirty() {
        dirty = false;
    }

    /** Indicates if the server is 'dirty'. */
    public boolean isDirty() {
        return dirty;
    }

    /** Saves a list of entities. */
    protected void saveEntity(FactoryWriterXML xml, TransactionXML transaction, 
                              List list, boolean demo, String iid) {
        for (Object entity: list)
            saveEntity(xml, transaction, (Entity)entity, demo, iid);
    }

    /** Saves an entity. */
    protected void saveEntity(FactoryWriterXML xml, TransactionXML transaction, 
                              Entity entity, boolean demo, String iid) {
        if ((demo && entity.isDemo()) || 
            (!demo && !entity.isDemo() && 
                (iid == null || 
                    (iid != null && iid.length() > 0 && iid.equals(entity.getIid()))))) {
				if(iid != null && iid.length() > 0) {
					List<Entity> prerequisites = new ArrayList(10);
					entity.addPrerequisites(transaction, prerequisites);
					for(Entity otherEntity: prerequisites) 
						otherEntity.xmlOut(xml, transaction, true);
				}	
				entity.xmlOut(xml, transaction, true);
			}
    }

    /** Sends a trace message when the file is saved. */
    protected void traceSaving(FactoryWriterXML xml, String filename) {
        xml.xmlMessage(FactoryWriterXML.TRACE, "message:savingcomplete", "", 
                       filename);
    }

    /** Sends an error message when the file is not saved. */
    protected void traceError(FactoryWriterXML xml, String filename) {
        xml.xmlMessage(FactoryWriterXML.ERROR, "error:filenotsaved", "", 
                       filename);
    }

    /** Clears objects. */
    public void clear() {
    }

    /** Sorts objects. */
    public void sort() {
    }

    /** Adds files to be opened. */
    public void addFileList(List<String> files, String prefix) {
    }

    /** Adds classes used for replication. */
    public void addClassList(List classes) {
    }

    /** Sends the object names to the client based on a list. */
    public boolean isAvailable(TransactionXML transaction, String category, 
                               String filter, Entity entity) {
        return true;
    }

    /** Creates a list based on category. */
    public void listByCategory(TransactionXML transaction, List lists, 
                               String category, String filter) {
    }

    /** Creates the list based on class. */
    public void listByClass(TransactionXML transaction, List lists, 
                            String classname) {
    }

    /** Saves all entities. */
    public void saveAll(FactoryWriterXML xml, TransactionXML transaction, 
                        boolean demo, String iid) {
    }

    /** Saves all objects in file(s). */
    public void saveAll(FactoryWriterXML xml, TransactionXML transaction) {
    }

    /** Returns the appropriate parser in order to read an element. */
    public SubEntityServerXML getParser(TransactionXML transaction, String tag) {
        return null;
    }

    /** Returns a tag for the specified category. */
    private static String getTagCategory(String category) {
        if (category.equals("WAR"))
            return FactoryWriterXML.WARNING;
        if (category.equals("ERR"))
            return FactoryWriterXML.ERROR;
        if (category.equals("FAT"))
            return FactoryWriterXML.FATAL;
        if (category.equals("TRC"))
            return FactoryWriterXML.TRACE;
        return FactoryWriterXML.MESSAGE;
    }

    /** Sens a message based on dictionary. */
    public static void addMessageDictionary(FactoryWriterXML xml, 
                                            String category, String label, 
                                            String... args) {
        xml.xmlMessage(getTagCategory(category), label, args);
    }
}
