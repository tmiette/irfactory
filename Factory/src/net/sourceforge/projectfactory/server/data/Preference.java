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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/data/Preference.java,v $
$Revision: 1.2 $
$Date: 2007/02/26 17:21:56 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.data;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;

/** 
  * User preferences. Preferences are stored locally on the system.
  * @author David Lambert
  */
public class Preference extends Entity {
    public int posX;
    public int posY;
    public int lenX;
    public int lenY;
    public int posLookup;
    public int posMessages;
    public String selectionCategory;
    public String displayOptions;
    public String displayPeriod;
    public int count;
    public java.util.List<PreferenceFilter> filters = new java.util.ArrayList();

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "preference");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "posx", posX);
            xmlOut(xml, "posy", posY);
            xmlOut(xml, "lenx", lenX);
            xmlOut(xml, "leny", lenY);
            xmlOut(xml, "poslookup", posLookup);
            xmlOut(xml, "posmessages", posMessages);
            xmlOut(xml, "selectioncategory", selectionCategory);
            xmlOut(xml, "displayoptions", displayOptions);
            xmlOut(xml, "displayperiod", displayPeriod);
            xmlOut(xml, "count", count);
            xmlOut(xml, transaction, filters);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("posx")) {
            posX = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("posy")) {
            posY = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("lenx")) {
            lenX = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("leny")) {
            lenY = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("poslookup")) {
            posLookup = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("posmessages")) {
            posMessages = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("selectioncategory")) {
            selectionCategory = value;
            return true;
        }
        if (tag.equals("displayoptions")) {
            displayOptions = value;
            return true;
        }
        if (tag.equals("displayperiod")) {
            displayPeriod = value;
            return true;
        }
        if (tag.equals("count")) {
            count = xmlInInt(xml, value);
            return true;
        }
        return false;
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("preferencefilter"))
            return new BaseEntityServerXML(transaction, new PreferenceFilter(),filters);
        return null;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) return;
        Preference otherEntity = (Preference) other;
        super.update(transaction, other);
        this.posX = otherEntity.posX;
        this.posY = otherEntity.posY;
        this.lenX = otherEntity.lenX;
        this.lenY = otherEntity.lenY;
        this.posLookup = otherEntity.posLookup;
        this.posMessages = otherEntity.posMessages;
        this.selectionCategory = otherEntity.selectionCategory;
        this.displayOptions = otherEntity.displayOptions;
        this.displayPeriod = otherEntity.displayPeriod;
        this.count = otherEntity.count;
        update(this.filters,otherEntity.filters);
    }
}
