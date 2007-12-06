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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Assignment.java,v $
$Revision: 1.12 $
$Date: 2007/01/27 16:30:56 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.resources.Resource;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Defines an assignment in a plan of one taks to an actor or anonymous.
 * @author David Lambert
 */
public class Assignment extends BaseEntity {

    /** Project plan. */
    protected Plan plan;

    /** Task. */
    protected Task task;

    /** Anonymous who is assigned to the task. */
    public Anonymous anonymous;

    /** Actor who is assigned to the task. */
    public Resource actor;

    /** Indicate the percentage of daily availability for the task. */
    int fte;

    /** Indicates if the free time, based on fte, is free to be shared with soem other tasks. */
    boolean free;

    /** Constructor. */
    public Assignment(Plan plan) {
        this.plan = plan;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "assignment");

        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, transaction, "actor", actor);

            if (anonymous != null)
                xmlAttribute(xml, "anonymous", anonymous.getName());

            xmlAttribute(xml, "fte", fte);
            xmlAttribute(xml, "free", free);

            if (task != null) {
                xmlAttribute(xml, transaction, "task", task);
                if (transaction.isDetail())
                    xmlAttribute(xml, "type", task.type);
            }
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (tag.equals("fte")) {
            fte = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("free")) {
            free = xmlInBoolean(value);
            return true;
        }

        if (tag.equals("task")) {
            if (plan != null && plan.project != null) {
                task = (Task) xmlInEntity(xml, 
                                            transaction, 
                                            value, 
                                            new Task(), 
                                            plan.project.tasks, 
                                            "error:incorrect:task", 
                                            plan);
            }
            return true;
        }

        if (tag.equals("actor")) {
            actor = (Resource) xmlInEntity(xml, 
                                        transaction, 
                                        value, 
                                        new Resource(), 
                                        transaction.getServer().actors.actors, 
                                        "error:incorrect:assignment:actor", 
                                        plan);
            return true;
        }

        if (tag.equals("anonymous")) {
            if (plan != null) {
                anonymous = (Anonymous) xmlInEntity(xml, 
                                            transaction, 
                                            value, 
                                            new Anonymous(plan), 
                                            plan.anonymities, 
                                            "error:incorrect:anonymous", 
                                            plan);
            }
            return true;
        }

        return false;
    }

    /** Indicates if the element is valid and contains information. */
    boolean isValid() {
        return actor != null || anonymous != null;
    }
}
