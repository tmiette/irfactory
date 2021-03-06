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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/EMail.java,v $
$Revision: 1.10 $
$Date: 2007/01/29 22:26:56 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.resources;

import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;

/** Email address.
  * Generated by David Lambert
  * On Monday, January 29, 2007 9:10:42 AM CET
  * Using Factory 0.4.8.4
  */
public class EMail extends BaseEntity {
    public String address;
    public boolean principal;

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "email");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "address", address);
            xmlAttribute(xml, "principal", principal);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("address")) {
            address = value;
            return true;
        }
        if (tag.equals("principal")) {
            principal = xmlInBoolean(value);
            return true;
        }
        return false;
    }
}
