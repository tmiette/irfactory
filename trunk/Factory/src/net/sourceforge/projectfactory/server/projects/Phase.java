/*

Copyright (c) 2006 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Phase.java,v $
$Revision: 1.10 $
$Date: 2007/01/04 15:42:13 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;


/**
 * Phase attached to a project plan.
 * @author David Lambert
 */
public class Phase extends BaseEntity {

    /** Project plan. */
    protected Plan plan;

    /** Description of the phase. */
    String phase;

    /** Description. */
    String description;

    /** Constructor. */
    public Phase(Plan plan) {
        this.plan = plan;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "phase");

        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "phase", phase);
            xmlAttribute(xml, "description", description);
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (tag.equals("phase")) {
            phase = value;
            return true;
        }

        if (tag.equals("description")) {
            description = value;
            return true;
        }

        return false;
    }

    /** Compares two phases for equality. */
    public boolean equals(Object object) {
        if (object == this)
            return true;

        if (object == null || phase == null)
            return false;

        return phase.equals(((Phase)object).phase);
    }

    /** Sets entity name. */
    public void setName(String name) {
        this.phase = name;
    }
}
