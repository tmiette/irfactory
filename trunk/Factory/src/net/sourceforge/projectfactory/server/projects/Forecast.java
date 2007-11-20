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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Forecast.java,v $
$Revision: 1.26 $
$Date: 2007/01/27 17:11:52 $
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
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Forecast calendar generated based on a project plan.
 * Contains daily individual assignments, absences and events.
 * @author David Lambert
 */
public class Forecast extends Entity {

    /** Plan that the forecast refers to */
    public Plan plan;

    /** Forecast date (beginning of the calendar) */
    public Date forecastDate;

    /** Forecast end date (end of the calendar, or estimated project end date) */
    public Date endDate;

    /** Forecast items */
    public List<ForecastItem> items = new ArrayList(100);

    /** Total remaining. */
    private DurationCount totalRemaining = new DurationCount();

    /** Total scheduled. */
    private DurationCount totalScheduled = new DurationCount();

    /** Total completed. */
    private DurationCount totalCompleted = new DurationCount();

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        List<WeeklyTask> weeklyTasks = new ArrayList(40);
        List<WeeklyAssignment> weeklyAssignments = new ArrayList(40);
        List<PhaseTask> phases = new ArrayList(5);

        totalRemaining.reset();
        totalScheduled.reset();
        totalCompleted.reset();

        if (tags)
            xmlStart(xml, "forecast");

