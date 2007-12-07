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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/ForecastItem.java,v $
$Revision: 1.15 $
$Date: 2007/01/27 16:30:56 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import java.util.Date;

import net.sourceforge.projectfactory.server.entities.Duration;
import net.sourceforge.projectfactory.server.resources.Resource;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;


/**
 * Part of the forecast calendar which contains individual and daily information
 * including date, task and assignment.
 * @author David Lambert
 */
public class ForecastItem extends Duration {

    /** Date for the forecast item. */
    public Date dateItem;

    /** Type, coming from the task, event or absence. */
    int type;

    /** Day number in the month. */
    int day;

    /** Indicates a target has not been hit or there is an issue. */
    boolean notHit;

    /** Target or due date. */
    Date target;

    /** % Complete. */
    public int completion;

    /** Day number in the forecast. */
    int dayNumber;

    /** Duration converted into hours. */
    int hours;

    /** Total duration converted into hours for the same task. */
    int accumulatedHours;

    /** Forecast for which the item is attached. */
    public Forecast forecast;

    /** Actor assigned to a task, an event or an absence. */
    public Resource actor;

    /** Anonymous assigned to a task or an event. */
    public Anonymous anonymous;

    /** Phase. */
    Phase phase;

    /** Task */
    public Task task;

    /** Constructor. */
    public ForecastItem(Forecast forecast) {
        this.forecast = forecast;
    }

    /** Writes the object as an XML output. */
    private void xmlOut(WriterXML xml, TransactionXML transaction, 
                        boolean tags, String tag) {
        if (tags)
            xmlStart(xml, tag);

        if (transaction.isDetail() || transaction.isSave()) {
            if (duration > 0) {
                xmlAttribute(xml, "duration", duration);
                xmlAttribute(xml, "durationtype", durationType);
            }
            xmlAttribute(xml, "dateitem", dateItem);
            xmlAttribute(xml, "hours", hours);
            xmlAttribute(xml, "accumulatedhours", accumulatedHours);
            xmlAttribute(xml, "type", type);
            xmlAttribute(xml, "day", day);
            xmlAttribute(xml, "daynumber", dayNumber);
            xmlAttribute(xml, "target", target);
            xmlAttribute(xml, "nothit", notHit);
            xmlAttribute(xml, "completion", completion);
            xmlAttribute(xml, transaction, "actor", actor);
            xmlAttribute(xml, transaction, "task", task);

            if (task == null || transaction.isDetail())
                xmlAttribute(xml, "purpose", purpose);

            if (anonymous != null)
                xmlAttribute(xml, "anonymous", anonymous.getName());

            if (transaction.isDetail()) {
                if (actor != null)
                    xmlAttribute(xml, transaction, "assigned", actor);
                else if (anonymous != null)
                    xmlAttribute(xml, "assigned", anonymous.getName());
            }

            if (phase != null)
                xmlAttribute(xml, "phase", phase.phase);
        }

        if (tags) {
            xmlEnd(xml);
            if (transaction.isDetail())
                xmlOutCalendar(xml);
        }
    }

    /** Writes calendar information as an XML output. */
    void xmlOutCalendar(WriterXML xml) {
        if (phase != null) {
            xmlCalendar(xml, dateItem, phase.phase, Item.PHASE, "", 1, 
                        Duration.DAY, 100);
        }

        if (anonymous != null)
            xmlCalendar(xml, dateItem, purpose, type, anonymous.getName(), 
                        duration, durationType, completion);
        else if (actor != null)
            xmlCalendar(xml, dateItem, purpose, type, actor.getName(), 
                        duration, durationType, completion);
        else
            xmlCalendar(xml, dateItem, purpose, type, "", duration, 
                        durationType, completion);
    }

    /** Writes the object as an XML output using tag "forecastitem". */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        xmlOut(xml, transaction, tags, "forecastitem");
    }

    /** Writes the object as an XML output using another tag. */
    public void xmlOutCompleted(WriterXML xml, 
                                TransactionXML transaction, boolean tags) {
        xmlOut(xml, transaction, tags, "completed");
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (tag.equals("duration")) {
            duration = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("durationtype")) {
            durationType = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("purpose")) {
            purpose = value;
            return true;
        }

        if (tag.equals("dateitem")) {
            dateItem = xmlInDate(xml, value);
            return true;
        }

        if (tag.equals("type")) {
            type = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("day")) {
            day = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("daynumber")) {
            dayNumber = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("target")) {
            target = xmlInDate(xml, value);
            return true;
        }

        if (tag.equals("nothit")) {
            notHit = xmlInBoolean(value);
            return true;
        }

        if (tag.equals("completion")) {
            completion = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("hours")) {
            hours = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("accumulatedhours")) {
            accumulatedHours = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("task")) {
            purpose = value;
            if (forecast.plan != null && forecast.plan.project != null) {
                task = (Task) xmlInEntity(xml, 
                                            transaction, 
                                            value, 
                                            new Task(), 
                                            forecast.plan.project.tasks);
                if(task != null)
                    purpose = task.getName();
            }
            return true;
        }

        if (tag.equals("actor")) {
            actor = (Resource) xmlInEntity(xml, 
                                        transaction, 
                                        value, 
                                        new Resource(), 
                                        transaction.getServer().actors.actors);
            return true;
        }

        if (tag.equals("anonymous")) {
            if(forecast.plan != null) {
                anonymous = (Anonymous) xmlInEntity(xml, 
                                            transaction, 
                                            value, 
                                            new Anonymous(forecast.plan), 
                                            forecast.plan.anonymities);
            }
            return true;
        }

        if (tag.equals("phase")) {
            if(forecast.plan != null) {
                phase = (Phase) xmlInEntity(xml, 
                                            transaction, 
                                            value, 
                                            new Phase(forecast.plan), 
                                            forecast.plan.phases);
            }
            return true;
        }
        return false;
    }
}
