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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Status.java,v $
$Revision: 1.36 $
$Date: 2007/02/06 17:43:53 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.sourceforge.projectfactory.server.entities.DurationCount;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Project status. Inludes %complete for each task and individual task status.
 * @author David Lambert
 */
public class Status extends Entity {

    /** Plan. */
    public Plan plan;

    /** Project confidence. */
    private int confidence;

    /** Date used for the status. */
    private Date dateStatus;

    /** List of status items. */
    public List<StatusItem> items = new ArrayList(20);

    /** List of adjustments. */
    public List<Adjustment> adjustments = new ArrayList(20);

    /** List of workload. */
    public List<Workload> workloads = new ArrayList(20);

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
        boolean found2;

        Forecast latestForecast = null;
        List<PhaseTask> phases = new ArrayList(5);
        int sumHours = 0;
        int proratedHours = 0;
        int proratedScheduledHours = 0;

        totalWorkload.reset();
        totalCompleted.reset();
        totalRemaining.reset();
        totalExposure.reset();

        if (tags)
            xmlStart(xml, "status");

        super.xmlOut(xml, transaction, false);

        if (plan != null && plan.project != null && dateStatus != null && 
            (transaction.isDetail() || transaction.isSave())) {
            xmlOut(xml, transaction, "plan", plan);
            xmlOut(xml, "confidence", confidence);
            xmlOut(xml, "datestatus", dateStatus);

            if (transaction.isDetail()) {
                latestForecast = 
                        plan.project.getLatestForecast(transaction, plan, 
                                                       dateStatus, true);

                if (latestForecast != null) {
                    for (ForecastItem item: latestForecast.items) {
                        if (item.type != Item.NO && 
                            item.type != Item.REPORTED) {
                            item.xmlOutCalendar(xml);
                        }
                    }
                }
            }

            for (Task task: plan.project.tasks) {
                found = false;

                if (Item.needsWorkload(task.type)) {
                    for (StatusItem item: items) {
                        if (item.task == task && item.isValid()) {
                            if (latestForecast != null) {
                                int hours = 
                                    latestForecast.getWorkloadHours(task);
                                int scheduled = 
                                    latestForecast.getLastScheduledCompletion(dateStatus, 
                                                                              task, 
                                                                              true);
                                sumHours += hours;
                                proratedHours += (item.complete * hours);
                                proratedScheduledHours += (scheduled * hours);

                                String phaseName = plan.getPhase(task);
                                String description = 
                                    plan.getPhaseDescription(task);

                                if (phaseName.length() > 0) {
                                    found2 = false;

                                    for (PhaseTask phase: phases) {
                                        if (phase.phase.equals(phaseName)) {
                                            phase.sumHours += hours;
                                            phase.proratedHours += 
                                                    (item.complete * hours);
                                            found2 = true;

                                            break;
                                        }
                                    }

                                    if (!found2) {
                                        PhaseTask phase = new PhaseTask();
                                        phase.sumHours += hours;
                                        phase.proratedHours += 
                                                (item.complete * hours);
                                        phase.phase = phaseName;
                                        phase.description = description;
                                        phases.add(phase);
                                    }
                                }
                            }

                            item.xmlOutForecast(xml, transaction, true, 
                                                latestForecast);
                            found = true;

                            break;
                        }
                    }

                    if (!found && transaction.isDetail()) {
                        xmlStart(xml, "statusitem");
                        xmlAttribute(xml, "level", task.getLevel());
                        xmlAttribute(xml, "task", task.getName());
                        xmlAttribute(xml, "type", task.type);

                        if (latestForecast != null) {
                            int scheduled = 
                                latestForecast.getLastScheduledCompletion(dateStatus, 
                                                                          task, 
                                                                          true);
                            xmlAttribute(xml, "scheduled", scheduled);
                            if (scheduled > 0)
                                xmlOut(xml, "nothit", true);
                        }

                        if (task.gotAction(transaction)) 
                            xmlAttribute(xml, "action", "y");

                        xmlEnd(xml);
                    }
                }
            }

            if (sumHours != 0) {
                xmlOut(xml, "average", proratedHours / sumHours);
                xmlOut(xml, "averagescheduled", 
                       proratedScheduledHours / sumHours);
            }

            for (Task task: plan.project.tasks) {
                int remainingHours = 0;
                int scheduledHours = 0;
                Workload remaining = new Workload(plan);
                Workload workloadplan = new Workload(plan);
                Workload completed = new Workload(plan);

                if (Item.needsWorkload(task.type)) {

                    workloadplan.setHours(plan.getAdjustedWorkloadHours(task), 
                                          plan.hoursPerDay);
                    found = false;
                    for (Workload workload: workloads) {
                        if (workload.task == task && workload.isValid()) {
                            found = true;
                            xmlStart(xml, "workload");
                            workload.duration = workloadplan.duration;
                            workload.durationType = workloadplan.durationType;
                            workload.xmlOut(xml, transaction, false);
                            workload.addCount(totalWorkload);
                            remainingHours = 
                                    workload.getAdjustedHours(plan.hoursPerDay);
                            break;
                        }
                    }

                    if (!found && transaction.isDetail()) {
                        xmlStart(xml, "workload");
                        xmlAttribute(xml, "task", task.getName());
                        if (transaction.isDetail())
                            xmlAttribute(xml, "type", task.type);
                        if (workloadplan.duration > 0) {
                            xmlAttribute(xml, "duration", 
                                         workloadplan.duration);
                            xmlAttribute(xml, "durationtype", 
                                         workloadplan.durationType);
                        }
                        workloadplan.addCount(totalWorkload);
                        remainingHours = 
                                workloadplan.getAdjustedHours(plan.hoursPerDay);
                    }

                    if (found || transaction.isDetail())
                        xmlEnd(xml);

                    found = false;

                    Adjustment adjustmentCompleted = null;
                    for (Adjustment adjustment: adjustments) {
                        if (adjustment.task == task && adjustment.isValid()) {
                            found = true;
                            adjustmentCompleted = adjustment;
                            xmlStart(xml, "adjustment");
                            adjustment.xmlOut(xml, transaction, false);
                            break;
                        }
                    }

                    if (!found && transaction.isDetail()) {
                        xmlStart(xml, "adjustment");
                        xmlAttribute(xml, "task", task.getName());
                        xmlAttribute(xml, "type", task.type);
                    }

                    if (transaction.isDetail()) {
                        int hours = 
                            (latestForecast != null) ? latestForecast.getLastScheduledHours(dateStatus, 
                                                                                            task) : 
                            0;

                        if (hours != 0) {
                            completed.setHours(hours, plan.hoursPerDay);
                            scheduledHours = hours;
                            if (adjustmentCompleted != null) {
                                completed.adjustment = adjustmentCompleted;
                                scheduledHours = 
                                        adjustmentCompleted.addHours(scheduledHours, 
                                                                     plan.hoursPerDay);
                            }
                            if (completed.duration > 0) {
                                completed.addCount(totalCompleted);
                                xmlAttribute(xml, "duration", 
                                             completed.duration);
                                xmlAttribute(xml, "durationtype", 
                                             completed.durationType);
                            }
                        }

                        remaining.setHours(remainingHours - scheduledHours, 
                                           plan.hoursPerDay);
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
            }

            Collections.sort(phases);
            xmlOutPhases(xml, phases);
            xmlOutRisk(xml, transaction);

            // Totals
            if (transaction.isDetail() && plan != null) {
                totalWorkload.xmlOutString(xml, "totalworkload", 
                                           plan.hoursPerDay);
                totalCompleted.xmlOutString(xml, "totalcompleted", 
                                            plan.hoursPerDay);
                totalRemaining.xmlOutString(xml, "totalremaining", 
                                            plan.hoursPerDay);
                totalExposure.xmlOutString(xml, "totalexposure", 
                                           plan.hoursPerDay);
            }
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Writes risks as an XML output. */
    private void xmlOutRisk(WriterXML xml, TransactionXML transaction) {
        for (Risk risk: risks) {
            risk.xmlOut(xml, transaction, true);
            totalExposure.addHours(risk.getExposure());
        }
    }

    /** Writes phases as an XML output. */
    private void xmlOutPhases(WriterXML xml, List<PhaseTask> phases) {
        for (PhaseTask phase: phases) {
            xmlStart(xml, "phase");
            xmlAttribute(xml, "phase", phase.phase);
            xmlAttribute(xml, "description", phase.description);
            if (phase.sumHours != 0 && phase.proratedHours != 0)
                xmlAttribute(xml, "complete", 
                             phase.proratedHours / phase.sumHours);

            xmlEnd(xml);
        }
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("statusitem"))
            return new BaseEntityServerXML(transaction, new StatusItem(this), items);
        if (tag.equals("adjustment"))
            return new BaseEntityServerXML(transaction, new Adjustment(plan), adjustments);
        if (tag.equals("workload")) 
            return new BaseEntityServerXML(transaction, new Workload(plan), workloads);
        if (tag.equals("risk")) 
            return new BaseEntityServerXML(transaction, new Risk(this), risks);
        return null;
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) {
            return true;
        }

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

        if (tag.equals("confidence")) {
            confidence = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("datestatus")) {
            dateStatus = xmlInDate(xml, value);
            return true;
        }

        return false;
    }

    /** Updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) {
            return;
        }

        Status otherStatus = (Status)other;
        super.update(transaction, other);
        this.plan = otherStatus.plan;
        this.confidence = otherStatus.confidence;
        this.dateStatus = otherStatus.dateStatus;
        update(items, otherStatus.items);
        update(adjustments, otherStatus.adjustments);
        update(workloads, otherStatus.workloads);
        update(risks, otherStatus.risks);
    }

    /** Adds prerequisites to the list. */
    public void addPrerequisites(TransactionXML transaction, List<Entity> prerequisites) {
        addPrerequisites(transaction, prerequisites, plan);
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);
        dateStatus = XMLWrapper.systemDate();

        if (plan != null && plan.project != null) {
            confidence = plan.project.confidence;

            Status latestStatus = 
                plan.project.getLatestStatus(transaction, dateStatus, false);

            if (latestStatus != null) {
                plan = plan.project.getLatestActivePlan(transaction);
                confidence = latestStatus.confidence;

                for (StatusItem item: latestStatus.items)
                    items.add(new StatusItem(this, item));

                for (Workload workload: latestStatus.workloads)
                    workloads.add(new Workload(workload));

                for (Task task: plan.project.tasks) {
                    for (StatusItem item: items) {
                        if (item.actionStatus == 0 && item.task == task) {
                            item.actionStatus = task.actionStatus;
                        }
                    }
                }
            }

            // Copy risk items and adjustments from other status
            if (plan != null && latestStatus != null && 
                latestStatus.plan == plan) {
                for (Adjustment adjustment: adjustments)
                    adjustments.add(new Adjustment(adjustment));

                for (Risk risk: latestStatus.risks)
                    risks.add(new Risk(risk));
            }
            // Copy risk items from plan
            else if (plan != null) {
                for (Risk risk: plan.risks)
                    risks.add(new Risk(risk));
            }

            setName();
        }
    }

