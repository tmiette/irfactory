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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actions/ActionItemBase.java,v $
$Revision: 1.1 $
$Date: 2007/02/27 22:13:11 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.actions;

import net.sourceforge.projectfactory.server.entities.Duration;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;

/** 
  * Part of the forecast calendar attached to an action, 
  * which contains individual and daily information 
  * including date, task and assignment.
  * @author David Lambert
  */
public class ActionItemBase extends Duration {
    public java.util.Date dateItem;
    public int completion;
    public String assigned;

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "item");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "dateitem", dateItem);
            xmlAttribute(xml, "completion", completion);
            xmlAttribute(xml, "assigned", assigned);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("dateitem")) {
            dateItem = xmlInDate(xml, value);
            return true;
        }
        if (tag.equals("completion")) {
            completion = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("assigned")) {
            assigned = value;
            return true;
        }
        return false;
    }
}
