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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Workload.java,v $
$Revision: 1.13 $
$Date: 2007/01/27 16:30:46 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import net.sourceforge.projectfactory.server.entities.Duration;
import net.sourceforge.projectfactory.server.entities.DurationCount;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Workload defined for a project task in a plan.
 * @author David Lambert
 */
public class Workload extends Duration {

    /** Plan. */
    protected Plan plan;

    /** Task. */
    protected Task task;

    /** Adjustment. */
    protected Adjustment adjustment;

    /** Constructor. */
    public Workload(Plan plan) {
        this.plan = plan;
        this.adjustment = new Adjustment(plan);
    }

    /** Constructor. */
    public Workload(Workload workload) {
        this.plan = workload.plan;
        this.task = workload.task;
        this.adjustment = new Adjustment(workload.adjustment);
        this.duration = workload.duration;
        this.durationType = workload.durationType;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "workload");

        super.xmlOut(xml, transaction, false);
        xmlAttribute(xml, "adjustment", adjustment.adjustment);
        if (adjustment.duration > 0) {
            xmlAttribute(xml, "adjustmentduration", adjustment.duration);
            xmlAttribute(xml, "adjustmentdurationtype", adjustment.durationType);
        }
        xmlAttribute(xml, "purpose", adjustment.purpose);

        if (task != null) {
            xmlAttribute(xml, transaction, "task", task);
            if (transaction.isDetail())
                xmlAttribute(xml, "type", task.type);
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) {
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

        if (tag.equals("adjustmentduration")) {
            adjustment.duration = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("adjustmentdurationtype")) {
            adjustment.durationType = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("purpose")) {
            adjustment.purpose = value;
            return true;
        }

        if (tag.equals("adjustment")) {
            adjustment.adjustment = xmlInInt(xml, value);
            return true;
        }

        return false;
    }

    /** Returns a number of adjusted hours. */
    int getAdjustedHours(int hoursPerDay) {
        return adjustment.addHours(getHours(hoursPerDay), hoursPerDay);
    }

    /** Adds workload and adjustment to the duration count. */
    void addCount(DurationCount count) {
        switch (adjustment.adjustment) {
        case Adjustment.SET:
            count.add(adjustment);
            break;
        case Adjustment.ADD:
            count.add(this);
            count.add(adjustment);
            break;
        case Adjustment.SUB:
            count.add(this);
            count.sub(adjustment);
            break;
        default:
            count.add(this);
            break;
        }
    }

    /** Sets (changes) the adjustment with another. */
    void setAdjustment(Adjustment adjustment) {
        this.adjustment = adjustment;
    }

    /** Indicates if the element is valid and contains information. */
    boolean isValid() {
        if (duration == 0 && adjustment != null)
            return adjustment.isValid();
        return duration != 0;
    }
}
