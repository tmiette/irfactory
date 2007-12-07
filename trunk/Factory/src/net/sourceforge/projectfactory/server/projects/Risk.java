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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Risk.java,v $
$Revision: 1.16 $
$Date: 2007/01/27 16:30:46 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import net.sourceforge.projectfactory.server.entities.Duration;
import net.sourceforge.projectfactory.server.resources.Resource;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;


/**
 * Risk attached to a project plan.
 * @author David Lambert
 */
public class Risk extends Duration {

    /** Project plan. */
    protected Plan plan;

    /** Status. */
    protected Status status;

    /** Label of the risk. */
    String risk;

    /** Owner of the risk. */
    public Resource owner;

    /** Risk probability. */
    int probability;

    /** Task. */
    protected Task task;

    /** Status : refers to TaskStatusType. */
    public int actionStatus;

    /** Constructor, attachement to a plan. */
    public Risk(Plan plan) {
        this.plan = plan;
    }

    /** Constructor, attachement to a status. */
    public Risk(Status status) {
        this.status = status;
    }

    /** Constructor, based on an existing risk. */
    public Risk(Risk risk) {
        this.plan = risk.plan;
        this.status = risk.status;
        this.risk = risk.risk;
        this.owner = risk.owner;
        this.probability = risk.probability;
        this.task = risk.task;
        this.duration = risk.duration;
        this.durationType = risk.durationType;
        this.actionStatus = risk.actionStatus;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "risk");

        super.xmlOut(xml, transaction, false);

        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "risk", risk);
            xmlAttribute(xml, transaction, "owner", owner);
            xmlAttribute(xml, "probability", probability);
            xmlAttribute(xml, transaction, "task", task);
            xmlAttribute(xml, "exposure", getExposure());
            xmlAttribute(xml, "actionstatus", actionStatus);
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

        if (tag.equals("risk")) {
            risk = value;
            return true;
        }

        if (tag.equals("probability")) {
            probability = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("actionstatus")) {
            actionStatus = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("owner")) {
            if (plan != null && plan.project != null) {
                owner = (Resource) xmlInEntity(xml, 
                                            transaction, 
                                            value, 
                                            new Resource(), 
                                            transaction.getServer().actors.actors, 
                                            "error:incorrect:risk:owner", 
                                            plan);
            }
            else if (status != null && status.plan != null) {
                owner = (Resource) xmlInEntity(xml, 
                                            transaction, 
                                            value, 
                                            new Resource(), 
                                            transaction.getServer().actors.actors, 
                                            "error:incorrect:risk:owner", 
                                            status);
            }
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
            if (status != null && status.plan != null && status.plan.project != null) {
                task = (Task) xmlInEntity(xml, 
                                            transaction, 
                                            value, 
                                            new Task(), 
                                            status.plan.project.tasks, 
                                            "error:incorrect:task", 
                                            status);
            }

            return true;
        }

        return false;
    }

    /** Compares two risks for equality. */
    public boolean equals(Object object) {
        if (object == this)
            return true;

        if (object == null)
            return false;

        if (risk == null)
            return false;

        return this.risk.equals(((Risk)object).risk);
    }

    /** Calculates risk exposure (in hours). */
    public int getExposure() {
        if (probability > 0 && duration > 0 && plan != null && 
            plan.project != null) {
            return (probability * getHours(plan.hoursPerDay)) / 100;
        } else if (probability > 0 && duration > 0 && status != null && 
                   status.plan != null && status.plan.project != null) {
            return (probability * getHours(status.plan.hoursPerDay)) / 100;
        } else
            return 0;
    }
}
