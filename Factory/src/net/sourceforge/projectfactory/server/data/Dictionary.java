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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/data/Dictionary.java,v $
$Revision: 1.4 $
$Date: 2007/02/26 17:21:56 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.data;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;

/** 
  * Represents an entry in the multi-languages dictionary. 
  * An entry is composed by strings in different languages. 
  * The system will use English string by default.
  * @author David Lambert
  */
public class Dictionary extends Entity {
    public String application;
    public String english;
    public String french;
    public String german;
    public String spanish;
    public String arabic;

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "dictionary");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "application", application);
            xmlOut(xml, "english", english);
            xmlOut(xml, "french", french);
            xmlOut(xml, "german", german);
            xmlOut(xml, "spanish", spanish);
            xmlOut(xml, "arabic", arabic);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("application")) {
            application = value;
            return true;
        }
        if (tag.equals("english")) {
            english = value;
            return true;
        }
        if (tag.equals("french")) {
            french = value;
            return true;
        }
        if (tag.equals("german")) {
            german = value;
            return true;
        }
        if (tag.equals("spanish")) {
            spanish = value;
            return true;
        }
        if (tag.equals("arabic")) {
            arabic = value;
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
        Dictionary otherEntity = (Dictionary) other;
        super.update(transaction, other);
        this.application = otherEntity.application;
        this.english = otherEntity.english;
        this.french = otherEntity.french;
        this.german = otherEntity.german;
        this.spanish = otherEntity.spanish;
        this.arabic = otherEntity.arabic;
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
