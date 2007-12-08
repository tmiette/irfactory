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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actions/ActionItem.java,v $
$Revision: 1.3 $
$Date: 2007/02/27 22:13:11 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.actions;

import net.sourceforge.projectfactory.server.projects.Item;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;


/**
 * Part of the forecast calendar attached to an action,
 * which contains individual and daily information
 * including date, task and assignment.
 * @author David Lambert
 */
public class ActionItem extends ActionItemBase {

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                        boolean tags) {
        super.xmlOut(xml, transaction, tags);
        if (transaction.isDetail())
            xmlCalendar(xml, 
                        dateItem, 
                        purpose, 
                        Item.ITEMTASK, 
                        assigned, 
                        duration, 
                        durationType, 
                        completion);
    }
}
