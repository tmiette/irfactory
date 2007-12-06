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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/MemberBase.java,v $
$Revision: 1.3 $
$Date: 2007/03/18 16:39:42 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.resources;

import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.resources.Resource;
import net.sourceforge.projectfactory.server.resources.Team;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;

/** 
  * Defines an actor as member of a team for a period of time.
  * @author David Lambert
  */
public class MemberBase extends BaseEntity {
    public Team team;
    public java.util.Date from;
    public java.util.Date to;
    public String role;
    public Resource actor;
    public boolean interim;

    /** Constructor. */
    public MemberBase(Team team) {
        this.team = team;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "member");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "from", from);
            xmlAttribute(xml, "to", to);
            xmlAttribute(xml, "role", role);
            xmlAttribute(xml, transaction, "actor", actor);
            xmlAttribute(xml, "interim", interim);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("from")) {
            from = xmlInDate(xml, value);
            return true;
        }
        if (tag.equals("to")) {
            to = xmlInDate(xml, value);
            return true;
        }
        if (tag.equals("role")) {
            role = value;
            return true;
        }
        if (tag.equals("actor") && transaction.getServer().actors != null) {
            actor = (Resource)xmlInEntity(xml,transaction,value,
                new Resource(),transaction.getServer().actors.actors,
                "error:incorrect:member",team);
            return true;
        }
        if (tag.equals("interim")) {
            interim = xmlInBoolean(value);
            return true;
        }
        return false;
    }

    /** Indicates if the member is available at the instant. */
    public boolean isAvailable(java.util.Date instant) {
        if (instant == null) return false;
        if (from != null && instant.before(from)) return false;
        if (to != null && instant.after(to)) return false;
        return true;
    }
}
