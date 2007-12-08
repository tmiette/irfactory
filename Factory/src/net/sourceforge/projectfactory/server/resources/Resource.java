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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/Actor.java,v $
$Revision: 1.32 $
$Date: 2007/02/27 22:11:53 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.resources;

import java.util.List;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.projects.Item;
import net.sourceforge.projectfactory.server.projects.Project;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Actor of the system, who may be member of a team.
 * @author David Lambert
 */
public class Resource extends ResourceBase {

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags) xmlStart(xml, "actor");
        super.xmlOut(xml, transaction, false);

        if (transaction.isDetail()) {

            for (Absence absence: absences) {
                xmlCalendar(xml, absence.absence, absence.purpose, 
                            Item.ABSENCE, getName(), absence.duration, 
                            absence.durationType, 100);
            }

            for (Resource other: transaction.getServer().actors.actors) {
                if (other.reportsTo != null &&
                        other.reportsTo.equals(this) && 
                        other.isActive()) {
                    xmlStart(xml, "whoreport");
                    xmlAttribute(xml, "name", other.getName());
                    xmlAttribute(xml, "position", other.getPosition());
                    xmlAttribute(xml, "phonenumber", other.getPhoneNumber());
                    xmlEnd(xml);
                }
            }

            for (Team team: transaction.getServer().actors.teams) {
                if (team.isActive()) {
                    for (Member member: team.members) {
                        if (member.actor != null && 
                            member.actor.equals(this)) {
                            xmlStart(xml, "team");
                            xmlAttribute(xml, "team", member.team.getName());
                            xmlAttribute(xml, "from", member.from);
                            xmlAttribute(xml, "to", member.to);
                            xmlAttribute(xml, "role", member.role);
                            xmlAttribute(xml, "lead", member.team.getLead());
                            xmlEnd(xml);

                            for (Project project: transaction.getServer().projects.projects) {
                                if (project.isActive() && 
                                    project.isTeamMember(member.team)) {
                                    xmlStart(xml, "project");
                                    xmlAttribute(xml, "name", project.getName());
                                    xmlAttribute(xml, "begin", project.getBegin());
                                    xmlAttribute(xml, "target", project.getTarget());
                                    xmlAttribute(xml, "lead", project.getLead());
                                    xmlEnd(xml);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (tags) xmlEnd(xml);
    }

    /** Writes the object as an XML output. */
    public void xmlOutRecipient(WriterXML xml, TransactionXML transaction) {
        xmlStart(xml, "networkrecipient");
        xmlAttribute(xml, "iid", getIid());
        xmlAttribute(xml, "name", getName());
        xmlAttribute(xml, "networkid", networkId);
        xmlAttribute(xml, "alternatenetworkid", alternateNetworkId);
        for (EMail email: emails)
            email.xmlOut(xml, transaction, true);
        xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) 
            return true;

        if (tag.equals("reportsto")) {
            reportsTo = (Resource) xmlInEntityCreate(xml, 
                                        transaction, 
                                        value, 
                                        new Resource(), 
                                        transaction.getServer().actors.actors, 
                                        "error:incorrect:reportsto", 
                                        this);
            return true;
        }

        if (tag.equals("location")) {
            location = (Location) xmlInEntityCreate(xml, 
                                        transaction, 
                                        value, 
                                        new Location(), 
                                        transaction.getServer().actors.locations, 
                                        "error:incorrect:location", 
                                        this);
            return true;
        }

        if (tag.equals("holidayschedule")) {
            holidaySchedule = (HolidaySchedule) xmlInEntityCreate(xml, 
                                        transaction, 
                                        value, 
                                        new HolidaySchedule(), 
                                        transaction.getServer().actors.holidaySchedules, 
                                        "error:incorrect:holidayschedule", 
                                        this);
            return true;
        }

        return false;
    }

    /** Adds prerequisites to the list. */
    public void addPrerequisites(TransactionXML transaction, List<Entity> prerequisites) {
        addPrerequisites(transaction, prerequisites, reportsTo);
        addPrerequisites(transaction, prerequisites, location);
        addPrerequisites(transaction, prerequisites, holidaySchedule);
    }

    /** Returns the network identification. */
    public String getNetworkId() {
        return (networkId != null) ? networkId : "";
    }

    /** Returns the alternate network identification. */
    public String getAltNetworkId() {
        return (alternateNetworkId != null) ? alternateNetworkId : "";
    }

    /**
     * Defines the actor as the operator of the system/server.
     * Access to this method is reserved by the server in its initialization phase. */
    public void createDefaultOperator(String networkId) {
        this.name = networkId;
        this.networkId = networkId;
        this.active = true;
    }

    /** Returns the phone number. */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /** Returns actor's position. */
    public String getPosition() {
        return position;
    }

    /** Returns true if the actor is in the same team as another. */
    public boolean isInSameTeam(TransactionXML transaction, Resource actor) {
        for (Team team: transaction.getServer().actors.teams) {
            if (team.isActive() && team.isMember(actor) && team.isMember(this)) 
                return true;
        }
        return false;
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);
        for (Location otherLocation: 
             transaction.getServer().actors.locations) {
            if (otherLocation.isDefault() && otherLocation.isActive()) {
                location = otherLocation;
                break;
            }
        }
        for (HolidaySchedule otherHoliday: 
             transaction.getServer().actors.holidaySchedules) {
            if (otherHoliday.isDefault() && otherHoliday.isActive()) {
                holidaySchedule = otherHoliday;
                break;
            }
        }
    }

    /** Provides a summary when the object is displayed in a list. */
    public String getSummary() {
        String indicator = "";
        if (isActive()) {
            if (isAdministrator())
                indicator = "@label:underline";
        } else
            indicator = "@label:inactive";

        return super.getSummary() + indicator;
    }

    /** Indicates if the actor is an administrator of the system. */
    public boolean isAdministrator() {
        return administrator;
    }
    
    /** Controls the actor is identified in the recipient. 
     *  Updates iid if it matches. */
    public boolean matchNetworkRecipient(TransactionXML transaction, 
                                                String iid, 
                                                String name, 
                                                String networkId,
                                                String alternateNetworkId,
                                                List<EMail> emails,
                                                boolean change) {
        if(getIid().equals(iid)) {
            return true;
        }
        
        if(getName().equals(name)) {
            if(change)
                changeIId(transaction, iid);
            return true;
        }
        
        if((networkId != null && networkId.equals(this.networkId)) || 
            (networkId != null && networkId.equals(this.alternateNetworkId)) ||
            (alternateNetworkId != null && alternateNetworkId.equals(this.networkId)) ||
            (alternateNetworkId != null && alternateNetworkId.equals(this.alternateNetworkId))) {
                if(change)
                    changeIId(transaction, iid);
                return true;
            }

        if(emails != null) {
            for(EMail email: this.emails) 
                for(EMail emailString: emails)
                    if(emailString.address.equals(email.address)) {
                        if(change)
                            changeIId(transaction, iid);
                        return true;
                    }
        }
                    
        return false;                                                
    }

    /** Controls the actor is identified in the recipient. 
     *  Updates iid if it matches. */
    public boolean matchNetworkRecipient(TransactionXML transaction, Resource actor) {
        return matchNetworkRecipient(transaction, 
                                        actor.getIid(), 
                                        actor.name, 
                                        actor.networkId, 
                                        actor.alternateNetworkId, 
                                        actor.emails, 
                                        false);
    }

    /** Called after replication of the object. */
    public void afterReplication(WriterXML xml, 
                                    TransactionXML transaction,
                                    List list) {
        // Look for duplicates
        for(int i=0 ; i<list.size() ; i++) {
            Resource actor = (Resource) list.get(i);
            if(actor != this && matchNetworkRecipient(transaction, actor)) {
                xmlWarning(xml, "warning:duplicate", actor.getName());
                list.remove(i);
                i--;
            }
        }
    }
}
