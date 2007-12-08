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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/StatusItem.java,v $
$Revision: 1.12 $
$Date: 2007/01/28 13:39:58 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Defines an item of the status, including %complete and confidence for an individual task.
 * @author David Lambert
 */
public class StatusItem extends BaseEntity {

    /** Confidence (color : green/yellow/red). */
    int confidence;

    /** % Complete. */
    int complete;

    /** Action status flag. */
    public int actionStatus;

    /** Status. */
    public Status status;

    /** Task. */
    public Task task;

    /** Constructor. */
    public StatusItem(Status status) {
        this.status = status;
    }

    /** Constructor, based on another status item (clone). */
    public StatusItem(Status status, StatusItem statusItem) {
        this.status = status;
        this.task = statusItem.task;
        this.confidence = statusItem.confidence;
        this.complete = statusItem.complete;
        this.actionStatus = statusItem.actionStatus;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        xmlOutForecast(xml, transaction, tags, null);
    }


    /** Writes the object as an XML output.
     *  The output contains information based on the latest forecast. */
    public void xmlOutForecast(WriterXML xml, 
                               TransactionXML transaction, boolean tags, 
                               Forecast latestForecast) {
        if (tags)
            xmlStart(xml, "statusitem");

        if (transaction.isDetail() || transaction.isSave()) {
            if (task != null) {
                xmlAttribute(xml, transaction, "task", task);
                if (transaction.isDetail()) {
                    xmlAttribute(xml, "level", task.getLevel());
                    xmlAttribute(xml, "type", task.type);
                }
            }
            xmlAttribute(xml, "confidence", confidence);
            xmlAttribute(xml, "complete", complete);
            xmlAttribute(xml, "actionstatus", actionStatus);

            if (task != null && transaction.isDetail()) {
                if (latestForecast != null) {
                    int scheduled = 
                        latestForecast.getLastScheduledCompletion(status.getDateStatus(), 
                                                                  task, true);
                    xmlAttribute(xml, "scheduled", scheduled);
                    if (scheduled > 0 && scheduled > complete)
                        xmlAttribute(xml, "nothit", true);
                }
                if (task.gotAction(transaction)) 
                    xmlAttribute(xml, "action", "y");
            }
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (tag.equals("task")) {
            if (status != null && status.plan != null && 
                status.plan.project != null) {
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

        if (tag.equals("confidence")) {
            confidence = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("complete")) {
            complete = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("actionstatus")) {
            actionStatus = xmlInInt(xml, value);
            return true;
        }

        return false;
    }

    /** Indicates if the element is valid and contains information. */
    boolean isValid() {
        return confidence != 0 || complete != 0 || actionStatus != 0;
    }
}
