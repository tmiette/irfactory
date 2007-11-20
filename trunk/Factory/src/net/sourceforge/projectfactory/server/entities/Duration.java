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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/entities/Duration.java,v $
$Revision: 1.10 $
$Date: 2006/12/30 10:06:57 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.entities;

import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * Abstract class used in order to manipulate durations with a unit (or type),
 * in hours, days or half-days.
 * @author David Lambert
 */
public abstract class Duration extends BaseEntity {

    /** None. */
    public static final int NONE = 0;

    /** Day. */
    public static final int DAY = 1;

    /** Half-day. */
    public static final int HALFDAY = 2;

    /** Hour. */
    public static final int HOUR = 3;

    /** Represents the duration. */
    public int duration;

    /** Represents the duration unit. */
    public int durationType = DAY;

    /** Describes the date with a comment or purpose. */
    public String purpose;

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if ((transaction.isDetail()) || 
            (transaction.isSave())) {
            if (duration > 0) {
                xmlAttribute(xml, "duration", duration);
                xmlAttribute(xml, "durationtype", durationType);
            }
            xmlAttribute(xml, "purpose", purpose);
        }
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, 
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

        return false;
    }

    /** Calculates the difference between 2 durations. */
    public void diff(Duration o1, Duration o2, int hoursPerDay) {
        if (o1.durationType == o2.durationType) {
            duration = o1.duration - o2.duration;
            durationType = o1.durationType;
        } else if ((o1.durationType == DAY) && (o2.durationType == HOUR)) {
            duration = (hoursPerDay * o1.duration) - o2.duration;
            durationType = HOUR;
        } else if ((o1.durationType == HOUR) && (o2.durationType == DAY)) {
            duration = o1.duration - (hoursPerDay * o2.duration);
            durationType = HOUR;
        } else if ((o1.durationType == HALFDAY) && (o2.durationType == HOUR)) {
            duration = ((hoursPerDay * o1.duration) / 2) - o2.duration;
            durationType = HOUR;
        } else if ((o1.durationType == HOUR) && (o2.durationType == HALFDAY)) {
            duration = o1.duration - ((hoursPerDay * o2.duration) / 2);
            durationType = HOUR;
        } else if ((o1.durationType == DAY) && (o2.durationType == HALFDAY)) {
            duration = (2 * o1.duration) - o2.duration;
            durationType = HALFDAY;
        } else if ((o1.durationType == HALFDAY) && (o2.durationType == DAY)) {
            duration = o1.duration - (2 * o2.duration);
            durationType = HALFDAY;
        }

        if (duration > 0) {
            normalize(hoursPerDay);
        }
    }

    /** Calculates the maximum between 2 durations. */
    public void diffMax(Duration o1, Duration o2, int hoursPerDay) {
        diff(o1, o2, hoursPerDay);

        if (duration < 0) {
            duration = o1.duration;
            durationType = o1.durationType;
        } else {
            duration = o2.duration;
            durationType = o2.durationType;
        }

        normalize(hoursPerDay);
    }

    /** Substracts one duration. */
    public void subtract(Duration o2, int hoursPerDay) {
        diff(this, o2, hoursPerDay);

        if (duration < 0) {
            duration = 0;
        }
    }

    /** Converts the duration to the best approriate unit,
	  * based on the number of hours per day. */
    public void normalize(int hoursPerDay) {
        if ((hoursPerDay > 0) && (duration > 0)) {
            if ((durationType == HOUR) && ((duration % hoursPerDay) == 0)) {
                duration /= hoursPerDay;
                durationType = DAY;
            } else if ((durationType == HOUR) && 
                       ((duration % (hoursPerDay / 2)) == 0)) {
                duration /= (hoursPerDay / 2);
                durationType = HALFDAY;
            } else if ((durationType == HALFDAY) && ((duration % 2) == 0)) {
                duration /= 2;
                durationType = DAY;
            }
        } else {
            duration = 0;
            durationType = NONE;
        }
    }

    /** Calculates a proration based on a percent. */
    public void prorate(int hoursPerDay, int percent) {
        if (duration == 0) {
            return;
        }

        if (percent == 0) {
            duration = 0;
        } else if (percent != 100) {
            if (durationType == HOUR) {
                duration = (duration * percent) / 100;
            } else if (durationType == DAY) {
                duration = (hoursPerDay * duration * percent) / 100;
                durationType = HOUR;
            } else if (durationType == HALFDAY) {
                duration = (hoursPerDay * duration * percent) / 200;
                durationType = HOUR;
            }
        }

        if (duration == 0) {
            duration = 1;
        }

        normalize(hoursPerDay);
    }

    /** Calculates a reversed proration based on a percent. */
    public void unprorate(int hoursPerDay, int percent) {
        if (duration == 0) {
            return;
        }

        if (percent == 0) {
            duration = 0;
        } else if (percent != 100) {
            if (durationType == HOUR) {
                duration = (100 * duration) / percent;
            } else if (durationType == DAY) {
                duration = (100 * hoursPerDay * duration) / percent;
                durationType = HOUR;
            } else if (durationType == HALFDAY) {
                duration = (200 * hoursPerDay * duration) / percent;
                durationType = HOUR;
            }
        }

        if (duration == 0) {
            duration = 1;
        }

        normalize(hoursPerDay);
    }

    /** Converts the duration in hours. */
    public int getHours(int hoursPerDay) {
        if (durationType == HOUR) {
            return duration;
        }

        if (durationType == DAY) {
            return hoursPerDay * duration;
        }

        if (durationType == HALFDAY) {
            return (hoursPerDay * duration) / 2;
        }

        return 0;
    }

    /** Assigns a value based on a number of hours and a number of hours
	  * per day in order to normalize. */
    public void setHours(int hours, int hoursPerDay) {
        durationType = HOUR;
        duration = hours;
        normalize(hoursPerDay);
    }

    /** Assigns a value based on a duration and a unit. */
    public void set(int duration, int durationType) {
        this.durationType = durationType;
        this.duration = duration;
    }

    /** Updates the availability from another duration. */
    public void update(Duration other) {
        duration = other.duration;
        durationType = other.durationType;
        purpose = other.purpose;
    }
}
