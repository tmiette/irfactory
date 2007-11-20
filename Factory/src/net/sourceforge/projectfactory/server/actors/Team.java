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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/Team.java,v $
$Revision: 1.20 $
$Date: 2007/03/18 16:39:42 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.actors;

import java.util.List;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.projects.Item;
import net.sourceforge.projectfactory.server.projects.Project;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * Defines a team.
 * @author David Lambert
 */
public class Team extends TeamBase {

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags) xmlStart(xml, "team");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail()) {
            for (Member member: members) {
				if (member.actor != null) {
					for (Absence absence: member.actor.absences) {
						xmlStart(xml, "absence");
						xmlAttribute(xml, "actor", member.actor.getName());
						xmlAttribute(xml, "absence", absence.absence);
						xmlAttribute(xml, "duration", absence.duration);
						xmlAttribute(xml, "durationtype", absence.durationType);
						xmlAttribute(xml, "purpose", absence.purpose);
						xmlEnd(xml);
						xmlCalendar(xml, absence.absence, absence.purpose, 
									Item.ABSENCE, member.actor.getName(), 
									absence.duration, absence.durationType, 
									100);
					}

					for (Skill skill: member.actor.skills) {
						xmlStart(xml, "skill");
						xmlAttribute(xml, "actor", member.actor.getName());
						xmlAttribute(xml, "skill", skill.skill);
						xmlAttribute(xml, "level", skill.level);
						xmlAttribute(xml, "comment", skill.comment);
						xmlEnd(xml);
					}
				}
            }

            for (Project project: 
                 transaction.getServer().projects.projects) {
                if (project.isActive() && project.isTeamMember(this)) {
                    xmlStart(xml, "project");
                    xmlAttribute(xml, "name", project.getName());
                    xmlAttribute(xml, "begin", project.getBegin());
                    xmlAttribute(xml, "target", project.getTarget());
                    xmlAttribute(xml, "lead", project.getLead());
                    xmlEnd(xml);
                }
            }
        }
        if (tags) xmlEnd(xml);
    }

	/** Adds prerequisites to the list. */
	public void addPrerequisites(TransactionXML transaction, List<Entity> prerequisites) {
		addPrerequisites(transaction, prerequisites, lead);
		for (Member member: members) 
			addPrerequisites(transaction, prerequisites, member.actor);
	}

    /** Returns true if the actor is member of the team. */
    public boolean isMember(Actor actor) {
        if (lead != null && lead.equals(actor)) 
            return true;

        for (Member member: members) 
            if (member.actor != null && member.actor.equals(actor)) 
                return true;

        return false;
    }

    /** Returns lead name. */
    public String getLead() {
        return lead != null ? lead.getName() : "";
    }
}
