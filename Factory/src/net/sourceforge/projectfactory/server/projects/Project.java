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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Project.java,v $
$Revision: 1.36 $
$Date: 2007/02/27 22:11:36 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.projectfactory.server.actors.Actor;
import net.sourceforge.projectfactory.server.actors.Member;
import net.sourceforge.projectfactory.server.actors.Skill;
import net.sourceforge.projectfactory.server.actors.Team;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * Define a project, which is a scope, a list of tasks
 * (derived from business process) and teams.
 * @author David Lambert
 */
public class Project extends BusinessProcess {

    /** Project begin date. */
    public Date begin;

    /** Target end date. */
    private Date target;

    /** Confidence (green/yellow/red). */
    int confidence;

    /** Status for the scope. */
    private int statusScope;

    /** Project lead. */
    protected Actor lead;

    /** Business process, if any. */
    BusinessProcess businessProcess;

    /** Items defined for the scope. */
    public List<ScopeItem> scopeItems = new ArrayList(20);

    /** Teams assigned for this project. */
    public List<ProjectTeam> teams = new ArrayList(2);

    /** Availability defined for actors for this project. */
    public List<Availability> availabilities = new ArrayList(2);

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "project");

        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "begin", begin);
            xmlOut(xml, "target", target);
            xmlOut(xml, transaction, "lead", lead);
            xmlOut(xml, "statusscope", statusScope);
            xmlOut(xml, "confidence", confidence);
            xmlOut(xml, transaction, "businessprocess", businessProcess);
            xmlOutScope(xml, transaction);
            xmlOutMembers(xml, transaction);
            xmlOutAvailability(xml, transaction);
            if (transaction.isDetail()) {
                xmlCalendar(xml, begin, "", "label:begindate", 100);
                xmlCalendar(xml, target, "", "label:targetenddate", 100);
            }
        }

        if (transaction.isSummary() || transaction.isExpand())
            xmlOutExpandPlans(xml, transaction);

        if (tags)
            xmlEnd(xml);
    }

    /** Writes associated members as an XML output. */
    private void xmlOutMembers(FactoryWriterXML xml, 
                               TransactionXML transaction) {
        for (ProjectTeam projectTeam: teams) {
            projectTeam.xmlOut(xml, transaction, true);
            if (transaction.isDetail()) {
                Team team = projectTeam.team;
                if (team != null) {
                    for (Member member: team.members) {
                        boolean activated = true;
                        if (begin != null && member.to != null && 
                            member.to.before(begin))
                            activated = false;
                        if (activated && member.actor != null) {
                            member.xmlOut(xml, transaction, true);
                            for (Skill skill: member.actor.skills) {
                                xmlStart(xml, "skill");
                                xmlAttribute(xml, "actor", 
                                             member.actor.getName());
                                xmlAttribute(xml, "skill", skill.skill);
                                xmlAttribute(xml, "level", skill.level);
                                xmlAttribute(xml, "comment", skill.comment);
                                xmlEnd(xml);
                            }
                        }
                    }
                }
            }
        }
    }

    /** Writes associated scope items as an XML output. */
    private void xmlOutScope(FactoryWriterXML xml, 
                             TransactionXML transaction) {

        // Remove updatedBy attribute for demo data
        if (isDemo()) {
            for (ScopeItem scopeItem: scopeItems)
                scopeItem.reset();
        }

        for (ScopeItem scopeItem: scopeItems)
            scopeItem.xmlOut(xml, transaction, true);
    }

    /** Writes associated availability as an XML output. */
    private void xmlOutAvailability(FactoryWriterXML xml, 
                                    TransactionXML transaction) {
        for (Availability availability: availabilities)
            availability.xmlOut(xml, transaction, true);
    }

    /** Writes associated plans as an XML output. */
    private void xmlOutExpandPlans(FactoryWriterXML xml, 
                                   TransactionXML transaction) {
        for (Plan plan: transaction.getServer().projects.plans) {
            if (plan.project != null && plan.project.equals(this)) {
                xmlStart(xml, "plan");
                plan.xmlSummary(xml);
                xmlOutExpandForecasts(xml, transaction, plan);
                xmlOutExpandTrackings(xml, transaction, plan);
                xmlOutExpandStatuses(xml, transaction, plan);
                xmlEnd(xml);
            }
        }
    }

    /** Writes associated forecasts as an XML output. */
    private void xmlOutExpandForecasts(FactoryWriterXML xml, 
                                       TransactionXML transaction, Plan plan) {
        for (Forecast forecast:transaction.getServer().projects.forecasts) {
            if (forecast.plan != null && forecast.plan.project != null && 
                forecast.plan.project.equals(this) && 
                (forecast.plan == plan || (plan == null && 
                                           forecast.isActive()))) {
                forecast.xmlOutSummary(xml, "forecast");
            }
        }
    }

    /** Writes associated forecasts as an XML output. */
    private void xmlOutExpandStatuses(FactoryWriterXML xml, 
                                      TransactionXML transaction, Plan plan) {
        for (Status status: transaction.getServer().projects.statuses) {
            if (status.plan != null && status.plan.project != null && 
                status.plan.project.equals(this) && 
                (status.plan == plan || (plan == null && status.isActive()))) {
                status.xmlOutSummary(xml, "status");
            }
        }
    }

    /** Writes associated trackings as an XML output. */
    private void xmlOutExpandTrackings(FactoryWriterXML xml, 
                                       TransactionXML transaction, Plan plan) {
        for (Tracking tracking: transaction.getServer().projects.trackings) {
            if (tracking.plan != null && tracking.plan.project != null && 
                tracking.plan.project.equals(this) && 
                (tracking.plan == plan || (plan == null && 
                                           tracking.isActive()))) {
                tracking.xmlOutSummary(xml, "tracking");
            }
        }
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("scopeitem")) 
            return new BaseEntityServerXML(transaction, new ScopeItem(), scopeItems);
        if (tag.equals("task")) 
            return new BaseEntityServerXML(transaction, new Task(), tasks);
        if (tag.equals("projectteam"))
            return new BaseEntityServerXML(transaction, new ProjectTeam(this), teams);
        if (tag.equals("availability"))
            return new BaseEntityServerXML(transaction, new Availability(this), availabilities);
        return null;
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) {
            return true;
        }

        if (tag.equals("begin")) {
            begin = xmlInDate(xml, value);
            return true;
        }

        if (tag.equals("target")) {
            target = xmlInDate(xml, value);
            return true;
        }

        if (tag.equals("statusscope")) {
            statusScope = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("confidence")) {
            confidence = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("lead")) {
            lead = (Actor) xmlInEntity(xml, 
                                        transaction, 
                                        value, 
                                        new Actor(), 
                                        transaction.getServer().actors.actors, 
                                        "error:incorrect:projectlead", 
                                        this);
            return true;
        }

        if (tag.equals("businessprocess")) {
            businessProcess = (BusinessProcess) xmlInEntity(xml, 
                                        transaction, 
                                        value, 
                                        new BusinessProcess(), 
                                        transaction.getServer().projects.businessProcesses, 
                                        "error:incorrect:businessprocess", 
                                        this);
            return true;
        }

        return false;
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);
        confidence = 1;
        statusScope = 1;
        if (businessProcess != null) {
            for (Task task: businessProcess.tasks) {
                Task newTask = new Task();
                newTask.update(transaction, task);
                newTask.create(transaction);
                newTask.actionStatus = 1;
                tasks.add(newTask);
            }
        }
    }

    /** Initializes the project. */
    public void create(TransactionXML transaction) {
        super.create(transaction);
        for (ScopeItem item: scopeItems)
            item.create(transaction);
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) {
            return;
        }

        Project otherProject = (Project)other;
        super.update(transaction, other);
        this.begin = otherProject.begin;
        this.target = otherProject.target;
        this.lead = otherProject.lead;
        this.businessProcess = otherProject.businessProcess;
        this.statusScope = otherProject.statusScope;
        this.confidence = otherProject.confidence;
        updateTasks(transaction, scopeItems, otherProject.scopeItems);
        update(teams, otherProject.teams);
        update(availabilities, otherProject.availabilities);
        setName(transaction);
    }

    /** Adds prerequisites to the list. */
    public void addPrerequisites(TransactionXML transaction, List<Entity> prerequisites) {
        addPrerequisites(transaction, prerequisites, businessProcess);
        addPrerequisites(transaction, prerequisites, lead);
        for (ProjectTeam team: teams) 
            addPrerequisites(transaction, prerequisites, team.team);
    }

    /** Sets project name. */
    public void setName(TransactionXML transaction) {
        // Update any plan, forecast or status
        for (Plan plan: transaction.getServer().projects.plans) {
            if (plan.project != null && plan.project.equals(this)) 
                plan.setName();
        }

        for (Forecast forecast:transaction.getServer().projects.forecasts) {
            if (forecast.plan != null && forecast.plan.project != null && 
                forecast.plan.project.equals(this)) {
                forecast.setName();
            }
        }

        for (Status status: transaction.getServer().projects.statuses) {
            if (status.plan != null && status.plan.project != null && 
                status.plan.project.equals(this)) {
                status.setName();
            }
        }

        for (Tracking tracking: transaction.getServer().projects.trackings) {
            if (tracking.plan != null && tracking.plan.project != null && 
                tracking.plan.project.equals(this)) {
                tracking.setName();
            }
        }
    }

    /** Returns the begin date. */
    public Date getBegin() {
        return begin;
    }

    /** Returns the target end date. */
    public Date getTarget() {
        return target;
    }

    /** Returns true if the team is involved in the project. */
    public boolean isTeamMember(Team team) {
        for (ProjectTeam projectTeam: teams)
            if (projectTeam.team != null && projectTeam.team.equals(team))
                return true;

        return false;
    }

    /** Returns true if the actor is involved in the project. */
    public boolean isMember(Actor actor) {
        if (lead == actor)
            return true;

        for (ProjectTeam projectTeam: teams)
            if (projectTeam.team != null && projectTeam.team.isMember(actor))
                return true;

        return false;
    }

    /** Returns the latest active plan for the project. */
    public Plan getLatestActivePlan(TransactionXML transaction) {
        Plan latestPlan = null;
        for (Plan plan: transaction.getServer().projects.plans) {
            if (plan.project != null && 
                    plan.project.equals(this) && 
                    plan.isActive())
                latestPlan = plan;
        }
        return latestPlan;
    }

    /** Returns the latest forecast for the project and plan at the given date. */
    public Forecast getLatestForecast(TransactionXML transaction, Plan plan, 
                                      Date dateStatus, boolean equal) {
        Date dateForecast = null;
        Forecast latestForecast = null;

        for (Forecast forecast:transaction.getServer().projects.forecasts) {
            if ((forecast.plan != null && 
                    forecast.plan.project != null && 
                    forecast.plan.project.equals(this)) && 
                    (plan == null || forecast.plan.equals(plan)) && 
                    (forecast.forecastDate.before(dateStatus) || 
                    (equal && forecast.forecastDate.equals(dateStatus)))) {
                if (dateForecast == null) {
                    dateForecast = forecast.forecastDate;
                    latestForecast = forecast;
                } else if (forecast.forecastDate.after(dateForecast)) {
                    dateForecast = forecast.forecastDate;
                    latestForecast = forecast;
                }
            }
        }
        
        return latestForecast;
    }

    /** Returns the latest forecast for the project and plan. */
    public Forecast getLatestActiveForecast(TransactionXML transaction) {
        Date dateForecast = null;
        Forecast latestForecast = null;

        for (Forecast forecast:transaction.getServer().projects.forecasts) {
            if (forecast.isActive() &&
                    forecast.plan != null && 
                    forecast.plan.project != null && 
                    forecast.plan.project.equals(this)) {
                if (dateForecast == null) {
                    dateForecast = forecast.forecastDate;
                    latestForecast = forecast;
                } else if (forecast.forecastDate.after(dateForecast)) {
                    dateForecast = forecast.forecastDate;
                    latestForecast = forecast;
                }
            }
        }
        
        return latestForecast;
    }

    /** Returns the latest forecast for the project at the given date. */
    public Forecast getLatestForecast(TransactionXML transaction, 
                                      Date dateStatus, boolean equal) {
        return getLatestForecast(transaction, null, dateStatus, equal);
    }

    /** Returns the latest trackling for the project and plan at the given date. */
    public Tracking getLatestTracking(TransactionXML transaction, Plan plan, 
                                      Date dateStatus, boolean equal) {
        Date dateTracking = null;
        Tracking latestTracking = null;

        for (Tracking tracking: transaction.getServer().projects.trackings) {
            if ((tracking.plan != null && 
                    tracking.plan.project != null && 
                    tracking.dateTrackingTo != null && 
                    tracking.plan.project.equals(this)) && 
                    ((plan == null) || (tracking.plan.equals(plan))) && 
                    (tracking.dateTrackingTo.before(dateStatus) || 
                    (equal && tracking.dateTrackingTo.equals(dateStatus)))) {
                if (dateTracking == null) {
                    dateTracking = tracking.dateTrackingTo;
                    latestTracking = tracking;
                } else if (tracking.dateTrackingTo.after(dateTracking)) {
                    dateTracking = tracking.dateTrackingTo;
                    latestTracking = tracking;
                }
            }
        }

        return latestTracking;
    }

    /** Returns the latest status for the project and plan at the given date. */
    public Status getLatestStatus(TransactionXML transaction, Plan plan, 
                                  Date dateStatus, boolean equal) {
        Date dateLatestStatus = null;
        Status latestStatus = null;

        for (Status status: transaction.getServer().projects.statuses) {
            if ((status.plan != null && 
                    status.plan.project != null &&
                    status.plan.project.equals(this)) && 
                    ((plan == null) || (status.plan.equals(plan))) && 
                    (status.getDateStatus().before(dateStatus) || 
                    (equal && status.getDateStatus().equals(dateStatus)))) {
                if (dateLatestStatus == null) {
                    dateLatestStatus = status.getDateStatus();
                    latestStatus = status;
                } else if (status.getDateStatus().after(dateLatestStatus)) {
                    dateLatestStatus = status.getDateStatus();
                    latestStatus = status;
                }
            }
        }

        return latestStatus;
    }

    /** Returns the latest status for the project at the given date. */
    public Status getLatestStatus(TransactionXML transaction, Date dateStatus, 
                                  boolean equal) {
        return getLatestStatus(transaction, null, dateStatus, equal);
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

        return super.getSummary() + color;
    }

    /** Validates the object before delete. */
    public boolean xmlValidateDelete(FactoryWriterXML xml, 
                                  TransactionXML transaction) {
        for (Plan plan: transaction.getServer().projects.plans) {
            if (plan.project.equals(this)) {
                xmlError(xml, "error:delete", getName(), plan.getName());
                return true;
            }
        }
        
        return false;
    }

    /** Returns lead name. */
    public String getLead() {
        return lead != null ? lead.getName() : "";
    }

    /** Returns availability defined for the actor at given date. */
    Availability getAvailability(Actor actor, Date date) {
        if (date == null)
            return null;
        for (Availability availability: availabilities) {
            if (availability.isDateInRange(actor, date))
                return availability;
        }
        return null;
    }
}
