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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/core/PanelCode.java,v $
$Revision: 1.3 $
$Date: 2007/03/04 21:03:29 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.core;

import net.sourceforge.projectfactory.server.core.Panel;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;

/** 
  * Code attached to a panel.
  * @author David Lambert
  */
public class PanelCode extends CoreEntity {
    public Panel panelName;
    public String smallComments;
    public String code;

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "panelcode");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, transaction, "panelname", panelName);
            xmlOut(xml, "smallcomments", smallComments);
            xmlOut(xml, "code", code);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("panelname") && transaction.getServer().core != null) {
            panelName = (Panel)xmlInEntity(xml,transaction,value,
                new Panel(),transaction.getServer().core.panels,
                null,this);
            return true;
        }
        if (tag.equals("smallcomments")) {
            smallComments = value;
            return true;
        }
        if (tag.equals("code")) {
            code = value;
            return true;
        }
        return false;
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        return null;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) return;
        PanelCode otherEntity = (PanelCode) other;
        super.update(transaction, other);
        this.panelName = otherEntity.panelName;
        this.smallComments = otherEntity.smallComments;
        this.code = otherEntity.code;
    }
    /** Indicates if the names must be unique in the system or not. */
    protected boolean hasUniqueName() {
        return false;
    }
}
