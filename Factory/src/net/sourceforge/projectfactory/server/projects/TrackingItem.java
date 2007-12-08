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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/TrackingItem.java,v $
$Revision: 1.14 $
$Date: 2007/01/27 16:30:46 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import java.util.Calendar;
import java.util.Date;

import net.sourceforge.projectfactory.server.entities.Duration;
import net.sourceforge.projectfactory.server.resources.Resource;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;


/**
 * Tracking item defined for project tracking.
 * @author David Lambert
 */
public class TrackingItem extends Duration {

    /** Date for project tracking. */
    Date dateItem;

    /** Project tracking. */
    protected Tracking tracking;

    /** Task. */
    protected Task task;

    /** Actor assigned to a task. */
    public Resource actor;

    /** On track. */
    boolean onTrack;

    /** Tracking duration. */
    int trackingItemDuration;

    /** Tracking duration type. */
    int trackingItemDurationType;

    /** Constructor. */
    public TrackingItem(Tracking tracking) {
        this.tracking = tracking;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "trackingitem");

        super.xmlOut(xml, transaction, false);

        if (transaction.isDetail() || transaction.isSave()) {
            if (trackingItemDuration > 0) {
                xmlAttribute(xml, "trackingitemduration", trackingItemDuration);
                xmlAttribute(xml, "trackingitemdurationtype", 
                       trackingItemDurationType);
            }
            xmlAttribute(xml, "ontrack", onTrack);
            if (dateItem != null) {
                xmlAttribute(xml, "dateitem", dateItem);
                if (transaction.isDetail()) {
                    Calendar calendarItem = Calendar.getInstance();
                    calendarItem.setTime(dateItem);
                    xmlAttribute(xml, "day", calendarItem.get(Calendar.DAY_OF_WEEK));
                }
            }
            xmlAttribute(xml, transaction, "actor", actor);
            if (task != null) {
                xmlAttribute(xml, "task", task.getName());
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

        if (super.xmlIn(xml, transaction, tag, value)) {
            return true;
        }

        if (tag.equals("ontrack")) {
            onTrack = xmlInBoolean(value);
            return true;
        }

        if (tag.equals("dateitem")) {
            dateItem = xmlInDate(xml, value);
            return true;

        }

        if (tag.equals("trackingitemduration")) {
            trackingItemDuration = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("trackingitemdurationtype")) {
            trackingItemDurationType = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("task")) {
            if (tracking != null && tracking.plan != null && tracking.plan.project != null) {
                task = (Task) xmlInEntity(xml, 
                                            transaction, 
                                            value, 
                                            new Task(), 
                                            tracking.plan.project.tasks, 
                                            "error:incorrect:task", 
                                            tracking);
            }
            return true;
        }

        if (tag.equals("actor")) {
            if (tracking != null) {
                actor = (Resource) xmlInEntity(xml, 
                                            transaction, 
                                            value, 
                                            new Resource(), 
                                            transaction.getServer().actors.actors, 
                                            "error:incorrect:tracking:actor", 
                                            tracking);
            }
            return true;
        }
        return false;
    }
}
