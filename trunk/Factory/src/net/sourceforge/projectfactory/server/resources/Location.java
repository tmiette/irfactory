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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/Location.java,v $
$Revision: 1.18 $
$Date: 2007/03/18 16:39:42 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.resources;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;


/**
 * A location is defined for a time zone and working days.
 * @author David Lambert
 */
public class Location extends LocationBase {

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags) xmlStart(xml, "location");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail()) {
            for (Resource other:transaction.getServer().actors.actors) {
                if (other.location != null &&
					other.location.equals(this) && 
					other.isActive()) {
                    xmlStart(xml, "actor");
                    xmlAttribute(xml, "name", other.getName());
                    xmlAttribute(xml, "position", other.getPosition());
                    xmlAttribute(xml, "phonenumber", other.getPhoneNumber());
                    xmlEnd(xml);
                }
            }
        }
        if (tags) xmlEnd(xml);
    }

    /** True if the location is the default. */
    public boolean isDefault() {
        return defaultLocation;
    }

    /** Validates the object before any save or update. */
    public boolean xmlValidate(WriterXML xml, TransactionXML transaction, 
                            List list) {
        if(super.xmlValidate(xml, transaction, list))
            return true;

        if (!monday && !tuesday && !wednesday && !thursday && !friday && 
            !saturday && !sunday) {
            xmlError(xml, "error:incorrect:location:days", getName());
            return true;
        }
        return false;
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);
        monday = true;
        tuesday = true;
        wednesday = true;
        thursday = true;
        friday = true;
        timeZone = TimeZone.getDefault().getID();
    }

    /** Indicates if the specified day is a working day in this location. */
    public boolean isWorkingDay(int dayWeek) {
        if (((dayWeek == Calendar.MONDAY) && monday) || 
            ((dayWeek == Calendar.TUESDAY) && tuesday) || 
            ((dayWeek == Calendar.WEDNESDAY) && wednesday) || 
            ((dayWeek == Calendar.THURSDAY) && thursday) || 
            ((dayWeek == Calendar.FRIDAY) && friday) || 
            ((dayWeek == Calendar.SATURDAY) && saturday) || 
            ((dayWeek == Calendar.SUNDAY) && sunday)) {
            return true;
        }
        return false;
    }
}
