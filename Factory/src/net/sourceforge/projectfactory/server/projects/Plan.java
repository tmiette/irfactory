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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Plan.java,v $
$Revision: 1.37 $
$Date: 2007/02/05 22:16:09 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.sourceforge.projectfactory.server.entities.Duration;
import net.sourceforge.projectfactory.server.entities.DurationCount;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.resources.Absence;
import net.sourceforge.projectfactory.server.resources.Holiday;
import net.sourceforge.projectfactory.server.resources.Member;
import net.sourceforge.projectfactory.server.resources.Resource;
import net.sourceforge.projectfactory.server.resources.Team;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;
import net.sourceforge.projectfactory.xml.server.TransactionXML;


/**
 * Defines a project plan, which is version-based. Includes workloads, constraints, assignments but also anonymous and events.
 * @author David Lambert
 */
public class Plan extends Entity {

    /** Reference to the project. */
    public Project project;

    /** Version number. */
    protected int version;

    /** Date used for forecast calendar. */
    private Date forecastDate;

    /** Generate forecast calendar (during save). */
    private boolean generateForecast;

    /** Generate a remaining based on latest forecast. */
    private boolean useRemaining;

    /** Number of hours per day, used for forecast calendar generation. */
    int hoursPerDay;

    /** List of assignments. */
    public List<Assignment> assignments = new ArrayList(20);

    /** List of anonymities. */
    public List<Anonymous> anonymities = new ArrayList(5);

    /** List of events. */
    public List<Event> events = new ArrayList(20);

    /** List of constraints. */
    public List<Constraint> constraints = new ArrayList(20);

    /** List of workload. */
    public List<Workload> workloads = new ArrayList(20);

    /** List of adjustments. */
    public List<Adjustment> adjustments = new ArrayList(20);

    /** List of phases. */
    public List<Phase> phases = new ArrayList(5);

    /** List of risks. */
    public List<Risk> risks = new ArrayList(10);

    /** Total workload. */
    private DurationCount totalWorkload = new DurationCount();

    /** Total completed. */
    private DurationCount totalCompleted = new DurationCount();

    /** Total remaining. */
    private DurationCount totalRemaining = new DurationCount();

    /** Total risk exposure. */
    private DurationCount totalExposure = new DurationCount();

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        boolean found;

        totalWorkload.reset();
        totalCompleted.reset();
        totalRemaining.reset();
        totalExposure.reset();

        if (tags)
            xmlStart(xml, "plan");

