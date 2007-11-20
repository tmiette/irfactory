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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/core/PanelBase.java,v $
$Revision: 1.2 $
$Date: 2007/02/15 13:51:30 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.core;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;

/** 
  * Represents a panel definition.
  * @author David Lambert
  */
public class PanelBase extends CoreEntity {
    public boolean canEdit;
    public boolean canDelete;
    public boolean canSave;
    public java.util.List<PanelItem> items = new java.util.ArrayList();
    public java.util.List<Button> buttons = new java.util.ArrayList();
    public java.util.List<ButtonParameter> buttonParameters = new java.util.ArrayList();
    public String xmlTagAction;
    public String exitXml;

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "panel");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "canedit", canEdit);
            xmlOut(xml, "candelete", canDelete);
            xmlOut(xml, "cansave", canSave);
            xmlOut(xml, "xmltagaction", xmlTagAction);
            xmlOut(xml, "exitxml", exitXml);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("canedit")) {
            canEdit = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("candelete")) {
            canDelete = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("cansave")) {
            canSave = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("xmltagaction")) {
            xmlTagAction = value;
            return true;
        }
        if (tag.equals("exitxml")) {
            exitXml = value;
            return true;
        }
        return false;
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("content"))
            return new BaseEntityServerXML(transaction, new PanelItem(),items);
        if (tag.equals("button"))
            return new BaseEntityServerXML(transaction, new Button(),buttons);
        if (tag.equals("buttonparameter"))
            return new BaseEntityServerXML(transaction, new ButtonParameter(),buttonParameters);
        return null;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) return;
        Panel otherEntity = (Panel) other;
        super.update(transaction, other);
        this.canEdit = otherEntity.canEdit;
        this.canDelete = otherEntity.canDelete;
        this.canSave = otherEntity.canSave;
        update(this.items,otherEntity.items);
        update(this.buttons,otherEntity.buttons);
        update(this.buttonParameters,otherEntity.buttonParameters);
        this.xmlTagAction = otherEntity.xmlTagAction;
        this.exitXml = otherEntity.exitXml;
    }
}
