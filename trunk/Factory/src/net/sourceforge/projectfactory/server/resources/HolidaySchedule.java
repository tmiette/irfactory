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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/HolidaySchedule.java,v $
$Revision: 1.14 $
$Date: 2007/03/18 16:39:42 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.resources;

import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * A holiday schedule is defined by a list of holiday.
 * @author David Lambert
 */
public class HolidaySchedule extends HolidayScheduleBase {

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags) xmlStart(xml, "holidayschedule");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail()) {
            for (Resource other:transaction.getServer().actors.actors) {
                if (other.holidaySchedule != null &&
					other.holidaySchedule.equals(this) && 
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

    /** True if the holiday schedule is the default. */
    public boolean isDefault() {
        return defaultHolidaySchedule;
    }
}