    /** Sets status name. */
    public void setName() {
        if (plan != null) {
            setName(plan.getName() + ":[" + 
                    XMLWrapper.dsUS.format(dateStatus) + "]");
        }
    }

    /** Provides a summary when the object is displayed in a list. */
    public String getSummary() {
        String color = "";

        switch (confidence) {
        case 1:
            color = "@label:green";
            break;

        case 2:
            color = "@label:orange";
            break;

        case 3:
            color = "@label:red";
            break;
        }

        return "@label:status" + 
            ((dateStatus != null) ? (" [" + XMLWrapper.dsUS.format(dateStatus) + 
                                     "]") : "") + color + 
            (active ? "" : "@label:inactive");
    }

    /** Validates the object before any save or update. */
    public boolean xmlValidate(WriterXML xml, TransactionXML transaction, 
                            List list) {
        int i;
        List list1;

        if(super.xmlValidate(xml, transaction, list))
            return true;

        if (plan == null || plan.project == null || dateStatus == null) {
            return true;
        }

        // Remove all empty element
        for (list1 = items, i = list1.size() - 1; i >= 0; i--) {
            StatusItem item = (StatusItem)list1.get(i);
            if (!item.isValid())
                list1.remove(item);
        }

        for (list1 = workloads, i = list1.size() - 1; i >= 0; i--) {
            Workload workload = (Workload)list1.get(i);
            if (!workload.isValid())
                list1.remove(workload);
        }

        for (list1 = adjustments, i = list1.size() - 1; i >= 0; i--) {
            Adjustment adjustment = (Adjustment)list1.get(i);
            if (!adjustment.isValid())
                list1.remove(adjustment);
        }

        /* Only one status on the same project can be active */
        if (isActive()) {
            for (Status otherStatus: transaction.getServer().projects.statuses) {
                if (otherStatus != this && otherStatus.plan != null && 
                    otherStatus.plan.project == plan.project) {
                    otherStatus.setInactive(transaction);
                }
            }

            plan.project.confidence = confidence;

            for (Task task: plan.project.tasks) {
                for (StatusItem item: items) {
                    if (item.task == task && item.actionStatus != 0)
                        task.actionStatus = item.actionStatus;
                }
            }
        }

        setName();

        return false;
    }

