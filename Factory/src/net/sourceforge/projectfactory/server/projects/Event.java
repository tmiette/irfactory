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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Event.java,v $
$Revision: 1.14 $
$Date: 2007/01/27 16:30:56 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import java.util.Date;

import net.sourceforge.projectfactory.server.entities.Duration;
import net.sourceforge.projectfactory.server.resources.Resource;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;


/**
 * Event attached to a project plan.
 * @author David Lambert
 */
public class Event extends Duration {

    /** Project plan. */
    protected Plan plan;

    /** Date of the event. */
    Date event;

    /** Actor who is associated to the event. */
    public Resource actor;

    /** Constructor. */
    public Event(Plan plan) {
        this.plan = plan;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "event");

        super.xmlOut(xml, transaction, false);

        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "event", event);
            xmlAttribute(xml, transaction, "actor", actor);
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) {
            return true;
        }

        if (tag.equals("event")) {
            event = xmlInDate(xml, value);
            return true;
        }

        if (tag.equals("actor")) {
            actor = (Resource) xmlInEntity(xml, 
                                        transaction, 
                                        value, 
                                        new Resource(), 
                                        transaction.getServer().actors.actors, 
                                        "error:incorrect:event:actor", 
                                        plan);
            return true;
        }

        return false;
    }

    /** Compares two events for equality. */
    public boolean equals(Object object) {
        if (object == this)
            return true;

        if (object == null)
            return false;

        if (event == null)
            return false;

        return event.equals(((Event)object).event);
    }
}
