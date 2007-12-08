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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/data/ActionBarBase.java,v $
$Revision: 1.2 $
$Date: 2007/03/04 21:04:11 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.data;

import net.sourceforge.projectfactory.server.data.ActionBarItem;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;

/** 
  * Defines an action available in the action bar.
  * @author David Lambert
  */
public class ActionBarBase extends Entity {
    public String application;
    public String labelButton;
    public String icon;
    public int orderNumber;
    public boolean administratorOnly;
    public boolean localOnly;
    public java.util.List<ActionBarItem> items = new java.util.ArrayList();

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "actionbar");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "application", application);
            xmlOut(xml, "labelbutton", labelButton);
            xmlOut(xml, "icon", icon);
            xmlOut(xml, "ordernumber", orderNumber);
            xmlOut(xml, "administratoronly", administratorOnly);
            xmlOut(xml, "localonly", localOnly);
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
        if (tag.equals("labelbutton")) {
            labelButton = value;
            return true;
        }
        if (tag.equals("icon")) {
            icon = value;
            return true;
        }
        if (tag.equals("ordernumber")) {
            orderNumber = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("administratoronly")) {
            administratorOnly = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("localonly")) {
            localOnly = xmlInBoolean(value);
            return true;
        }
        return false;
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("item"))
            return new BaseEntityServerXML(transaction, new ActionBarItem(),items);
        return null;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) return;
        ActionBarBase otherEntity = (ActionBarBase) other;
        super.update(transaction, other);
        this.application = otherEntity.application;
        this.labelButton = otherEntity.labelButton;
        this.icon = otherEntity.icon;
        this.orderNumber = otherEntity.orderNumber;
        this.administratorOnly = otherEntity.administratorOnly;
        this.localOnly = otherEntity.localOnly;
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
}