    /** Indicates if the task is referenced in the status. */
    boolean references(Task task) {
        for (Workload workload: workloads)
            if (workload.task == task)
                return true;

        for (StatusItem item: items) {
            if (item.task == task)
                return true;
        }

        for (Adjustment adjustment: adjustments) {
            if (adjustment.task == task)
                return true;
        }

        for (Risk risk: risks)
            if (risk.task == task)
                return true;

        return false;
    }

    /** Compares this object with the specified object for order. */
    public int compareTo(Object object) {
        if (dateStatus != null && ((Status)object).dateStatus != null)
            return dateStatus.compareTo(((Status)object).dateStatus);
        return super.compareTo(object);
    }

    /** Returns status date. */
    public Date getDateStatus() {
        return dateStatus;
    }

    /**
     * Represents phases.
     */
    private class PhaseTask implements Comparable {

        /** Phase. */
        String phase;

        /** Description. */
        String description;

        /** Sum of hours. */
        int sumHours;

        /** Number of prorated hours. */
        int proratedHours;

        /** Compares this object with the specified object for order. */
        public int compareTo(Object object) {
            return this.phase.compareToIgnoreCase(((PhaseTask)object).phase);
        }

        /** Compares two phases for equality. */
        public boolean equals(Object object) {
            if (object == this)
                return true;

            if (object == null)
                return false;

            if (phase == null)
                return false;

            return this.phase.equals(((PhaseTask)object).phase);
        }
    }
}
