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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/core/ClassDefinitionBase.java,v $
$Revision: 1.2 $
$Date: 2007/03/04 21:03:29 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.core;

import net.sourceforge.projectfactory.server.core.ClassItem;
import net.sourceforge.projectfactory.server.core.ClassValue;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;

/** 
  * Represents a class definition.
  * @author David Lambert
  */
public class ClassDefinitionBase extends CoreEntity {
    public String classType;
    public String superClass;
    public String xmlTag;
    public boolean uniqueName;
    public boolean createDerived;
    public java.util.List<ClassItem> items = new java.util.ArrayList();
    public java.util.List<ClassValue> staticValues = new java.util.ArrayList();

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "classdefinition");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "classtype", classType);
            xmlOut(xml, "superclass", superClass);
            xmlOut(xml, "xmltag", xmlTag);
            xmlOut(xml, "uniquename", uniqueName);
            xmlOut(xml, "createderived", createDerived);
            xmlOut(xml, transaction, items);
            xmlOut(xml, transaction, staticValues);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("classtype")) {
            classType = value;
            return true;
        }
        if (tag.equals("superclass")) {
            superClass = value;
            return true;
        }
        if (tag.equals("xmltag")) {
            xmlTag = value;
            return true;
        }
        if (tag.equals("uniquename")) {
            uniqueName = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("createderived")) {
            createDerived = xmlInBoolean(value);
            return true;
        }
        return false;
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("content"))
            return new BaseEntityServerXML(transaction, new ClassItem(),items);
        if (tag.equals("classvalue"))
            return new BaseEntityServerXML(transaction, new ClassValue(),staticValues);
        return null;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) return;
        ClassDefinitionBase otherEntity = (ClassDefinitionBase) other;
        super.update(transaction, other);
        this.classType = otherEntity.classType;
        this.superClass = otherEntity.superClass;
        this.xmlTag = otherEntity.xmlTag;
        this.uniqueName = otherEntity.uniqueName;
        this.createDerived = otherEntity.createDerived;
        update(this.items,otherEntity.items);
        update(this.staticValues,otherEntity.staticValues);
    }
}
