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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/TeamBase.java,v $
$Revision: 1.2 $
$Date: 2007/03/04 21:04:28 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.resources;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.resources.Member;
import net.sourceforge.projectfactory.server.resources.Resource;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;

/** 
  * Defines a team with its members.
  * @author David Lambert
  */
public class TeamBase extends Entity {
    public Resource lead;
    public java.util.List<Member> members = new java.util.ArrayList();

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "team");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, transaction, "lead", lead);
            xmlOut(xml, transaction, members);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("lead") && transaction.getServer().actors != null) {
            lead = (Resource)xmlInEntity(xml,transaction,value,
                new Resource(),transaction.getServer().actors.actors,
                "error:incorrect:lead",this);
            return true;
        }
        return false;
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("member"))
            return new BaseEntityServerXML(transaction, new Member((Team)this),members);
        return null;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) return;
        TeamBase otherEntity = (TeamBase) other;
        super.update(transaction, other);
        this.lead = otherEntity.lead;
        update(this.members,otherEntity.members);
    }
}
