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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Constraint.java,v $
$Revision: 1.12 $
$Date: 2007/01/04 15:42:13 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import java.util.Date;

import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * Constraint - Define constraints between tasks
 * @author David Lambert
 */
public class Constraint extends BaseEntity implements Comparable {

    /** Plan. */
    public Plan plan;

    /** Task. */
    public Task task;

    /** Predecessor : the task can start when the predecessor is complete. */
    public Task predecessor;

    /** Date of the constraint - target date or milestone date. */
    Date constraint;

    /** Priority. */
    int priority;

    /** Phase. */
    Phase phase;

    /** Skip the constraint (used during forecast generation. */
    boolean skip;

    /** Skip the predecessor (used during forecast generation. */
    boolean skipPredecessor;

    /** Constructor. */
    public Constraint(Plan plan) {
        this.plan = plan;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "constraint");

        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "constraint", constraint);
            xmlAttribute(xml, transaction, "predecessor", predecessor);
            xmlAttribute(xml, "priority", priority);

            if (phase != null)
                xmlAttribute(xml, "phase", phase.phase);

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
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (tag.equals("constraint")) {
            constraint = xmlInDate(xml, value);
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

        if (tag.equals("predecessor")) {
            if (plan != null && plan.project != null) {
                predecessor = (Task) xmlInEntity(xml, 
                                            transaction, 
                                            value, 
                                            new Task(), 
                                            plan.project.tasks, 
                                            "error:incorrect:predecessor", 
                                            plan);
            }
            return true;
        }

        if (tag.equals("priority")) {
            priority = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("phase")) {
            if (plan != null) {
                phase = (Phase) xmlInEntity(xml, 
                                            transaction, 
                                            value, 
                                            new Phase(plan), 
                                            plan.phases, 
                                            "error:incorrect:phase", 
                                            plan);
            }
            return true;
        }

        return false;
    }

    /** Compares this object with the specified object for order. */
    public int compareTo(Object object) {
        if (this.priority < ((Constraint)object).priority)
            return -1;

        if (this.priority > ((Constraint)object).priority)
            return 1;

        return 0;
    }

    /** Indicates if the element is valid and contains information. */
    boolean isValid() {
        return predecessor != null || constraint != null || priority != 0 || 
            phase != null;
    }
}
