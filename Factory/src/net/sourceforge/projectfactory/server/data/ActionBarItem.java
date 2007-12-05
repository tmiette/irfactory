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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/data/ActionBarItem.java,v $
$Revision: 1.1 $
$Date: 2007/03/05 07:24:24 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.data;

import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;

/** 
  * Defines a panel attached to a category.
  * @author David Lambert
  */
public class ActionBarItem extends BaseEntity {
    public String button;
    public String panel;

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "item");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "button", button);
            xmlAttribute(xml, "panel", panel);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("button")) {
            button = value;
            return true;
        }
        if (tag.equals("panel")) {
            panel = value;
            return true;
        }
        return false;
    }
}
