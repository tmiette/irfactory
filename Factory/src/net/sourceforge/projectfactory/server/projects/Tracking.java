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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Tracking.java,v $
$Revision: 1.27 $
$Date: 2007/02/05 22:16:09 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sourceforge.projectfactory.server.entities.DurationCount;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.resources.Resource;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;
import net.sourceforge.projectfactory.xml.server.TransactionXML;


/**
 * Project tracking.
 * @author David Lambert
 */
public class Tracking extends Entity {

    /** Plan. */
    public Plan plan;

    /** From date used for the tracking. */
    private Date dateTrackingFrom;

    /** To date used for the tracking. */
    Date dateTrackingTo;

    /** List of tracking items. */
    public List<TrackingItem> items = new ArrayList(20);

    /** List of tracking items. */
    public List<TrackingItem> unscheduledItems = new ArrayList(10);

    /** Total scheduled. */
    private DurationCount totalScheduled = new DurationCount();

    /** Total unscheduled. */
    private DurationCount totalUnscheduled = new DurationCount();

    /** Totals. */
    private DurationCount totals = new DurationCount();

    /** Cumulated. */
    private DurationCount cumulated = new DurationCount();

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {

        totalScheduled.reset();
        totalUnscheduled.reset();
        totals.reset();
        cumulated.reset();

        if (tags)
            xmlStart(xml, "tracking");

        super.xmlOut(xml, transaction, false);
        Forecast latestForecast = null;

        xmlOut(xml, "datetrackingfrom", dateTrackingFrom);
        xmlOut(xml, "datetrackingto", dateTrackingTo);

        if ((plan != null) && (dateTrackingTo != null) && 
            ((transaction.isDetail()) || (transaction.isSave()))) {
            xmlOut(xml, transaction, "plan", plan);

            if (transaction.isDetail()) {
                xmlOutTasks(xml, transaction);
                plan.xmlOutMembers(xml, transaction);
            }

            for (TrackingItem tracking: unscheduledItems) {
                xmlStart(xml, "unscheduledtrackingitem");
                tracking.xmlOut(xml, transaction, false);
                xmlEnd(xml);
                totalUnscheduled.add(tracking.trackingItemDuration, 
                                     tracking.trackingItemDurationType);
            }

            latestForecast = 
                    plan.project.getLatestForecast(transaction, plan, dateTrackingTo, 
                                                   true);
            if (latestForecast != null) {
                for (ForecastItem item:latestForecast.items) {
                    if (item.dateItem == null)
                        continue;
                    if (dateTrackingFrom != null && 
                        dateTrackingFrom.after(item.dateItem))
                        continue;
                    if (dateTrackingTo != null && 
                        dateTrackingTo.before(item.dateItem))
                        continue;
                    if ((item.type != Item.NO) && 
                        (item.type != Item.REPORTED) && 
                        transaction.isDetail()) {
                        item.xmlOutCalendar(xml);
                    }

                    if (item.task != null && item.actor != null && 
                        Item.needsWorkload(item.type)) {
                        xmlStart(xml, "trackingitem");

                        boolean found = false;
                        for (TrackingItem tracking: items) {
                            if (tracking.dateItem.equals(item.dateItem) && 
                                tracking.task.equals(item.task) && 
                                tracking.actor.equals(item.actor)) {
                                found = true;
                                tracking.set(item.duration, item.durationType);
                                tracking.xmlOut(xml, transaction, false);
                                if (tracking.onTrack)
                                    totalScheduled.add(item);
                                else
                                    totalScheduled.add(tracking.trackingItemDuration, 
                                                       tracking.trackingItemDurationType);
                                break;
                            }
                        }
                        if (!found) {
                            if (item.duration > 0) {
                                xmlAttribute(xml, "duration", item.duration);
                                xmlAttribute(xml, "durationtype", item.durationType);
                            }
                            xmlAttribute(xml, "dateitem", item.dateItem);
                            xmlAttribute(xml, transaction, "task", item.task);
                            xmlAttribute(xml, "type", item.type);
                            xmlAttribute(xml, "day", item.day);
                            xmlAttribute(xml, "daynumber", item.dayNumber);
                            xmlAttribute(xml, transaction, "actor", item.actor);
                        }

                        xmlEnd(xml);
                    }
                }
            }

            // Calculate totals
            if (transaction.isDetail()) {
                List<TrackingCount> countActor = new ArrayList(40);
                List<TrackingCount> countTask = new ArrayList(40);

                for (TrackingItem tracking: items) {
                    // Add count per actor
                    boolean found = false;
                    for (TrackingCount counter: countActor) {
                        if (counter.actor == tracking.actor) {
                            if (tracking.onTrack)
                                counter.add(tracking);
                            else
                                counter.add(tracking.trackingItemDuration, 
                                            tracking.trackingItemDurationType);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        TrackingCount counter = new TrackingCount();
                        counter.actor = tracking.actor;
                        if (tracking.onTrack)
                            counter.add(tracking);
                        else
                            counter.add(tracking.trackingItemDuration, 
                                        tracking.trackingItemDurationType);
                        countActor.add(counter);
                    }

                    // Add count per task
                    found = false;
                    for (TrackingCount counter: countTask) {
                        if (counter.task == tracking.task) {
                            if (tracking.onTrack)
                                counter.add(tracking);
                            else
                                counter.add(tracking.trackingItemDuration, 
                                            tracking.trackingItemDurationType);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        TrackingCount counter = new TrackingCount();
                        counter.task = tracking.task;
                        if (tracking.onTrack)
                            counter.add(tracking);
                        else
                            counter.add(tracking.trackingItemDuration, 
                                        tracking.trackingItemDurationType);
                        countTask.add(counter);
                    }
                }
                for (TrackingItem tracking: unscheduledItems) {
                    // Add count per actor
                    boolean found = false;
                    for (TrackingCount counter: countActor) {
                        if (counter.actor == tracking.actor) {
                            counter.add(tracking.trackingItemDuration, 
                                        tracking.trackingItemDurationType);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        TrackingCount counter = new TrackingCount();
                        counter.actor = tracking.actor;
                        counter.add(tracking.trackingItemDuration, 
                                    tracking.trackingItemDurationType);
                        countActor.add(counter);
                    }

                    // Add count per task
                    found = false;
                    for (TrackingCount counter: countTask) {
                        if (counter.task == tracking.task) {
                            counter.add(tracking.trackingItemDuration, 
                                        tracking.trackingItemDurationType);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        TrackingCount counter = new TrackingCount();
                        counter.task = tracking.task;
                        counter.add(tracking.trackingItemDuration, 
                                    tracking.trackingItemDurationType);
                        countTask.add(counter);
                    }
                }

                for (TrackingCount counter: countActor) {
                    counter.normalize(plan.hoursPerDay);
                    counter.xmlOut(xml, transaction, "totalactor");
                }
                for (TrackingCount counter: countTask) {
                    counter.normalize(plan.hoursPerDay);
                    counter.xmlOut(xml, transaction, "totaltask");
                    totals.add(counter);
                }

                // Calculate cumulated
                if (plan != null && plan.project != null) {
                    countActor.clear();
                    countTask.clear();

                    for (Tracking otherTracking: transaction.getServer().projects.trackings) {
                        if (otherTracking.plan != null && plan != null && 
                            otherTracking.plan.project != plan.project)
                            continue;
                        if (otherTracking.dateTrackingTo.after(dateTrackingTo))
                            continue;

                        for (TrackingItem tracking: otherTracking.items) {
                            // Add count per actor
                            boolean found = false;
                            for (TrackingCount counter: countActor) {
                                if (counter.actor == tracking.actor) {
                                    if (tracking.onTrack)
                                        counter.add(tracking);
                                    else
                                        counter.add(tracking.trackingItemDuration, 
                                                    tracking.trackingItemDurationType);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                TrackingCount counter = new TrackingCount();
                                counter.actor = tracking.actor;
                                if (tracking.onTrack)
                                    counter.add(tracking);
                                else
                                    counter.add(tracking.trackingItemDuration, 
                                                tracking.trackingItemDurationType);
                                countActor.add(counter);
                            }

                            // Add count per task
                            found = false;
                            for (TrackingCount counter: countTask) {
                                if (counter.task == tracking.task) {
                                    if (tracking.onTrack)
                                        counter.add(tracking);
                                    else
                                        counter.add(tracking.trackingItemDuration, 
                                                    tracking.trackingItemDurationType);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                TrackingCount counter = new TrackingCount();
                                counter.task = tracking.task;
                                if (tracking.onTrack)
                                    counter.add(tracking);
                                else
                                    counter.add(tracking.trackingItemDuration, 
                                                tracking.trackingItemDurationType);
                                countTask.add(counter);
                            }
                        }
                        for (TrackingItem tracking: otherTracking.unscheduledItems) {
                            // Add count per actor
                            boolean found = false;
                            for (TrackingCount counter: countActor) {
                                if (counter.actor == tracking.actor) {
                                    counter.add(tracking.trackingItemDuration, 
                                                tracking.trackingItemDurationType);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                TrackingCount counter = new TrackingCount();
                                counter.actor = tracking.actor;
                                counter.add(tracking.trackingItemDuration, 
                                            tracking.trackingItemDurationType);
                                countActor.add(counter);
                            }

                            // Add count per task
                            found = false;
                            for (TrackingCount counter: countTask) {
                                if (counter.task == tracking.task) {
                                    counter.add(tracking.trackingItemDuration, 
                                                tracking.trackingItemDurationType);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                TrackingCount counter = new TrackingCount();
                                counter.task = tracking.task;
                                counter.add(tracking.trackingItemDuration, 
                                            tracking.trackingItemDurationType);
                                countTask.add(counter);
                            }
                        }
                    }
                    for (TrackingCount counter: countActor) {
                        counter.normalize(plan.hoursPerDay);
                        counter.xmlOut(xml, transaction, "cumulatedactor");
                    }
                    for (TrackingCount counter: countTask) {
                        counter.normalize(plan.hoursPerDay);
                        counter.xmlOut(xml, transaction, "cumulatedtask");
                        cumulated.add(counter);
                    }
                }
            }
        }

        // Totals
        if (transaction.isDetail()) {
            totalScheduled.xmlOutString(xml, "totalscheduled", 
                                        plan != null ? plan.hoursPerDay : 0);
            totalUnscheduled.xmlOutString(xml, "totalunscheduled", 
                                          plan != null ? plan.hoursPerDay : 0);
            totals.xmlOutString(xml, "totals", 
                                plan != null ? plan.hoursPerDay : 0);
            cumulated.xmlOutString(xml, "cumulated", 
                                   plan != null ? plan.hoursPerDay : 0);
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Writes tasks as an XML output. */
    private void xmlOutTasks(WriterXML xml, 
                             TransactionXML transaction) {
        for (Task item: plan.project.tasks) {
            if (Item.needsWorkload(item.type)) {
                xmlStart(xml, "task");
                xmlAttribute(xml, transaction, "task", item);
                xmlAttribute(xml, "type", item.type);
                xmlEnd(xml);
            }
        }
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("trackingitem")) 
            return new BaseEntityServerXML(transaction, new TrackingItem(this), items);
        if (tag.equals("unscheduledtrackingitem")) 
            return new BaseEntityServerXML(transaction, new TrackingItem(this), unscheduledItems);
        return null;
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) 
            return true;

        if (tag.equals("plan")) {
            plan = (Plan) xmlInEntity(xml, 
                                        transaction, 
                                        value, 
                                        new Plan(), 
                                        transaction.getServer().projects.plans, 
                                        "error:incorrect:plan", 
                                        this);
            return true;
        }

        if (tag.equals("datetrackingfrom")) {
            dateTrackingFrom = xmlInDate(xml, value);
            return true;
        }

        if (tag.equals("datetrackingto")) {
            dateTrackingTo = xmlInDate(xml, value);
            return true;
        }

        return false;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) {
            return;
        }

        Tracking otherTracking = (Tracking)other;
        super.update(transaction, other);
        this.plan = otherTracking.plan;
        this.dateTrackingFrom = otherTracking.dateTrackingFrom;
        this.dateTrackingTo = otherTracking.dateTrackingTo;
        update(items, otherTracking.items);
        update(unscheduledItems, otherTracking.unscheduledItems);
    }

    /** Adds prerequisites to the list. */
    public void addPrerequisites(TransactionXML transaction, List<Entity> prerequisites) {
        addPrerequisites(transaction, prerequisites, plan);
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);
        dateTrackingTo = XMLWrapper.systemDate();
        if (plan != null && plan.project != null) {
            Tracking last = 
                plan.project.getLatestTracking(transaction, plan, dateTrackingTo, 
                                               true);
            if (last != null) {
                plan = plan.project.getLatestActivePlan(transaction);
                dateTrackingFrom = last.dateTrackingTo;
                Calendar calendarItem = Calendar.getInstance();
                calendarItem.setTime(dateTrackingFrom);
                calendarItem.add(Calendar.DAY_OF_MONTH, 1);
                dateTrackingFrom = calendarItem.getTime();
            }
        }
        if (dateTrackingFrom == null && plan != null)
            dateTrackingFrom = plan.getForecastDate();
        setName();
    }

    /** Sets tracking name. */
    public void setName() {
        if (plan != null)
            setName(plan.getName() + ":[" + 
                    XMLWrapper.dsUS.format(dateTrackingTo) + "]");
    }

    /** Provides a summary when the object is displayed in a list. */
    public String getSummary() {
        return "@label:tracking" + 
            ((dateTrackingTo != null) ? (" [" + XMLWrapper.dsUS.format(dateTrackingTo) + 
                                         "]") : "") + 
            (active ? "" : "@label:inactive");
    }

    /** Validates the object before any save or update. */
    public boolean xmlValidate(WriterXML xml, TransactionXML transaction, 
                            List list) {
        if(super.xmlValidate(xml, transaction, list))
            return true;

        /* Only one status on the same project can be active */
        if (isActive()) {
            for (Tracking otherTracking: transaction.getServer().projects.trackings) {
                if (otherTracking.plan != null &&
                    otherTracking.plan.project != null &&
                    otherTracking != this && 
                    otherTracking.plan.project == plan.project) {
                    otherTracking.setInactive(transaction);
                }
            }
        }
        setName();
        
        return false;
    }

    /** Indicates if the task is referenced in the tracking. */
    boolean references(Task task) {
        for (TrackingItem tracking: items) {
            if (tracking.task == task)
                return true;
        }

        for (TrackingItem tracking: unscheduledItems) {
            if (tracking.task == task)
                return true;
        }

        return false;
    }

    /** Compares this object with the specified object for order. */
    public int compareTo(Object object) {
        if (dateTrackingTo != null)
            return dateTrackingTo.compareTo(((Tracking)object).dateTrackingTo);
        return super.compareTo(object);
    }

    /**
	 * Represents a counter of tracking items per task or actor
     */
    private class TrackingCount extends DurationCount {

        /** Task. */
        protected Task task;

        /** Actor. */
        protected Resource actor;

        /** Writes the object as an XML output. */
        public void xmlOut(WriterXML xml, TransactionXML transaction, 
                           String tag) {
            xmlStart(xml, tag);
            if (duration > 0) {
                xmlAttribute(xml, "duration", duration);
                xmlAttribute(xml, "durationtype", durationType);
            }
            xmlAttribute(xml, transaction, "task", task);
            if (task != null)
                xmlAttribute(xml, "type", task.type);
            xmlAttribute(xml, transaction, "actor", actor);
            xmlEnd(xml);
        }
    }
}
