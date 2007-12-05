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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/Member.java,v $
$Revision: 1.15 $
$Date: 2007/03/18 16:39:42 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.actors;

import java.util.Date;

import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Defines an actor as member of a team for a period of time.
 * @author David Lambert
 */
public class Member extends MemberBase {

    /** Constructor. */
    public Member(Team team) {
        super(team);
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
		xmlOut(xml, transaction, tags, null);
	}

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags, Date date) {
        if (tags) xmlStart(xml, "member");
        super.xmlOut(xml, transaction, false);
        if (actor != null && transaction.isDetail()) {
            if (actor.location != null)
                xmlAttribute(xml, "location", actor.location.getName());
            xmlAttribute(xml, "phonenumber", actor.phoneNumber);
            if (!(date != null && from != null && date.after(from)))
                xmlCalendar(xml, from, "", "label:member:from", actor.getName(), 100);
            if (!(date != null && to != null && date.before(to)))
                xmlCalendar(xml, to, "", "label:member:to", actor.getName(), 100);
        }
        if (tags) xmlEnd(xml);
    }
}
