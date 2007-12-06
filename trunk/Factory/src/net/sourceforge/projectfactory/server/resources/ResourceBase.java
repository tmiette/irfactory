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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/ActorBase.java,v $
$Revision: 1.1 $
$Date: 2007/02/27 22:11:53 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.resources;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.resources.HolidaySchedule;
import net.sourceforge.projectfactory.server.resources.Location;
import net.sourceforge.projectfactory.server.resources.Resource;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;

/** 
  * Actor definition.
  * @author David Lambert
  */
public class ResourceBase extends Entity {
    public String networkId;
    public String alternateNetworkId;
    public String phoneNumber;
    public boolean administrator;
    public String position;
    public Resource reportsTo;
    public HolidaySchedule holidaySchedule;
    public Location location;
    public java.util.List<Absence> absences = new java.util.ArrayList();
    public java.util.List<EMail> emails = new java.util.ArrayList();
    public java.util.List<Skill> skills = new java.util.ArrayList();

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "actor");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "networkid", networkId);
            xmlOut(xml, "alternatenetworkid", alternateNetworkId);
            xmlOut(xml, "phonenumber", phoneNumber);
            xmlOut(xml, "administrator", administrator);
            xmlOut(xml, "position", position);
            xmlOut(xml, transaction, "reportsto", reportsTo);
            xmlOut(xml, transaction, "holidayschedule", holidaySchedule);
            xmlOut(xml, transaction, "location", location);
            xmlOut(xml, transaction, absences);
            xmlOut(xml, transaction, emails);
            xmlOut(xml, transaction, skills);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("networkid")) {
            networkId = value;
            return true;
        }
        if (tag.equals("alternatenetworkid")) {
            alternateNetworkId = value;
            return true;
        }
        if (tag.equals("phonenumber")) {
            phoneNumber = value;
            return true;
        }
        if (tag.equals("administrator")) {
            administrator = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("position")) {
            position = value;
            return true;
        }
        return false;
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("absence"))
            return new BaseEntityServerXML(transaction, new Absence(),absences);
        if (tag.equals("email"))
            return new BaseEntityServerXML(transaction, new EMail(),emails);
        if (tag.equals("skill"))
            return new BaseEntityServerXML(transaction, new Skill(),skills);
        return null;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) return;
        ResourceBase otherEntity = (ResourceBase) other;
        super.update(transaction, other);
        this.networkId = otherEntity.networkId;
        this.alternateNetworkId = otherEntity.alternateNetworkId;
        this.phoneNumber = otherEntity.phoneNumber;
        this.administrator = otherEntity.administrator;
        this.position = otherEntity.position;
        this.reportsTo = otherEntity.reportsTo;
        this.holidaySchedule = otherEntity.holidaySchedule;
        this.location = otherEntity.location;
        update(this.absences,otherEntity.absences);
        update(this.emails,otherEntity.emails);
        update(this.skills,otherEntity.skills);
    }
}
