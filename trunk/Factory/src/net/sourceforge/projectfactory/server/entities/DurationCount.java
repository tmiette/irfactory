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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/entities/DurationCount.java,v $
$Revision: 1.11 $
$Date: 2006/12/30 10:06:57 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.entities;

import net.sourceforge.projectfactory.xml.WriterXML;


/**
  * Class used in order to count durations (calculates totals),
  * in days, half days and hours.
  * @author David Lambert
  */
public class DurationCount extends Duration {

    /** Total in hours. */
    private int hours;

    /** Total in half days. */
    private int halfDays;

    /** Total in days. */
    private int days;

    /** Adds a duration with a sign. */
    public void add(int sign, int duration, int durationType) {
        if (durationType == Duration.HOUR) {
            hours += sign * duration;
        } else if (durationType == Duration.HALFDAY) {
            halfDays += sign * duration;
        } else if (durationType == Duration.DAY) {
            days += sign * duration;
        }
    }

    /** Adds a duration with a sign. */
    public void add(int sign, Duration duration) {
        add(sign, duration.duration, duration.durationType);
    }

    /** Adds a duration. */
    public void add(Duration duration) {
        add(1, duration);
    }

    /** Adds a duration with a sign. */
    public void add(int duration, int durationType) {
        add(1, duration, durationType);
    }

    /** Subtracts a duration. */
    public void sub(Duration duration) {
        add(-1, duration);
    }

    /** Adds hours. */
    public void addHours(int hours) {
        this.hours += hours;
    }

    /** Adds days. */
    public void addDays(int days) {
        this.days += days;
    }

    /** Returns duration component in hours. */
    public int getHours() {
        return hours;
    }

    /** Returns duration component in half days. */
    public int getHalfDays() {
        return halfDays;
    }

    /** Returns duration component in days. */
    public int getDays() {
        return days;
    }

    /** Normalizes components. */
    public void normalize() {
        days += (halfDays / 2);
        halfDays = halfDays % 2;
    }

    /** Normalizes components. */
    public void normalize(int hoursPerDay) {
        if (hoursPerDay != 0) {
            halfDays += (hours / (hoursPerDay / 2));
            hours = (hours % (hoursPerDay / 2));
        }
        normalize();
        set(days * hoursPerDay + halfDays * hoursPerDay / 2 + hours, 
            Duration.HOUR);
        super.normalize(hoursPerDay);
    }

    /** Reset count. */
    public void reset() {
        hours = 0;
        halfDays = 0;
        days = 0;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, String tag) {
        xmlAttribute(xml, tag + "hours", hours);
        xmlAttribute(xml, tag + "halfdays", halfDays);
        xmlAttribute(xml, tag + "days", days);
    }

    /** Writes the object as an XML output. */
    public void xmlOutString(WriterXML xml, String tag) {
        normalize();
        String output = "";
        if (days != 0)
            output += 
                    (output.length() > 0 ? ", " : "") + days + " @label:day" + 
                    (days > 1 ? "s" : "");
        if (halfDays != 0)
            output += 
                    (output.length() > 0 ? ", " : "") + halfDays + " @label:halfday" + 
                    (halfDays > 1 ? "s" : "");
        if (hours != 0)
            output += 
                    (output.length() > 0 ? ", " : "") + hours + " @label:hour" + 
                    (hours > 1 ? "s" : "");
        xmlOut(xml, tag, output);
    }

    /** Writes the object as an XML output. */
    public void xmlOutString(WriterXML xml, String tag, 
                             int hoursPerDay) {
        normalize(hoursPerDay);
        xmlOutString(xml, tag);
    }
}
