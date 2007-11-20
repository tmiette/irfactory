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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/ProjectTeam.java,v $
$Revision: 1.14 $
$Date: 2007/01/27 16:30:56 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import net.sourceforge.projectfactory.server.actors.Team;
import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * Defines a team as project member.
 * @author David Lambert
 */
public class ProjectTeam extends BaseEntity {

    /** Project. */
    protected Project project;

    /** Team. */
    public Team team;

    /** Constructor. */
    public ProjectTeam(Project project) {
        this.project = project;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "projectteam");

        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, transaction, "team", team);
            if (team != null && transaction.isDetail())
                xmlAttribute(xml, "lead", team.getLead());
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (tag.equals("team")) {
            team = (Team) xmlInEntity(xml, 
                                        transaction, 
                                        value, 
                                        new Team(), 
                                        transaction.getServer().actors.teams, 
                                        "error:incorrect:projectteam", 
                                        project);
            return true;
        }
        return false;
    }
}
