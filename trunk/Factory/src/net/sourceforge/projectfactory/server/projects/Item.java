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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/Item.java,v $
$Revision: 1.12 $
$Date: 2007/01/03 11:11:18 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Defines an item, which is an entity, used to create actions or tasks. Contains a type and a level.
 * @author David Lambert
 */
public abstract class Item extends Entity {

    /** Represents a task. */
    public static final int ITEMTASK = 1;

    /** Represents a deliverable. */
    public static final int DELIVERABLE = 2;

    /** Represents a milestone. */
    public static final int MILESTONE = 3;

    /** Represents an event. */
    public static final int EVENT = 4;

    /** Represents an absence. */
    public static final int ABSENCE = 5;

    /** Represents a holiday. */
    public static final int HOLIDAY = 6;

    /** Represents nothing. */
    public static final int NO = 7;

    /** Represents an issue. */
    public static final int NOTHIT = 8;

    /** Represents someting reported from the past. */
    public static final int REPORTED = 9;

    /** Represents a phase. */
    public static final int PHASE = 10;

    /** Hierarchical level when the item is shown in a list / tree. */
    protected int level;

    /** Item type : refers to class ItemType. */
    public int type;

    /** Priority (on a *..***** scale). */
    protected int priority;

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "item");

        super.xmlOut(xml, transaction, false);

        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "level", level);
            xmlAttribute(xml, "type", type);
            xmlAttribute(xml, "priority", priority);
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

        if (tag.equals("level")) {
            level = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("type")) {
            type = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("priority")) {
            priority = xmlInInt(xml, value);
            return true;
        }

        return false;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) {
            return;
        }

        Item otherItem = (Item)other;

		// Comments are not allowed in order to be
		// able to store information as attributes
		// rather than regular tags
		comment = null;
        super.update(transaction, other);
        this.level = otherItem.level;
        this.type = otherItem.type;
        this.priority = otherItem.priority;
    }

    /** Returns the level of the object. */
    public int getLevel() {
        return level;
    }

    /** Indicates if the item requires attention in the plan. */
    public static boolean isAlive(int type) {
        return (type == ITEMTASK) || (type == DELIVERABLE) || 
            (type == MILESTONE);
    }

    /** Indicates if the item requires a workload in the plan. */
    public static boolean needsWorkload(int type) {
        return (type == ITEMTASK) || (type == DELIVERABLE);
    }

    /** Provides a summary when the object is displayed in a list. */
    public String getSummary() {
        return getName();
    }
}
