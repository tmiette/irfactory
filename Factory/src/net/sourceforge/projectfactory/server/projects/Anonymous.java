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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Anonymous.java,v $
$Revision: 1.13 $
$Date: 2007/01/27 16:30:56 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.resources.Location;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;


/**
 * Defines an anonymous person assigned to taks in a project plan.
 * @author David Lambert
 */
public class Anonymous extends BaseEntity {

    /** Anonymous name. */
    String name;

    /** Comment or description. */
    protected String description;

    /** Project plan. */
    public Plan plan;

    /** Location. */
    Location location;

    /** Constructor. */
    public Anonymous(Plan plan) {
        this.plan = plan;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "anonymous");

        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "name", name);
            xmlAttribute(xml, "description", description);
            xmlAttribute(xml, transaction, "location", location);
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (tag.equals("name")) {
            name = value;
            return true;
        }

        if (tag.equals("description")) {
            description = value;
            return true;
        }

        if (tag.equals("location")) {
            location = (Location) xmlInEntity(xml, 
                                        transaction, 
                                        value, 
                                        new Location(), 
                                        transaction.getServer().actors.locations, 
                                        "error:incorrect:location:anonymous", 
                                        plan);
            return true;
        }

        return false;
    }

    /** Compares this anonymous to the specified object. */
    public boolean equals(Object object) {
        if (object == this)
            return true;

        if (object == null)
            return false;

        if (name == null)
            return false;

        return name.equals(((Anonymous)object).name);
    }

    /** Sets entity name. */
    public void setName(String name) {
        this.name = name;
    }
    
    /** Returns anonymous name. */
    public String getName() {
        return name;
    }
}
