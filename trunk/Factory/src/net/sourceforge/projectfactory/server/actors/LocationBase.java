/*

Copyright (c) 2007 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/LocationBase.java,v $
$Revision: 1.1 $
$Date: 2007/02/27 22:11:53 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.actors;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;

/** 
  * A location refers to a weekly work schedule template.
  * @author David Lambert
  */
public class LocationBase extends Entity {
    public String timeZone;
    public boolean defaultLocation;
    public boolean monday;
    public boolean tuesday;
    public boolean wednesday;
    public boolean thursday;
    public boolean friday;
    public boolean saturday;
    public boolean sunday;

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "location");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "timezone", timeZone);
            xmlOut(xml, "defaultlocation", defaultLocation);
            xmlOut(xml, "monday", monday);
            xmlOut(xml, "tuesday", tuesday);
            xmlOut(xml, "wednesday", wednesday);
            xmlOut(xml, "thursday", thursday);
            xmlOut(xml, "friday", friday);
            xmlOut(xml, "saturday", saturday);
            xmlOut(xml, "sunday", sunday);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("timezone")) {
            timeZone = value;
            return true;
        }
        if (tag.equals("defaultlocation")) {
            defaultLocation = xmlInBoolean(value);
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

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        return null;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) return;
        LocationBase otherEntity = (LocationBase) other;
        super.update(transaction, other);
        this.timeZone = otherEntity.timeZone;
        this.defaultLocation = otherEntity.defaultLocation;
        this.monday = otherEntity.monday;
        this.tuesday = otherEntity.tuesday;
        this.wednesday = otherEntity.wednesday;
        this.thursday = otherEntity.thursday;
        this.friday = otherEntity.friday;
        this.saturday = otherEntity.saturday;
        this.sunday = otherEntity.sunday;
    }
}
