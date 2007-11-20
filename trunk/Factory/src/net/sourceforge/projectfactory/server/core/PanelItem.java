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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/core/PanelItem.java,v $
$Revision: 1.5 $
$Date: 2007/02/08 15:58:04 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.core;

import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;

/** 
  * Item defined within a panel.
  * Generated by David Lambert
  * On Thursday, February 8, 2007 9:36:09 AM CET
  * Using Factory 0.4.8.8
  */
public class PanelItem extends BaseEntity {
    public String xmlTag;
    public String label;
    public String type;
    public boolean readOnly;
    public boolean invisible;
    public boolean mustSave;
    public String comboBox;
    public String grid;
    public String lookupClass;

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "content");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "xmltag", xmlTag);
            xmlAttribute(xml, "label", label);
            xmlAttribute(xml, "type", type);
            xmlAttribute(xml, "readonly", readOnly);
            xmlAttribute(xml, "invisible", invisible);
            xmlAttribute(xml, "mustsave", mustSave);
            xmlAttribute(xml, "combobox", comboBox);
            xmlAttribute(xml, "grid", grid);
            xmlAttribute(xml, "lookupclass", lookupClass);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("xmltag")) {
            xmlTag = value;
            return true;
        }
        if (tag.equals("label")) {
            label = value;
            return true;
        }
        if (tag.equals("type")) {
            type = value;
            return true;
        }
        if (tag.equals("readonly")) {
            readOnly = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("invisible")) {
            invisible = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("mustsave")) {
            mustSave = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("combobox")) {
            comboBox = value;
            return true;
        }
        if (tag.equals("grid")) {
            grid = value;
            return true;
        }
        if (tag.equals("lookupclass")) {
            lookupClass = value;
            return true;
        }
        return false;
    }

    /** Returns the grid attached to the item. */
    Grid getGrid(TransactionXML transaction) {
        for (Grid item: transaction.getServer().core.grids)
            if(item.getName().equals(grid))
                return item;
        return null;
    }

    /** Returns the label. */
    String getLabel() {
        return label != null ? label : "";
    }

    /** Returns the XML tag. */
    String getTag() {
        return xmlTag != null ? xmlTag : "";
    }
}
