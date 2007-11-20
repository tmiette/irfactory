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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actions/Recipient.java,v $
$Revision: 1.12 $
$Date: 2007/03/04 21:04:59 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.actions;

import net.sourceforge.projectfactory.server.actions.Action;
import net.sourceforge.projectfactory.server.actors.Actor;
import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;

/** 
  * Defines an actor as recipient of an action.
  * @author David Lambert
  */
public class Recipient extends BaseEntity {
    public Action action;
    public int role;
    public int status;
    public Actor actor;
    public static int NOT_SENT = 1;
    public static int SENT = 2;
    public static int ANSWERED = 3;
    public static int ERROR = 4;
    public static int NA = 5;
    public static int RECEIVED = 6;

    /** Constructor. */
    public Recipient(Action action) {
        this.action = action;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "recipient");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "role", role);
            xmlAttribute(xml, "status", status);
            xmlAttribute(xml, transaction, "actor", actor);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("role")) {
            role = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("status")) {
            status = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("actor") && transaction.getServer().actors != null) {
            actor = (Actor)xmlInEntity(xml,transaction,value,
                new Actor(),transaction.getServer().actors.actors,
                "error:incorrect:recipient",action);
            return true;
        }
        return false;
    }
}
