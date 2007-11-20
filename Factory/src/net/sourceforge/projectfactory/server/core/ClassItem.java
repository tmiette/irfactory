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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/core/ClassItem.java,v $
$Revision: 1.5 $
$Date: 2007/03/04 21:03:29 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.core;

import net.sourceforge.projectfactory.server.entities.BaseEntity;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;

/** 
  * Items defined within a class.
  * @author David Lambert
  */
public class ClassItem extends BaseEntity {
    public String xmlTag;
    public String type;
    public boolean fromParent;
    public String classReference;
    public String listName;
    public String errorMessage;

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "content");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "xmltag", xmlTag);
            xmlAttribute(xml, "type", type);
            xmlAttribute(xml, "fromparent", fromParent);
            xmlAttribute(xml, "classreference", classReference);
            xmlAttribute(xml, "listname", listName);
            xmlAttribute(xml, "errormessage", errorMessage);
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
        if (tag.equals("type")) {
            type = value;
            return true;
        }
        if (tag.equals("fromparent")) {
            fromParent = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("classreference")) {
            classReference = value;
            return true;
        }
        if (tag.equals("listname")) {
            listName = value;
            return true;
        }
        if (tag.equals("errormessage")) {
            errorMessage = value;
            return true;
        }
        return false;
    }

    /** Returns the XML tag. */
    String getTag() {
        return xmlTag != null ? xmlTag : "";
    }
}
