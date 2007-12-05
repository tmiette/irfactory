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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Adjustment.java,v $
$Revision: 1.12 $
$Date: 2007/01/04 15:42:13 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import net.sourceforge.projectfactory.server.entities.Duration;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Adjustment defined for a workload or for reported duration.
 * @author David Lambert
 */
public class Adjustment extends Duration {

    /** Set. */
    public static final int SET = 1;

    /** Add. */
    public static final int ADD = 2;

    /** Subtract. */
    public static final int SUB = 3;

    /** Plan. */
    protected Plan plan;

    /** Task. */
    protected Task task;

    /** Adjustment type. */
    protected int adjustment;

    /** Constructor. */
    public Adjustment(Plan plan) {
        this.plan = plan;
    }

    /** Constructor. */
    public Adjustment(Adjustment adjustment) {
        this.plan = adjustment.plan;
        this.task = adjustment.task;
        this.adjustment = adjustment.adjustment;
        this.duration = adjustment.duration;
        this.durationType = adjustment.durationType;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "adjustment");

        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "adjustment", adjustment);
            if (duration > 0) {
                xmlAttribute(xml, "adjustmentduration", duration);
                xmlAttribute(xml, "adjustmentdurationtype", durationType);
            }
            xmlAttribute(xml, "purpose", purpose);

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
        if (tag.equals("adjustmentduration")) {
            duration = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("adjustmentdurationtype")) {
            durationType = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("purpose")) {
            purpose = value;
            return true;
        }

        if (tag.equals("adjustment")) {
            adjustment = xmlInInt(xml, value);
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

        return false;
    }

    /** Add the adjustment in hours. */
    public int addHours(int hours, int hoursPerDay) {
        if (adjustment == SET) {
            return getHours(hoursPerDay);
        }

        if (adjustment == ADD) {
            return hours + getHours(hoursPerDay);
        }

        if (adjustment == SUB) {
            return hours - getHours(hoursPerDay);
        }

        return hours;
    }

    /** Indicates if the element is valid and contains information. */
    boolean isValid() {
        return adjustment != 0;
    }
}
