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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/core/GridBase.java,v $
$Revision: 1.1 $
$Date: 2007/02/27 22:12:14 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.core;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;

/** 
  * Represents a grid used in panels.
  * @author David Lambert
  */
public class GridBase extends CoreEntity {
    public boolean hierarchical;
    public boolean noSort;
    public boolean noButton;
    public java.util.List<GridItem> items = new java.util.ArrayList();

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "grid");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "hierarchical", hierarchical);
            xmlOut(xml, "nosort", noSort);
            xmlOut(xml, "nobutton", noButton);
            xmlOut(xml, transaction, items);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("hierarchical")) {
            hierarchical = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("nosort")) {
            noSort = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("nobutton")) {
            noButton = xmlInBoolean(value);
            return true;
        }
        return false;
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("content"))
            return new BaseEntityServerXML(transaction, new GridItem(),items);
        return null;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) return;
        GridBase otherEntity = (GridBase) other;
        super.update(transaction, other);
        this.hierarchical = otherEntity.hierarchical;
        this.noSort = otherEntity.noSort;
        this.noButton = otherEntity.noButton;
        update(this.items,otherEntity.items);
    }
}