        super.xmlOut(xml, transaction, false);

        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "version", version);
            xmlOut(xml, transaction, "project", project);
            xmlOut(xml, "generateforecast", generateForecast);
            xmlOut(xml, "useremaining", useRemaining);
            xmlOut(xml, "forecastdate", forecastDate);
            xmlOut(xml, "hoursperday", hoursPerDay);

            xmlOutAnonymities(xml, transaction);
            xmlOutPhases(xml, transaction);
            xmlOutRisk(xml, transaction);
            xmlOutEvents(xml, transaction);

            if (project != null) {
                Forecast latestForecast = 
                    (forecastDate != null) ? project.getLatestForecast(transaction, 
                                                                       forecastDate, 
                                                                       false) : 
                    null;

                for (Task task: project.tasks) {

                    int remainingHours = 0;
                    int scheduledHours = 0;
                    Workload completed = new Workload(this);
                    Workload remaining = new Workload(this);

                    if (Item.needsWorkload(task.type)) {

                        if (transaction.isDetail()) {
                            if (latestForecast != null) {
                                scheduledHours = 
                                        latestForecast.getLastScheduledHours(forecastDate, 
                                                                             task);
                                completed.setHours(scheduledHours, 
                                                   hoursPerDay);
                            }
                        }

                        for (Workload workload: workloads) {
                            if (workload.task == task) {
                                remainingHours = 
                                        workload.getAdjustedHours(hoursPerDay);
                                break;
                            }
                        }

                        found = false;
                        for (Adjustment adjustment: adjustments) {
                            if (adjustment.task == task && 
                                adjustment.isValid()) {
                                found = true;

                                xmlStart(xml, "adjustment");
                                adjustment.xmlOut(xml, transaction, false);
                                scheduledHours = 
                                        adjustment.addHours(scheduledHours, 
                                                            hoursPerDay);
                                completed.adjustment = adjustment;
                                break;
                            }
                        }

                        if (!found && transaction.isDetail()) {
                            xmlStart(xml, "adjustment");
                            xmlAttribute(xml, "task", task.getName());
                            xmlAttribute(xml, "type", task.type);
                        }

                        if (transaction.isDetail()) {
                            if (completed.duration > 0) {
                                xmlAttribute(xml, "duration", completed.duration);
                                xmlAttribute(xml, "durationtype", 
                                       completed.durationType);
                            }

                            completed.addCount(totalCompleted);
                            remaining.setHours(remainingHours - scheduledHours, 
                                               hoursPerDay);
                            if (remaining.duration > 0) {
                                xmlAttribute(xml, "remaining", remaining.duration);
                                xmlAttribute(xml, "remainingdurationtype", 
                                       remaining.durationType);
                                remaining.addCount(totalRemaining);
                            }
                        }

                        if (found || transaction.isDetail())
                            xmlEnd(xml);
                    }

                    xmlOutWorkload(xml, transaction, task);
                    xmlOutAssigments(xml, transaction, task);
                    xmlOutConstraints(xml, transaction, task);
                }

                if (transaction.isDetail()) {
                    xmlOutMembers(xml, transaction);

                    Forecast latestAttachedForecast = 
                        (forecastDate != null) ? project.getLatestForecast(transaction, 
                                                                           this, 
                                                                           forecastDate, 
                                                                           true) : 
                        null;

                    if (latestAttachedForecast == null)
                        latestAttachedForecast = latestForecast;

                    if (latestAttachedForecast != null) {
                        for (ForecastItem item: latestAttachedForecast.items)
                            if (item.type != Item.REPORTED && 
                                item.type != Item.NO) {
                                item.xmlOutCalendar(xml);
                            }
                    }
                }
            }

            // Totals
            if (transaction.isDetail()) {
                totalWorkload.xmlOutString(xml, "totalworkload", hoursPerDay);
                totalCompleted.xmlOutString(xml, "totalcompleted", 
                                            hoursPerDay);
                totalRemaining.xmlOutString(xml, "totalremaining", 
                                            hoursPerDay);
                totalExposure.xmlOutString(xml, "totalexposure", hoursPerDay);
            }
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Writes constraints as an XML output. */
    private void xmlOutConstraints(WriterXML xml, 
                                   TransactionXML transaction, Task task) {
        boolean found = false;

        for (Constraint constraint: constraints) {
            if (constraint.task == task && constraint.isValid()) {
                found = true;
                if (Item.isAlive(task.type))
                    constraint.xmlOut(xml, transaction, true);
            }
        }

        if (!found && Item.isAlive(task.type) && (transaction.isDetail())) {
            xmlStart(xml, "constraint");
            xmlAttribute(xml, "task", task.getName());
            if (transaction.isDetail())
                xmlAttribute(xml, "type", task.type);
            xmlEnd(xml);
        }
    }

    /** Writes assignments as an XML output. */
    private void xmlOutAssigments(WriterXML xml, 
                                  TransactionXML transaction, Task task) {
        boolean found;
        found = false;

        for (Assignment assignment: assignments) {
            if (assignment.task == task && assignment.isValid()) {
                found = true;
                if ((assignment.task != null) && 
                    Item.needsWorkload(task.type)) {
                    assignment.xmlOut(xml, transaction, true);
                }
            }
        }

        if (!found && Item.needsWorkload(task.type) && 
            (transaction.isDetail())) {
            xmlStart(xml, "assignment");
            xmlAttribute(xml, "task", task.getName());
            if (transaction.isDetail())
                xmlAttribute(xml, "type", task.type);
            xmlEnd(xml);
        }
    }

    /** Writes workload as an XML output. */
    private void xmlOutWorkload(WriterXML xml, 
                                TransactionXML transaction, Task task) {
        boolean found;
        found = false;

        for (Workload workload: workloads) {
            if (workload.task == task && workload.isValid()) {
                found = true;
                workload.xmlOut(xml, transaction, true);
                workload.addCount(totalWorkload);
                break;
            }
        }

        if (!found && Item.needsWorkload(task.type) && 
            (transaction.isDetail())) {
            xmlStart(xml, "workload");
            xmlAttribute(xml, "task", task.getName());
            if (transaction.isDetail())
                xmlAttribute(xml, "type", task.type);
            xmlEnd(xml);
        }
    }

    /** Writes phases as an XML output. */
    private void xmlOutPhases(WriterXML xml, 
                              TransactionXML transaction) {
        for (Phase phase: phases)
            phase.xmlOut(xml, transaction, true);
    }

    /** Writes anonimities as an XML output. */
    private void xmlOutAnonymities(WriterXML xml, 
                                   TransactionXML transaction) {
        for (Anonymous anonymous: anonymities)
            anonymous.xmlOut(xml, transaction, true);
    }

    /** Writes events as an XML output. */
    private void xmlOutEvents(WriterXML xml, 
                              TransactionXML transaction) {
        for (Event event: events) {
            event.xmlOut(xml, transaction, true);

            if (transaction.isDetail()) {
                if (event.event != null && forecastDate != null && 
                    event.event.before(forecastDate))
                    continue;
                if (event.actor != null) {
                    xmlCalendar(xml, event.event, event.purpose, Item.EVENT, 
                                event.actor.getName(), event.duration, 
                                event.durationType, 100);
                } else {
                    xmlCalendar(xml, event.event, event.purpose, Item.EVENT, 
                                "", event.duration, event.durationType, 100);
                }
            }
        }
    }

    /** Writes risks as an XML output. */
    private void xmlOutRisk(WriterXML xml, TransactionXML transaction) {
        for (Risk risk: risks) {
            risk.xmlOut(xml, transaction, true);
            totalExposure.addHours(risk.getExposure());
        }
    }

    /** Writes members as an XML output. */
    void xmlOutMembers(WriterXML xml, TransactionXML transaction) {
        for (ProjectTeam projectTeam:project.teams) {
            Team team = projectTeam.team;
            if (team != null) {
                for (Member member: team.members) {
                    boolean activated = true;

                    if (project.begin != null && member.to != null && 
                        member.to.before(project.begin)) {
                        activated = false;
                    }
                    if (activated) {
                        member.xmlOut(xml, transaction, true, forecastDate);
                        if (member.actor != null) {
                            for (Absence absence: member.actor.absences) {
                                xmlStart(xml, "absence");
                                xmlAttribute(xml, "actor", member.actor.getName());
                                xmlAttribute(xml, "absence", absence.absence);
                                xmlAttribute(xml, "duration", absence.duration);
                                xmlAttribute(xml, "durationtype", absence.durationType);
                                xmlAttribute(xml, "purpose", absence.purpose);
                                xmlEnd(xml);
                                if (transaction.isDetail()) {
                                    if (absence.absence != null && 
                                        forecastDate != null && 
                                        absence.absence.before(forecastDate))
                                        continue;
                                    xmlCalendar(xml, absence.absence, 
                                                absence.purpose, Item.ABSENCE, 
                                                member.actor.getName(), 
                                                absence.duration, 
                                                absence.durationType, 100);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("workload")) 
            return new BaseEntityServerXML(transaction, new Workload(this), workloads);
        if (tag.equals("adjustment")) 
            return new BaseEntityServerXML(transaction, new Adjustment(this), adjustments);
        if (tag.equals("assignment")) 
            return new BaseEntityServerXML(transaction, new Assignment(this), assignments);
        if (tag.equals("constraint")) 
            return new BaseEntityServerXML(transaction, new Constraint(this), constraints);
        if (tag.equals("anonymous")) 
            return new BaseEntityServerXML(transaction, new Anonymous(this), anonymities);
        if (tag.equals("event")) 
            return new BaseEntityServerXML(transaction, new Event(this), events);
        if (tag.equals("risk")) 
            return new BaseEntityServerXML(transaction, new Risk(this), risks);
        if (tag.equals("phase")) 
            return new BaseEntityServerXML(transaction, new Phase(this), phases);
        return null;
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) {
            return true;
        }

        if (tag.equals("version")) {
            version = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("forecastdate")) {
            forecastDate = xmlInDate(xml, value);
            return true;
        }

        if (tag.equals("generateforecast")) {
            generateForecast = xmlInBoolean(value);
            return true;
        }

        if (tag.equals("useremaining")) {
            useRemaining = xmlInBoolean(value);
            return true;
        }

        if (tag.equals("hoursperday")) {
            hoursPerDay = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("project")) {
            project = (Project) xmlInEntity(xml, 
                                        transaction, 
                                        value, 
                                        new Project(), 
                                        transaction.getServer().projects.projects, 
                                        "error:incorrect:project", 
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

        Plan otherPlan = (Plan)other;
        super.update(transaction, other);
        this.version = otherPlan.version;
        this.project = otherPlan.project;
        this.forecastDate = otherPlan.forecastDate;
        this.hoursPerDay = otherPlan.hoursPerDay;
        this.generateForecast = otherPlan.generateForecast;
        this.useRemaining = otherPlan.useRemaining;
        update(workloads, otherPlan.workloads);
        update(adjustments, otherPlan.adjustments);
        update(assignments, otherPlan.assignments);
        update(constraints, otherPlan.constraints);
        update(anonymities, otherPlan.anonymities);
        update(phases, otherPlan.phases);
        update(events, otherPlan.events);
        update(risks, otherPlan.risks);
        setName();

        if (transaction.isUpdate()) {
            /* Re-attach forecasts */
            for (Forecast forecast:transaction.getServer().projects.forecasts) {
                if (forecast.plan == otherPlan)
                    forecast.plan = this;
            }

            /* Re-attach statuses */
             for (Status status: transaction.getServer().projects.statuses) {
                if (status.plan == otherPlan)
                    status.plan = this;
            }

            /* Re-attach trackings */
             for (Tracking tracking: transaction.getServer().projects.trackings) {
                if (tracking.plan == otherPlan)
                    tracking.plan = this;
            }
        }
    }

    /** Adds prerequisites to the list. */
    public void addPrerequisites(TransactionXML transaction, List<Entity> prerequisites) {
        addPrerequisites(transaction, prerequisites, project);
        for (Anonymous anonymous: anonymities) 
            addPrerequisites(transaction, prerequisites, anonymous.location);
    }

    /** Sets plan name. */
    public void setName() {
        if (project != null) {
            setName(project.getName() + ":" + version);
        }
    }

    /** Provides a summary when the object is displayed in a list. */
    public String getSummary() {
        return "@label:plan " + version + 
            ((forecastDate != null) ? (" [" + XMLWrapper.dsUS.format(forecastDate) + 
                                       "]") : "") + 
            (active ? "" : "@label:inactive");
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);

        if (project != null) {
            version = 1;
            hoursPerDay = 8;
            useRemaining = true;

            for (Plan otherPlan: transaction.getServer().projects.plans) {
                if (otherPlan.project == project) {
                    update(transaction, otherPlan);
                    reset();
                    ++version;
                }
            }

            forecastDate = XMLWrapper.systemDate();

            // Set adjustments from latest status
            Status latestStatus = 
                project.getLatestStatus(transaction, forecastDate, true);

            if (latestStatus != null) {
                update(workloads, latestStatus.workloads);
                update(adjustments, latestStatus.adjustments);

                // Copy risk items from other status attached to the same plan
                risks.clear();
                for (Risk risk: latestStatus.risks)
                    risks.add(new Risk(risk));
            }

            generateForecast = false;
            setName();
        }
    }

    /** Validates the object before any save or update. */
    public boolean xmlValidate(WriterXML xml, TransactionXML transaction, 
                            List list) {
        int i;
        List list1;

        if(super.xmlValidate(xml, transaction, list))
            return true;

        if (project == null)
            return true;

        // Control if all the task has a workload
        for (Workload workload: workloads) {
            if (workload.task != null) {
                if (Item.needsWorkload(workload.task.type) && 
                    ((workload.duration == 0) || 
                     (workload.durationType == 0))) {
                    if ((workload.adjustment.adjustment == 0) || 
                        (workload.adjustment.duration == 0) || 
                        (workload.adjustment.durationType == 0)) {
                        xmlWarning(xml, "warning:noworkload", 
                                   workload.task.getName());
                    }
                }
            }
        }

        /* Control if the actors are all assigned */
        for (i = 0; i < project.teams.size(); i++) {
            Team team = ((ProjectTeam)project.teams.get(i)).team;

            if (team != null) {
                for (Member member: team.members) {
                    boolean activated = true;
                    if ((project.begin != null) && (member.to != null)) {
                        if (member.to.before(project.begin)) {
                            activated = false;
                        }
                    }

                    if (activated) {
                        boolean found = false;
                        for (Assignment assignment: assignments) {
                            if (assignment.actor == member.actor) {
                                found = true;
                                break;
                            }
                        }
                        if (!found)
                            xmlWarning(xml, "warning:actor:notassigned", 
                                       member.actor.getName());
                    }
                }
            }
        }

        /* Control if any forecast has been created for another plan */
        list1 = transaction.getServer().projects.forecasts;

        for (i = 0; i < list1.size(); i++) {
            Forecast forecast = (Forecast)list1.get(i);

            if ((forecast.plan == this) && 
                (forecast.plan.project == project) && 
                forecast.forecastDate.equals(forecastDate) && 
                (forecast.plan.version != version)) {
                xmlError(xml, "error:anotherforecast", "");
                return true;
            }
        }

        if (generateForecast) {
            /* Control begin date */
            if (forecastDate == null) {
                xmlError(xml, "error:required:begindate", "");
                return true;
            }

            if (hoursPerDay == 0) {
                xmlError(xml, "error:required:hoursperday", "");
                return true;
            }

            if (!generateForecast(xml, transaction))
                return true;
        }

        /* Only one plan on the same project can be active */
        if (isActive()) {
            list1 = transaction.getServer().projects.plans;
            for (i = 0; i < list1.size(); i++) {
                Plan otherPlan = (Plan)list1.get(i);
                if (!otherPlan.equals(this) && 
					otherPlan.project != null &&
                    otherPlan.project.equals(project))
                    otherPlan.setInactive(transaction);
            }
        }

        // Remove all empty element
        for (list1 = assignments, i = list1.size() - 1; i >= 0; i--) {
            Assignment assignement = (Assignment)list1.get(i);
            if (!assignement.isValid())
                list1.remove(assignement);
        }

        for (list1 = workloads, i = list1.size() - 1; i >= 0; i--) {
            Workload workload = (Workload)list1.get(i);
            if (!workload.isValid())
                list1.remove(workload);
        }

        for (list1 = constraints, i = list1.size() - 1; i >= 0; i--) {
            Constraint constraint = (Constraint)list1.get(i);
            if (!constraint.isValid())
                list1.remove(constraint);
        }

        for (list1 = adjustments, i = list1.size() - 1; i >= 0; i--) {
            Adjustment adjustment = (Adjustment)list1.get(i);
            if (!adjustment.isValid())
                list1.remove(adjustment);
        }
        
        return false;
    }

    /** Indicates if the task is referenced in the plan. */
    boolean references(Task task) {
        for (Workload workload: workloads)
            if (workload.task == task)
                return true;

        for (Constraint constraint: constraints)
            if (constraint.task == task)
                return true;

        for (Adjustment adjustment: adjustments)
            if (adjustment.task == task)
                return true;

        for (Assignment assignment: assignments)
            if (assignment.task == task)
                return true;

        for (Risk risk: risks)
            if (risk.task == task)
                return true;

        return false;
    }

    /** Returns adjusted workload for the task referenced in the plan. */
    int getAdjustedWorkloadHours(Task task) {
        for (int i = 0; i < workloads.size(); i++) {
            Workload workload = workloads.get(i);
            if (workload.task == task)
                return workload.getAdjustedHours(hoursPerDay);
        }
        return 0;
    }

    /** Generates forecast calendar, based on forecast date and hours per week. */
    public boolean generateForecast(WriterXML xml, 
                                    TransactionXML transaction) {
        List list;
        List list2;
        int i;
        int i2;
        int i5;
        int dayNumber;
        boolean found;
        int maxDays = 0;
        int dayWeek;
        MemberAvailability proratedAvailability = new MemberAvailability();
        Calendar calendarItem = Calendar.getInstance();
        Date dateItem;

        xmlMessage(xml, "message:generateforecast", "", "");

        // Create forecast
        Forecast latestForecast = 
            useRemaining ? project.getLatestForecast(transaction, forecastDate, 
                                                     false) : null;
        Forecast forecast = new Forecast();
        forecast.setActive(transaction);
        forecast.plan = this;
        forecast.forecastDate = forecastDate;
        forecast.endDate = forecastDate;
        forecast.setName();

        ForecastItem proratedItem = new ForecastItem(forecast);

        // Recreate constraints if workload is defined
        for (list = workloads, i = list.size() - 1; i >= 0; i--) {
            Workload workload = (Workload)list.get(i);
            found = false;
            for (list2 = constraints, i2 = list2.size() - 1; i2 >= 0; i2--) {
                Constraint constraint = (Constraint)list2.get(i2);
                if (constraint.task == workload.task) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Constraint constraint = new Constraint(this);
                constraint.task = workload.task;
                constraints.add(constraint);
            }
        }

        for (i = 0; i < constraints.size(); i++) {
            ((Constraint)constraints.get(i)).skip = false;
            ((Constraint)constraints.get(i)).skipPredecessor = false;
        }

        for (i = 0; i < project.tasks.size(); i++) {
            ((Task)project.tasks.get(i)).inserted = false;
            ((Task)project.tasks.get(i)).predecessorComplete = false;
        }

        Collections.sort(constraints);

        // Initialize remaining list from the workload
        List<Remaining> remainingList = new ArrayList(30);
        for (Workload workload: workloads) {
            if (workload.duration != 0) {
                Remaining remaining = new Remaining(this);
                remaining.task = workload.task;
                remaining.setHours(workload.getAdjustedHours(hoursPerDay), 
                                   hoursPerDay);

                if (latestForecast != null) {
                    int completed = 
                        remaining.accumulatedHours = latestForecast.getLastScheduledHours(forecastDate, 
                                                                                          workload.task);
                    found = false;
                    list2 = adjustments;

                    for (i2 = 0; i2 < list2.size(); i2++) {
                        Adjustment adjustment = (Adjustment)list2.get(i2);

                        if (adjustment.task == workload.task) {
                            remaining.setHours(remaining.getHours(hoursPerDay) - 
                                               adjustment.addHours(completed, 
                                                                   hoursPerDay), 
                                               hoursPerDay);
                            remaining.accumulatedHours = 
                                    adjustment.addHours(remaining.accumulatedHours, 
                                                        hoursPerDay);
                            found = true;

                            break;
                        }
                    }

                    if (!found) {
                        remaining.accumulatedHours = completed;
                        remaining.setHours(remaining.getHours(hoursPerDay) - 
                                           completed, hoursPerDay);
                    }

                    if (remaining.duration < 0)
                        remaining.duration = 0;

                    if (remaining.accumulatedHours > 0) {
                        // Create an item with the accumulatedHours
                        ForecastItem item = new ForecastItem(forecast);
                        item.setHours(remaining.accumulatedHours, hoursPerDay);
                        item.task = workload.task;
                        item.type = Item.REPORTED;
                        item.purpose = workload.task.getName();
                        item.accumulatedHours = remaining.accumulatedHours;
                        item.completion = 
                                latestForecast.getLastScheduledCompletion(forecastDate, 
                                                                          workload.task, 
                                                                          false);
                        forecast.items.add(item);
                        item.task.inserted = true;
                    }
                }

                if (remaining.duration != 0)
                    remainingList.add(remaining);

                maxDays += remaining.duration;
            }
        }

        // Maximum iterations equal 2 times the total of durations (in days)
        maxDays = 2 * maxDays;

        // Initialize availability list from the assignments
        List<MemberAvailability> availabilityList = new ArrayList(20);

        // We loop twice: first on the actors, second on the anonymous
        for (int phase = 1; phase <= 2; phase++)
            for (Assignment assignment: assignments) {

                if (((phase == 1) && (assignment.actor != null)) || 
                    ((phase == 2) && (assignment.anonymous != null))) {
                    MemberAvailability availability = new MemberAvailability();
                    availability.actor = 
                            (phase == 1) ? assignment.actor : null;
                    availability.anonymous = 
                            (phase == 2) ? assignment.anonymous : null;
                    found = false;

                    for (i2 = 0; i2 < availabilityList.size(); i2++) {
                        if ((phase == 1) && (assignment.actor != null)) {
                            if (availabilityList.get(i2).actor == 
                                assignment.actor) {
                                found = true;

                                break;
                            }
                        }

                        if ((phase == 2) && (assignment.anonymous != null)) {
                            if (availabilityList.get(i2).anonymous == 
                                assignment.anonymous) {
                                found = true;

                                break;
                            }
                        }
                    }

                    if (!found) {
                        availabilityList.add(availability);

                        if ((phase == 1) && (availability.actor != null)) {
                            if (availability.actor.location == null) {
                                xmlWarning(xml, "error:nolocation", 
                                           availability.actor.getName());
                            } else
                                availability.actor.location.xmlValidate(xml, 
                                                                        transaction, 
                                                                        null);
                        }

                        if ((phase == 2) && (availability.anonymous != null)) {
                            if (availability.anonymous.location == null) {
                                xmlWarning(xml, "error:nolocation", 
                                           availability.anonymous.getName());
                            } else
                                availability.anonymous.location.xmlValidate(xml, 
                                                                            transaction, 
                                                                            null);
                        }
                    }
                }
            }

        // Absences and events for actors
        List<ForecastItem> absenceList = new ArrayList(50);

        for (i = 0; i < project.teams.size(); i++) {
            Team team = ((ProjectTeam)project.teams.get(i)).team;

            if (team != null) {
                for (Member member: team.members) {
                    if (member.actor != null) {
                        for (Absence absence: member.actor.absences) {
                            if (absence.absence != null) {
                                ForecastItem item = new ForecastItem(forecast);
                                item.dateItem = absence.absence;
                                item.set(absence.duration, 
                                         absence.durationType);
                                item.actor = member.actor;
                                item.type = Item.ABSENCE;
                                item.purpose = absence.purpose;
                                absenceList.add(item);
                            }
                        }

                        if (member.actor.holidaySchedule != null) {
                            for (Holiday holiday: 
                                 member.actor.holidaySchedule.holidays) {
                                if (holiday.holiday != null) {
                                    ForecastItem item = 
                                        new ForecastItem(forecast);
                                    item.dateItem = holiday.holiday;
                                    item.update(holiday);
                                    item.actor = member.actor;
                                    item.type = Item.HOLIDAY;
                                    item.purpose = holiday.purpose;
                                    absenceList.add(item);
                                }
                            }
                        }
                    }

                    for (Event event: events) {
                        if (event.event != null) {
                            if ((event.actor == null) || 
                                (event.actor == member.actor)) {
                                ForecastItem item = new ForecastItem(forecast);
                                item.dateItem = event.event;
                                item.update(event);
                                item.type = Item.EVENT;
                                item.actor = member.actor;
                                item.purpose = event.purpose;
                                absenceList.add(item);
                            }
                        }
                    }
                }
            }
        }

        // Events for anonymous
        for (Anonymous anonymous: anonymities) {
            for (Event event: events) {
                if (event.event != null) {
                    ForecastItem item = new ForecastItem(forecast);
                    item.dateItem = event.event;
                    item.update(event);
                    item.type = Item.EVENT;
                    item.anonymous = anonymous;
                    item.purpose = event.purpose;
                    absenceList.add(item);
                }
            }
        }

        // Loop on the absences list and generate detailled absences
        List<ForecastItem> absenceDetailList = new ArrayList(50);

        for (ForecastItem absence: absenceList) {
            calendarItem.setTime(absence.dateItem);

            int max = 10 * absence.duration;

            for (dayNumber = 1; dayNumber < max; dayNumber++) {
                dateItem = calendarItem.getTime();
                dayWeek = calendarItem.get(Calendar.DAY_OF_WEEK);

                // Get availability
                for (MemberAvailability availability: availabilityList) {
                    if ((absence.actor == availability.actor) && 
                        (absence.anonymous == availability.anonymous)) {
                        availability.reset(dateItem, dayWeek);

                        if (availability.duration != 0) {
                            ForecastItem item = new ForecastItem(forecast);
                            item.dateItem = dateItem;
                            item.day = dayWeek;

                            // Calculate duration
                            item.diffMax(absence, availability, hoursPerDay);
                            item.type = absence.type;
                            item.actor = absence.actor;
                            item.anonymous = absence.anonymous;
                            item.purpose = absence.purpose;
                            item.hours = item.getHours(hoursPerDay);
                            absenceDetailList.add(item);
                            absence.subtract(item, hoursPerDay);
                        } else if (dayNumber == 1) {
                            if (absence.actor != null && 
                                !dateItem.before(forecastDate)) {
                                xmlWarning(xml, "warning:absence:nottaken", 
                                           absence.actor.getName(), 
                                           XMLWrapper.dsUS.format(dateItem));
                            }

                            absence.duration = 0;

                            break;
                        }
                    }
                }

                if (absence.duration == 0)
                    break;

                calendarItem.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        // Date initialization
        calendarItem.setTime(forecastDate);

        // Loop on the dates, with a limit
        for (dayNumber = 1; dayNumber < maxDays; dayNumber++) {
            // If no remaining task, then exit
            if (remainingList.size() == 0) {
                xmlTrace(xml, "trace:nomoretask", "", "");
                break;
            }

            dateItem = calendarItem.getTime();
            dayWeek = calendarItem.get(Calendar.DAY_OF_WEEK);
            xmlTrace(xml, "trace:generationday", 
                     dayNumber + " [" + XMLWrapper.dsUS.format(dateItem) + "]", 
                     "");

            // Reset availabilities
            for (MemberAvailability availability: availabilityList)
                availability.reset(dateItem, dayWeek);

            // Fetch on the constraints
            // There are 4 passes in order to optimize workload
            for (int pass = 1; pass < 4; pass++) {
                for (Constraint constraint: constraints) {
                    // Control milestones
                    if ((constraint.task != null) && 
                        (constraint.task.type == Item.MILESTONE)) {
                        // Skip the milestone if it's already 
                        // included in the forecast
                        if (!constraint.skip && !constraint.task.inserted) {
                            if ((constraint.constraint != null) && 
                                dateItem.equals(constraint.constraint)) {
                                ForecastItem item = new ForecastItem(forecast);
                                item.dateItem = dateItem;
                                item.day = dayWeek;
                                item.dayNumber = dayNumber;
                                item.task = constraint.task;
                                item.type = 
                                        (dayNumber > 1) ? Item.MILESTONE : Item.REPORTED;
                                item.purpose = constraint.task.getName();
                                item.target = constraint.constraint;
                                item.phase = constraint.phase;

                                // Look for any predecessor
                                if (constraint.predecessor != null && 
                                    !isTaskPredecessorComplete(remainingList, 
                                                               constraint.task)) {
                                    item.notHit = true;
                                }

                                forecast.items.add(item);
                                forecast.endDate = dateItem;
                                constraint.skip = true;
                                item.task.inserted = true;
                            }

                            if (constraint.predecessor != null && 
                                isTaskPredecessorComplete(remainingList, 
                                                          constraint.task)) {
                                ForecastItem item = new ForecastItem(forecast);
                                item.dateItem = dateItem;
                                item.day = dayWeek;
                                item.dayNumber = dayNumber;
                                item.task = constraint.task;
                                item.type = 
                                        (dayNumber > 1) ? Item.MILESTONE : Item.REPORTED;
                                item.purpose = constraint.task.getName();
                                item.target = constraint.constraint;
                                item.phase = constraint.phase;
                                forecast.items.add(item);
                                forecast.endDate = dateItem;
                                constraint.skip = true;
                                item.task.inserted = true;
                            }
                        }
                    }

                    // Get remaining
                    for (Remaining remaining:remainingList) {
                        if (remaining.task == constraint.task) {
                            if (remaining.duration > 0) {
                                boolean skip = false;

                                // Look for any predecessor
                                if (constraint.predecessor != null) {
                                    if (!isTaskPredecessorComplete(remainingList, 
                                                                   constraint.task)) {
                                        skip = true;
                                    } else if (constraint.predecessor.type == 
                                               Item.MILESTONE) {
                                        if (!constraint.skipPredecessor && 
                                            !constraint.predecessor.inserted) {
                                            ForecastItem item = 
                                                new ForecastItem(forecast);
                                            item.dateItem = dateItem;
                                            item.day = dayWeek;
                                            item.dayNumber = dayNumber;
                                            item.task = constraint.predecessor;
                                            item.type = 
                                                    (dayNumber > 1) ? Item.MILESTONE : 
                                                    Item.REPORTED;
                                            item.purpose = 
                                                    constraint.predecessor.getName();
                                            item.phase = constraint.phase;
                                            constraint.skipPredecessor = true;
                                            forecast.items.add(item);
                                            item.task.inserted = true;
                                        }
                                    }
                                }

                                if (!skip) {
                                    // Get assignment
                                    for (Assignment assignment: assignments) {
                                        if ((assignment.task == 
                                             constraint.task) && 
                                            ((assignment.actor != null) || 
                                             (assignment.anonymous != null))) {
                                            // Get availability
                                            // We loop twice: first on the actors, 
                                            // second on anonymities
                                            for (int phase = 1; phase <= 2; 
                                                 phase++)
                                                for (MemberAvailability availability: 
                                                     availabilityList) {
                                                    if ((phase == 1) && 
                                                        (availability.actor != 
                                                         null)) {
                                                        if (!isAvailable(availability.actor, 
                                                                         dateItem)) {
                                                            availability.duration = 
                                                                    0;
                                                        }
                                                    }

                                                    if ((availability.duration > 
                                                         0) && 
                                                        (((phase == 1) && 
                                                          (availability.actor != 
                                                           null) && 
                                                          (assignment.actor == 
                                                           availability.actor)) || 
                                                         ((phase == 2) && 
                                                          (availability.anonymous != 
                                                           null) && 
                                                          (assignment.anonymous == 
                                                           availability.anonymous)))) {
                                                        for (i5 = 0; 
                                                             i5 < absenceDetailList.size(); 
                                                             i5++) {
                                                            ForecastItem absence = 
                                                                absenceDetailList.get(i5);

                                                            if ((absence.duration > 
                                                                 0) && 
                                                                absence.dateItem.equals(dateItem) && 
                                                                (((phase == 
                                                                   1) && 
                                                                  (absence.actor == 
                                                                   availability.actor)) || 
                                                                 ((phase == 
                                                                   2) && 
                                                                  (absence.anonymous == 
                                                                   availability.anonymous)))) {
                                                                absence.dayNumber = 
                                                                        dayNumber;
                                                                forecast.items.add(absence);
                                                                availability.subtract(absence, 
                                                                                      hoursPerDay);
                                                                absenceDetailList.remove(i5--);
                                                            }
                                                        }

                                                        if ((availability.duration > 
                                                             0) && 
                                                            (remaining.duration > 
                                                             0)) {
                                                            if ((phase == 1) && 
                                                                (assignment.actor != 
                                                                 null)) {
                                                                xmlTrace(xml, 
                                                                         "trace:assigned", 
                                                                         remaining.task.getName(), 
                                                                         assignment.actor.getName());
                                                            } else if ((phase == 
                                                                        2) && 
                                                                       (assignment.anonymous != 
                                                                        null)) {
                                                                xmlTrace(xml, 
                                                                         "trace:assigned", 
                                                                         remaining.task.getName(), 
                                                                         assignment.anonymous.getName());
                                                            }

                                                            proratedAvailability.update(availability);

                                                            if (assignment.fte != 
                                                                0) {
                                                                proratedAvailability.prorate(hoursPerDay, 
                                                                                             assignment.fte);
                                                            }

                                                            ForecastItem item = 
                                                                new ForecastItem(forecast);
                                                            item.dateItem = 
                                                                    dateItem;
                                                            item.day = dayWeek;
                                                            item.dayNumber = 
                                                                    dayNumber;

                                                            /* Calculate duration */
                                                            item.diffMax(remaining, 
                                                                         proratedAvailability, 
                                                                         hoursPerDay);
                                                            item.actor = 
                                                                    (phase == 
                                                                     1) ? 
                                                                    assignment.actor : 
                                                                    null;
                                                            item.anonymous = 
                                                                    (phase == 
                                                                     2) ? 
                                                                    assignment.anonymous : 
                                                                    null;
                                                            item.task = 
                                                                    assignment.task;
                                                            item.type = 
                                                                    assignment.task.type;
                                                            item.purpose = 
                                                                    assignment.task.getName();
                                                            item.phase = 
                                                                    constraint.phase;
                                                            item.target = 
                                                                    constraint.constraint;

                                                            if (constraint.constraint != 
                                                                null) {
                                                                item.notHit = 
                                                                        dateItem.after(constraint.constraint);
                                                            }

                                                            proratedItem.update(item);

                                                            if (!assignment.free && 
                                                                (assignment.fte != 
                                                                 0)) {
                                                                proratedItem.unprorate(hoursPerDay, 
                                                                                       assignment.fte);
                                                            }

                                                            availability.subtract(proratedItem, 
                                                                                  hoursPerDay);
                                                            remaining.subtract(item, 
                                                                               hoursPerDay);
                                                            item.hours = 
                                                                    item.getHours(hoursPerDay);
                                                            remaining.accumulatedHours += 
                                                                    item.hours;
                                                            item.accumulatedHours = 
                                                                    remaining.accumulatedHours - 
                                                                    item.hours;
                                                            item.completion = 
                                                                    (100 * 
                                                                     remaining.accumulatedHours) / 
                                                                    (remaining.getHours(hoursPerDay) + 
                                                                     remaining.accumulatedHours);
                                                            forecast.endDate = 
                                                                    dateItem;
                                                            forecast.items.add(item);
                                                            item.task.inserted = 
                                                                    true;

                                                            if (remaining.duration == 
                                                                0) {
                                                                remainingList.remove(remaining);
                                                            }
                                                        }

                                                        break;
                                                    }
                                                }
                                        }
                                    }
                                }
                            }

                            break;
                        }
                    }
                }
            }

            // Control when somebody is available but not affected
            for (MemberAvailability availability: availabilityList) {
                if (availability.duration > 0) {
                    for (i5 = 0; i5 < absenceDetailList.size(); i5++) {
                        ForecastItem absence = absenceDetailList.get(i5);
                        if ((absence.duration > 0) && 
                            absence.dateItem.equals(dateItem) && 
                            (absence.actor == availability.actor) && 
                            (absence.anonymous == availability.anonymous)) {
                            absence.dayNumber = dayNumber;
                            forecast.items.add(absence);
                            availability.subtract(absence, hoursPerDay);
                            absenceDetailList.remove(i5--);
                        }
                    }

                    if (availability.duration > 0) {
                        ForecastItem item = new ForecastItem(forecast);
                        item.dateItem = dateItem;
                        item.day = dayWeek;
                        item.dayNumber = dayNumber;
                        item.duration = availability.duration;
                        item.durationType = availability.durationType;
                        item.actor = availability.actor;
                        item.anonymous = availability.anonymous;
                        item.type = Item.NO;
                        item.hours = item.getHours(hoursPerDay);
                        forecast.items.add(item);
                    }
                }
            }

            // Let's go to the day after
            calendarItem.add(Calendar.DAY_OF_MONTH, 1);
        }

        // If there are some miletstones outside 
        // the calendar begin and end dates...
        for (Constraint constraint: constraints) {
            if (!constraint.skip && constraint.task != null) {
                if (constraint.task.type == Item.MILESTONE) {
                    if (constraint.constraint != null) {
                        if (constraint.constraint.before(forecast.forecastDate) || 
                            constraint.constraint.after(forecast.endDate)) {
                            ForecastItem item = new ForecastItem(forecast);
                            item.dateItem = constraint.constraint;
                            calendarItem.setTime(constraint.constraint);
                            item.day = calendarItem.get(Calendar.DAY_OF_WEEK);
                            item.task = constraint.task;
                            item.type = 
                                    constraint.constraint.before(forecast.forecastDate) ? 
                                    Item.REPORTED : Item.MILESTONE;
                            item.purpose = constraint.task.getName();
                            item.target = constraint.constraint;
                            item.phase = constraint.phase;
                            if ((constraint.predecessor != null) && 
                                !isTaskPredecessorComplete(remainingList, 
                                                           constraint.task)) {
                                item.notHit = true;
                            }
                            forecast.items.add(item);
                        }
                    } else if (constraint.predecessor != null) {
                        if (!isTaskPredecessorComplete(remainingList, 
                                                       constraint.task)) {
                            ForecastItem item = new ForecastItem(forecast);

                            if (forecast.endDate != null) {
                                item.dateItem = forecast.endDate;
                                calendarItem.setTime(forecast.endDate);
                                item.day = 
                                        calendarItem.get(Calendar.DAY_OF_WEEK);
                            }

                            item.task = constraint.task;
                            item.type = constraint.task.type;
                            item.purpose = constraint.task.getName();
                            item.target = constraint.constraint;
                            item.phase = constraint.phase;
                            if ((constraint.predecessor != null) && 
                                !isTaskPredecessorComplete(remainingList, 
                                                           constraint.task)) {
                                item.notHit = true;
                            }
                            forecast.items.add(item);
                        }
                    }
                }
            }
        }

        // If there is some remaining tasks, 
        // it's an error, but the system shows
        // a warning
        if (remainingList.size() != 0) {
            // Print the remaining
            for (Remaining remaining:remainingList) {
                xmlMessage(xml, "error:remaining", remaining.task.getName(), 
                           "");

                ForecastItem item = new ForecastItem(forecast);
                if (forecast.endDate != null) {
                    item.dateItem = forecast.endDate;
                    calendarItem.setTime(forecast.endDate);
                    item.day = calendarItem.get(Calendar.DAY_OF_WEEK);
                }

                item.task = remaining.task;
                item.purpose = remaining.task.getName();
                item.type = remaining.task.type;
                item.notHit = true;
                forecast.items.add(item);
            }

            xmlWarning(xml, "error:remainingtasks:1", 
                       XMLWrapper.dsUS.format(forecastDate));
            xmlWarning(xml, "error:remainingtasks:2");
        } else {
            xmlMessage(xml, "message:endcalendar", 
                       XMLWrapper.dsUS.format(forecast.endDate));
        }

        // Control with the project target end date
        if ((forecast.endDate != null) && (project.getTarget() != null) && 
            forecast.endDate.after(project.getTarget())) {
            xmlWarning(xml, "warning:targetdate:nothit", 
                       XMLWrapper.dsUS.format(forecast.endDate), 
                       XMLWrapper.dsUS.format(project.getTarget()));
        }

        // Remove all the items after the forecast end date
        for (i5 = 0; i5 < forecast.items.size(); i5++) {
            ForecastItem item = forecast.items.get(i5);
            if (item.dateItem != null && 
                item.dateItem.after(forecast.endDate)) {
                forecast.items.remove(i5--);
            }
        }

        // Updates the forecast on the server
        list = transaction.getServer().projects.forecasts;
        int index = list.indexOf(forecast);
        Forecast draft = (index >= 0) ? (Forecast)list.get(index) : null;
        if (draft != null) {
            draft.update(transaction, forecast);
            update(draft.items, forecast.items);
            xmlMessage(xml, "message:forecast:updated", forecast.getName());
        } else {
            forecast.create(transaction);
            forecast.setName();
            list.add(forecast);
            xmlMessage(xml, "message:forecast:created", forecast.getName());
        }

        // Cleanup
        if (remainingList != null)
            remainingList.clear();

        if (availabilityList != null)
            availabilityList.clear();

        if (absenceList != null)
            absenceList.clear();

        if (absenceDetailList != null)
            absenceDetailList.clear();

        // Only one forecast on the same project can be active
        list = transaction.getServer().projects.forecasts;
        for (i = 0; i < list.size(); i++) {
            Forecast otherForecast = (Forecast)list.get(i);
            if (!otherForecast.equals(forecast) && 
                !otherForecast.equals(draft) && otherForecast.plan != null && 
                otherForecast.plan.project != null &&
                otherForecast.plan.project.equals(project)) {
                otherForecast.setInactive(transaction);
            }
        }

        return true;
    }

    /** Controls if a predecessor (of a task) is complete. */
    private boolean isPredecessorComplete(List<Remaining> remainingList, 
                                          Constraint constraint) {
        // Get remaining for predecessor
        for (Remaining remainingPredecessor:remainingList) {
            if (remainingPredecessor.task == constraint.predecessor) {
                if (remainingPredecessor.duration > 0) 
                    return false;
                break;
            }
        }

        if (constraint.predecessor.type == Item.MILESTONE && 
            !isMilestoneComplete(remainingList, constraint)) {
            return false;
        }

        return true;
    }

    /** Controls if a task is complete. */
    private boolean isTaskPredecessorComplete(List<Remaining> remainingList, 
                                              Task task) {
        if (task.predecessorComplete)
            return true;
        for (Constraint constraint: constraints) {
            if (constraint.task == task && 
                constraint.predecessor != null && 
                !isPredecessorComplete(remainingList, constraint))
                return false;
        }
        task.predecessorComplete = true;
        return true;
    }

    /** Controls if a milestone predecessor (task) is complete. */
    private boolean isMilestoneComplete(List<Remaining> remainingList, 
                                        Constraint constraint) {
        if (constraint.predecessor.type == Item.MILESTONE) {
            for (Constraint constraintPredecessor: constraints) {
                if (constraint.predecessor == constraintPredecessor.task) {
                    if (constraintPredecessor.predecessor.type == 
                         Item.MILESTONE && 
                        !isMilestoneComplete(remainingList, constraintPredecessor)) {
                        return false;
                    } else if (!isTaskPredecessorComplete(remainingList, 
                                                          constraintPredecessor.task)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /** Returns the number of hours per day. */
    public int getHoursPerDay() {
        return hoursPerDay;
    }

    /** Validates the object before delete. */
    public boolean xmlValidateDelete(WriterXML xml, 
                                  TransactionXML transaction) {
        for (Forecast forecast:transaction.getServer().projects.forecasts) {
            if (forecast.plan == this) {
                xmlError(xml, "error:delete", forecast.getName(), getName());
                return true;
            }
        }

        for (Tracking tracking: transaction.getServer().projects.trackings) {
            if (tracking.plan == this) {
                xmlError(xml, "error:delete", tracking.getName(), getName());
                return true;
            }
        }

        for (Status status: transaction.getServer().projects.statuses) {
            if (status.plan == this) {
                xmlError(xml, "error:delete", status.getName(), getName());
                return true;
            }
        }
        
        return false;
    }

    /** Indicates if the actor is available at the instant. */
    public boolean isAvailable(Resource actor, Date instant) {
        int i;

        for (i = 0; i < project.teams.size(); i++) {
            Team team = ((ProjectTeam)project.teams.get(i)).team;
            for (Member member: team.members) {
                if (member.actor == actor) {
                    return member.isAvailable(instant);
                }
            }
        }

        return false;
    }

    /** Retrieves the phase associated to a task. */
    String getPhase(Task task) {
        for (Constraint constraint: constraints) {
            if (constraint.task == task)
                return (constraint.phase != null) ? constraint.phase.phase : 
                       "";
        }
        return "";
    }

    /** Retrieves the phase description associated to a task. */
    String getPhaseDescription(Task task) {
        for (Constraint constraint: constraints) {
            if (constraint.task == task)
                return (constraint.phase != null) ? 
                       constraint.phase.description : "";
        }
        return "";
    }

    /** Returns forecast date. */
    public Date getForecastDate() {
        return forecastDate;
    }

    /**
     * Remaining tasks, used during forecast calendar generation.
     */
    private class Remaining extends Workload {

        /** Accumulated hours for the same task. */
        private int accumulatedHours;

        /** Constructor. */
        public Remaining(Plan plan) {
            super(plan);
        }
    }

    /**
     * Availability of an actor or anonymous during forecast calendar generation.
     */
    private class MemberAvailability extends Duration {

        /** Actor. */
        private Resource actor;

        /** Anonymous. */
        private Anonymous anonymous;

        /** Resets the availability or the actor or anonymous
		  * based on the location working days. */
        public void reset(Date date, int dayWeek) {
            duration = 0;
            durationType = Duration.DAY;

            if (actor != null) {
                Availability availability = 
                    project.getAvailability(actor, date);

                if (availability != null) {
                    if (availability.isWorkingDay(dayWeek))
                        duration = 1;
                } else if (actor.location != null) {
                    if (actor.location.isWorkingDay(dayWeek))
                        duration = 1;
                }
            } else if (anonymous != null) {
                if (anonymous.location != null) {
                    if (anonymous.location.isWorkingDay(dayWeek))
                        duration = 1;
                }
            }
        }
    }
}
