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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Task.java,v $
$Revision: 1.12 $
$Date: 2007/02/12 16:12:27 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import net.sourceforge.projectfactory.server.actions.Action;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Defines a task, which is an item attached to a business plan or a project.
 * @author David Lambert
 */
public class Task extends Item {

    /** Complexity (high/medium/low). */
    private int complexity;

    /** Status : refers to TaskStatusType. */
    public int actionStatus;

    /** Indicates that the task has been included in a forecast (used during forecast generation). */
    boolean inserted;

    /** Indicates a predecessor is completed (used during forecast generation. */
    boolean predecessorComplete;

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "task");

        super.xmlOut(xml, transaction, false);

        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "complexity", complexity);
            xmlAttribute(xml, "actionstatus", actionStatus);
            if (transaction.isDetail() && gotAction(transaction)) 
                xmlAttribute(xml, "action", "y");
        }

        if (tags)
            xmlEnd(xml);
    }
    
    /** Indicates if an action is attached to the task. */
    boolean gotAction(TransactionXML transaction) {
        for(Action action:transaction.getServer().actions.actions) {
            if(action.task == this)
                return true;
        }
        return false;
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) {
            return true;
        }

        if (tag.equals("complexity")) {
            complexity = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("actionstatus")) {
            actionStatus = xmlInInt(xml, value);
            return true;
        }

        return false;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) {
            return;
        }

        Task otherTask = (Task)other;
        super.update(transaction, other);
        this.complexity = otherTask.complexity;
        this.actionStatus = otherTask.actionStatus;
    }
    
    /** Rests the task. */
    public void reset() {
        this.complexity = 0;
        this.actionStatus = 0;
        this.type = 0;
        this.priority = 0;
    }
}
