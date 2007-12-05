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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/data/ComboBox.java,v $
$Revision: 1.5 $
$Date: 2007/02/26 17:21:56 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.data;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;

/** 
  * Represents a combobox used to display a list of values.
  * @author David Lambert
  */
public class ComboBox extends Entity {
    public String application;
    public boolean blankRow;
    public java.util.List<ComboBoxItem> items = new java.util.ArrayList();

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "combobox");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "application", application);
            xmlOut(xml, "blankrow", blankRow);
            xmlOut(xml, transaction, items);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("application")) {
            application = value;
            return true;
        }
        if (tag.equals("blankrow")) {
            blankRow = xmlInBoolean(value);
            return true;
        }
        return false;
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("item"))
            return new BaseEntityServerXML(transaction, new ComboBoxItem(),items);
        return null;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) return;
        ComboBox otherEntity = (ComboBox) other;
        super.update(transaction, other);
        this.application = otherEntity.application;
        this.blankRow = otherEntity.blankRow;
        update(this.items,otherEntity.items);
    }

    /** Returns the application code. */
    public String getApplication() {
        return application != null ? application : "";
    }

    /** Provides a summary when the object is displayed in a list. */
    public String getSummary() {
        return getSummaryPrefix() + super.getSummary();
    }

    /** Provides a summary prefix when the object is displayed in a list. */
    public String getSummaryPrefix() {
        return "{" + getApplication() + "} ";
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);
        blankRow = true;
    }
}