        super.xmlOut(xml, transaction, false);

        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, transaction, "plan", plan);
            xmlOut(xml, "forecastdate", forecastDate);
            xmlOut(xml, "enddate", endDate);

            for (ForecastItem item: items) {
                if (transaction.isSave()) {
                    item.xmlOut(xml, transaction, true);
                } else {
                    if (item.type == Item.REPORTED) {
                        item.xmlOutCompleted(xml, transaction, true);
                        totalCompleted.add(item);
                    } else if (item.type != Item.NO) {
                        item.xmlOutCalendar(xml);
                        if (Item.isAlive(item.type))
                            totalRemaining.add(item);
                    }
                }

                if (transaction.isDetail() && item.dayNumber != 0) {
                    Calendar rightNow = Calendar.getInstance();
                    rightNow.setTime(item.dateItem);

                    String week = 
                        "" + rightNow.get(Calendar.YEAR) + " (" + ((rightNow.get(Calendar.WEEK_OF_YEAR) < 
                                                                    10) ? "0" : 
                                                                   "") + 
                        rightNow.get(Calendar.WEEK_OF_YEAR) + ")";

                    if (Item.isAlive(item.type)) {
                        boolean found = false;

                        for (WeeklyTask otherWeeklyTask: weeklyTasks) {
                            if (otherWeeklyTask.week.equals(week) && 
                                (otherWeeklyTask.task == item.task) && 
                                (otherWeeklyTask.actor == item.actor) && 
                                (otherWeeklyTask.anonymous == 
                                 item.anonymous)) {
                                otherWeeklyTask.dateItem = item.dateItem;
                                otherWeeklyTask.day = item.day;
                                otherWeeklyTask.completion = item.completion;
                                otherWeeklyTask.notHit = item.notHit;
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            WeeklyTask weeklyTask = new WeeklyTask(this);
                            weeklyTask.week = week;
                            weeklyTask.dateItem = item.dateItem;
                            weeklyTask.day = item.day;
                            weeklyTask.task = item.task;
                            weeklyTask.type = item.type;
                            weeklyTask.purpose = item.purpose;
                            weeklyTask.completion = item.completion;
                            weeklyTask.notHit = item.notHit;
                            weeklyTask.target = item.target;
                            weeklyTask.actor = item.actor;
                            weeklyTask.anonymous = item.anonymous;
                            weeklyTask.phase = item.phase;
                            weeklyTasks.add(weeklyTask);
                        }
                    }

                    if ((item.actor != null) || (item.anonymous != null)) {
                        boolean found = false;

                        for (WeeklyAssignment otherWeeklyAssignment: 
                             weeklyAssignments) {
                            if (otherWeeklyAssignment.week.equals(week) && 
                                otherWeeklyAssignment.actor == item.actor && 
                                otherWeeklyAssignment.anonymous == 
                                item.anonymous) {
                                otherWeeklyAssignment.dateItem = item.dateItem;
                                otherWeeklyAssignment.day = item.day;
                                if (Item.needsWorkload(item.type)) {
                                    otherWeeklyAssignment.onTask += item.hours;
                                } else if (item.type == Item.ABSENCE || 
                                           item.type == Item.HOLIDAY || 
                                           item.type == Item.EVENT) {
                                    otherWeeklyAssignment.onAbsence += 
                                            item.hours;
                                } else {
                                    otherWeeklyAssignment.noAssignment += 
                                            item.hours;
                                }
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            WeeklyAssignment weeklyAssignment = 
                                new WeeklyAssignment(this);
                            weeklyAssignment.week = week;
                            weeklyAssignment.dateItem = item.dateItem;
                            weeklyAssignment.day = item.day;
                            weeklyAssignment.actor = item.actor;
                            weeklyAssignment.anonymous = item.anonymous;
                            if (Item.needsWorkload(item.type)) {
                                weeklyAssignment.onTask = item.hours;
                            } else if ((item.type == Item.ABSENCE) || 
                                       (item.type == Item.HOLIDAY) || 
                                       (item.type == Item.EVENT)) {
                                weeklyAssignment.onAbsence = item.hours;
                            } else {
                                weeklyAssignment.noAssignment = item.hours;
                            }
                            weeklyAssignments.add(weeklyAssignment);
                        }
                    }

                    if (item.phase != null) {
                        boolean found = false;
                        for (PhaseTask phase: phases) {
                            if (phase.phase.equals(item.phase.phase)) {
                                if (phase.from == null || 
                                    item.dateItem.before(phase.from)) {
                                    phase.from = item.dateItem;
                                }
                                if (phase.to == null || 
                                    item.dateItem.after(phase.to)) {
                                    phase.to = item.dateItem;
                                }
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            PhaseTask phase = new PhaseTask(this);
                            phase.phase = item.phase.phase;
                            phase.from = item.dateItem;
                            phase.to = item.dateItem;
                            phases.add(phase);
                        }
                    }
                }
            }

            if (transaction.isDetail()) {
                if (forecastDate != null && endDate != null) {
                    Calendar from = Calendar.getInstance();
                    Calendar to = Calendar.getInstance();
                    from.setTime(forecastDate);
                    to.setTime(endDate);
                    while (!from.after(to)) {
                        totalScheduled.addDays(1);
                        from.add(Calendar.DATE, 1);
                    }
                }

                for (WeeklyTask wtask: weeklyTasks)
                    wtask.xmlOut(xml, transaction, true);

                for (WeeklyAssignment wtask: weeklyAssignments)
                    wtask.xmlOut(xml, transaction, true);

                for (PhaseTask ptask: phases)
                    ptask.xmlOut(xml, transaction, true);

                Workload workload = new Workload(plan);
                if (plan != null && plan.project != null) {
                    for (Task task: plan.project.tasks) {
                        Date from = null;
                        Date to = null;
                        boolean notHit = false;
                        int hours = 0;
                        String assigned = "";
                        String newassigned = "";
                        boolean alive = false;
                        String phase = "";

                        for (ForecastItem item: items) {
                            if (item.task == task) {
                                if (Item.isAlive(item.type)) {
                                    alive = true;

                                    if (Item.needsWorkload(item.type) && 
                                        from == null)
                                        from = item.dateItem;

                                    to = item.dateItem;
                                }

                                if (item.notHit)
                                    notHit = true;

                                hours += item.hours;

                                if (item.actor != null)
                                    newassigned = item.actor.getName();

                                if (item.anonymous != null)
                                    newassigned = item.anonymous.getName();

                                if (assigned.indexOf(newassigned) < 0)
                                    assigned += 
                                            (((assigned.length() == 0) ? "" : 
                                              ",") + newassigned);

                                if (item.phase != null)
                                    phase = item.phase.phase;
                            }
                        }

                        if (alive) {
                            workload.setHours(hours, plan.hoursPerDay);
                            xmlStart(xml, "sumtask");
                            xmlAttribute(xml, "level", task.getLevel());
                            xmlAttribute(xml, "type", task.type);
                            xmlAttribute(xml, "name", task.getName());
                            workload.xmlOut(xml, transaction, false);
                            xmlAttribute(xml, "from", from);
                            xmlAttribute(xml, "to", to);
                            xmlAttribute(xml, "phase", phase);
                            for (PhaseTask ophase: phases) {
                                if (ophase.phase.equals(phase)) {
                                    xmlAttribute(xml, "endphase", ophase.to);
                                    break;
                                }
                            }
                            xmlAttribute(xml, "nothit", notHit);
                            if (task.gotAction(transaction)) 
                                xmlAttribute(xml, "action", "y");
                            xmlEnd(xml);
                        }
                    }
                }
            }
        }

        weeklyTasks.clear();
        weeklyAssignments.clear();

        // Totals
        if (transaction.isDetail() && plan != null) {
            totalScheduled.xmlOutString(xml, "totalscheduled", 
                                        plan.hoursPerDay);
            totalRemaining.xmlOutString(xml, "totalremaining", 
                                        plan.hoursPerDay);
            totalCompleted.xmlOutString(xml, "totalcompleted", 
                                        plan.hoursPerDay);
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("forecastitem")) 
            return new BaseEntityServerXML(transaction, new ForecastItem(this), items);
        return null;
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) {
            return true;
        }

        if (tag.equals("forecastdate")) {
            forecastDate = xmlInDate(xml, value);
            return true;
        }

        if (tag.equals("enddate")) {
            endDate = xmlInDate(xml, value);
            return true;
        }

        if (tag.equals("plan")) {
            plan = (Plan) xmlInEntity(xml, 
                                        transaction, 
                                        value, 
                                        new Plan(), 
                                        transaction.getServer().projects.plans, 
                                        "error:incorrect:plan:forecast", 
                                        this);
            return true;
        }

        return false;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) {
            return;
        }

        Forecast otherForecast = (Forecast)other;
        super.update(transaction, other);
        this.forecastDate = otherForecast.forecastDate;
        this.endDate = otherForecast.endDate;
        this.plan = otherForecast.plan;
        if (transaction.isReplicate())
            // To update items is only necessary when we want to replicate 
            // information between systems. Because the items can't be changed
            // manually, they are not sent to clients in order to minimize
            // the amount of data.
            update(items, otherForecast.items);
        setName();
    }

    /** Adds prerequisites to the list. */
    public void addPrerequisites(TransactionXML transaction, List<Entity> prerequisites) {
        addPrerequisites(transaction, prerequisites, plan);
    }

    /** Sets forecast name. */
    public void setName() {
        if (plan != null) {
            setName(plan.getName() + ":[" + 
                    XMLWrapper.dsUS.format(forecastDate) + "]");
        }
    }

    /** Provides a summary when the object is displayed in a list. */
    public String getSummary() {
        return "@label:forecast" + 
            ((forecastDate != null) ? (" [" + XMLWrapper.dsUS.format(forecastDate) + 
                                       "]") : "") + 
            (active ? "" : "@label:inactive");
    }

    /** Indicates if the task is referenced in the forecast. */
    boolean references(Task task) {
        for (ForecastItem item: items)
            if (item.task == task)
                return true;

        return false;
    }

    /** Returns the %complete for the task at the specified date. */
    public int getLastScheduledCompletion(Date dateStatus, Task task, 
                                          boolean equal) {
        int last = 0;
        boolean found = false;

        for (ForecastItem item: items) {
            if (item.task == task) {
                if (item.type != Item.REPORTED)
                    found = true;
                if ((item.dateItem != null) && 
                    (item.dateItem.before(dateStatus) || 
                     (equal && item.dateItem.equals(dateStatus)))) {
                    last = item.completion;
                }
            }
        }
        if (!found) {
            for (ForecastItem item: items)
                if (item.task == task && item.type == Item.REPORTED)
                    return 100;
        }

        return last;
    }

    /** Returns the number of scheduled hours for the task at the specified date. */
    public int getLastScheduledHours(Date dateStatus, Task task) {
        int last = 0;

        for (ForecastItem item: items) {
            if (item.task == task && item.dateItem != null && 
                !item.dateItem.after(dateStatus))
                last = item.accumulatedHours + item.hours;

            if (item.task == task && item.dateItem == null)
                last = item.accumulatedHours;
        }

        return last;
    }

    /** Returns the workload for the task. */
    public int getWorkload(Task task) {
        if (plan == null)
            return 0;

        for (Workload workload: plan.workloads)
            if (workload.task == task)
                return workload.duration;

        return 0;
    }

    /** Returns the duration type for the workload for the task. */
    public int getWorkloadDurationType(Task task) {
        if (plan == null)
            return 0;

        for (Workload workload: plan.workloads)
            if (workload.task == task)
                return workload.durationType;

        return 0;
    }

    /** Returns the workload in hours for the task. */
    public int getWorkloadHours(Task task) {
        if (plan == null)
            return 0;

        for (Workload workload: plan.workloads)
            if (workload.task == task)
                return workload.getHours(plan.getHoursPerDay());

        return 0;
    }

    /** Returns the last scheduled hours for the task. */
    public int getLastScheduledHours(Task task) {
        int last = 0;

        for (ForecastItem item: items)
            if (item.task == task)
                last += item.hours;

        return last;
    }

    /** Compares this object with the specified object for order. */
    public int compareTo(Object object) {
        return this.forecastDate.compareTo(((Forecast)object).forecastDate);
    }

    /**
     * Represents phases.
     */
    private class PhaseTask extends ForecastItem {

        /** Phase. */
        protected String phase;

        /** From. */
        protected Date from;

        /** To. */
        protected Date to;

        /** Constructor. */
        public PhaseTask(Forecast forecast) {
            super(forecast);
        }

        /** Writes the object as an XML output. */
        public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                           boolean tags) {
            if (tags)
                xmlStart(xml, "phase");

            super.xmlOut(xml, transaction, false);
            xmlAttribute(xml, "phase", phase);
            xmlAttribute(xml, "from", from);
            xmlAttribute(xml, "to", to);

            if (tags)
                xmlEnd(xml);
        }
    }

    /**
     * Represents an individual task assignment accumulated on a week.
     */
    private class WeeklyTask extends ForecastItem {

        /** Week number. */
        protected String week;

        /** Constructor. */
        public WeeklyTask(Forecast forecast) {
            super(forecast);
        }

        /** Writes the object as an XML output. */
        public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                           boolean tags) {
            if (tags)
                xmlStart(xml, "weeklytask");

            super.xmlOut(xml, transaction, false);
            xmlAttribute(xml, "week", week);

            if (tags)
                xmlEnd(xml);
        }
    }

    /**
     * Represents an individual assignment accumulated on a week.
     */
    private class WeeklyAssignment extends WeeklyTask {

        /** Hours assigned on a task. */
        private int onTask;

        /** Hours on absence. */
        private int onAbsence;

        /** Hours not assigned. */
        private int noAssignment;

        /** Constructor. */
        public WeeklyAssignment(Forecast forecast) {
            super(forecast);
        }

        /** Writes the object as an XML output. */
        public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                           boolean tags) {
            if (tags)
                xmlStart(xml, "weeklyassignment");

            super.xmlOut(xml, transaction, false);

            int total = onTask + onAbsence + noAssignment;

            if (total != 0) {
                xmlAttribute(xml, "ontask", (100 * onTask) / total);
                xmlAttribute(xml, "onabsence", (100 * onAbsence) / total);
                xmlAttribute(xml, "noassignment", 
                             (100 * noAssignment) / total);
                xmlAttribute(xml, "badassignment", 
                             noAssignment > (onTask + onAbsence));
            }

            if (tags)
                xmlEnd(xml);
        }
    }
}
