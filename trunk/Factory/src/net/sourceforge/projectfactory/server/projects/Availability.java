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

 $Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Availability.java,v $
 $Revision: 1.12 $
 $Date: 2007/01/27 16:30:56 $
 $Author: ddlamb_2000 $

 */
package net.sourceforge.projectfactory.server.projects;

import java.util.Calendar;
import java.util.Date;

import net.sourceforge.projectfactory.server.actors.Actor;
import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * Availability defined for an actor and a project at given dates.
 * @author David Lambert
 */
public class Availability extends BaseEntity {

    /** Project. */
    protected Project project;

    /** From date. */
    private Date from;

    /** To date. */
    private Date to;

    /** Actor who is associated to the event. */
    private Actor actor;

    /** Defines Monday as a working day. */
    private boolean monday;

    /** Defines Tuesday as a working day. */
    private boolean tuesday;

    /** Defines Wednesday as a working day. */
    private boolean wednesday;

    /** Defines Thursday as a working day. */
    private boolean thursday;

    /** Defines Friday as a working day. */
    private boolean friday;

    /** Defines Saturday as a working day. */
    private boolean saturday;

    /** Defines Sunday as a working day. */
    private boolean sunday;

    /** Constructor. */
    public Availability(Project project) {
        this.project = project;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "availability");

        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "from", from);
            xmlAttribute(xml, "to", to);
            xmlAttribute(xml, transaction, "actor", actor);
            xmlAttribute(xml, "monday", monday);
            xmlAttribute(xml, "tuesday", tuesday);
            xmlAttribute(xml, "wednesday", wednesday);
            xmlAttribute(xml, "thursday", thursday);
            xmlAttribute(xml, "friday", friday);
            xmlAttribute(xml, "saturday", saturday);
            xmlAttribute(xml, "sunday", sunday);
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (tag.equals("from")) {
            from = xmlInDate(xml, value);
            return true;
        }

        if (tag.equals("to")) {
            to = xmlInDate(xml, value);
            return true;
        }

        if (tag.equals("actor")) {
            actor = (Actor) xmlInEntity(xml, 
                                        transaction, 
                                        value, 
                                        new Actor(), 
                                        transaction.getServer().actors.actors, 
                                        "error:incorrect:availability:actor", 
                                        project);
            return true;
        }

        if (tag.equals("monday")) {
            monday = xmlInBoolean(value);
            return true;
        }

        if (tag.equals("tuesday")) {
            tuesday = xmlInBoolean(value);
            return true;
        }

        if (tag.equals("wednesday")) {
            wednesday = xmlInBoolean(value);
            return true;
        }

        if (tag.equals("thursday")) {
            thursday = xmlInBoolean(value);
            return true;
        }

        if (tag.equals("friday")) {
            friday = xmlInBoolean(value);
            return true;
        }

        if (tag.equals("saturday")) {
            saturday = xmlInBoolean(value);
            return true;
        }

        if (tag.equals("sunday")) {
            sunday = xmlInBoolean(value);
            return true;
        }

        return false;
    }

    /** Compares two events for equality. */
    public boolean equals(Object object) {
        if (object == this)
            return true;

        if (object == null)
            return false;

        if (from == null)
            return false;

        return this.from.equals(((Availability)object).from);
    }

    /** Indicates if the specified day is a working day. */
    boolean isWorkingDay(int dayWeek) {
        if ((dayWeek == Calendar.MONDAY && monday) || 
            (dayWeek == Calendar.TUESDAY && tuesday) || 
            (dayWeek == Calendar.WEDNESDAY && wednesday) || 
            (dayWeek == Calendar.THURSDAY && thursday) || 
            (dayWeek == Calendar.FRIDAY && friday) || 
            (dayWeek == Calendar.SATURDAY && saturday) || 
            (dayWeek == Calendar.SUNDAY && sunday)) {
            return true;
        }
        return false;
    }

    /** Indicates if the availability is defined for the given date. */
    boolean isDateInRange(Actor actor, Date date) {
        if (this.actor != actor)
            return false;
        if (date == null)
            return false;
        if (from != null && date.before(from))
            return false;
        if (to != null && date.after(to))
            return false;
        return true;
    }
}
