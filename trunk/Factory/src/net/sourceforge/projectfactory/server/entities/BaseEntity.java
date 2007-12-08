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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/entities/BaseEntity.java,v $
$Revision: 1.21 $
$Date: 2007/01/31 23:58:17 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.entities;

import java.text.ParseException;

import java.util.Date;
import java.util.List;

import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Base class for every object managed by the server.
 * @author David Lambert
 */
public abstract class BaseEntity {

    /** Sets entity name. */
    public void setName(String name) {
    }

    /** Prepares the element for output in the data stream. */
    protected final BaseEntity xmlStart(WriterXML xml, String tag) {
        if (xml != null)
            xml.xmlStart(tag);
        return this;
    }

    /** Adds an attribute. */
    public BaseEntity xmlAttribute(WriterXML xml, String tag, 
                                   String value) {
        if (xml != null)
            xml.xmlAttribute(tag, value);
        return this;
    }

    /** Adds an attribute. */
    public BaseEntity xmlAttribute(WriterXML xml, String tag, 
                                   int value) {
        if (xml != null && value != 0)
            xml.xmlAttribute(tag, value);
        return this;
    }

    /** Adds an attribute. */
    public BaseEntity xmlAttribute(WriterXML xml, String tag, 
                                   boolean value) {
        if (value && xml != null)
            xml.xmlAttribute(tag, "y");
        return this;
    }

    /** Adds an attribute. */
    public BaseEntity xmlAttribute(WriterXML xml, String tag, 
                                   Date value) {
        if(xml != null)
            xmlAttribute(xml, tag, 
                     value != null ? XMLWrapper.dsUS.format(value).toString() : 
                     "");
        return this;
    }

    /** Adds an attribute. */
    public BaseEntity xmlAttributeFull(WriterXML xml, String tag, 
                                       Date value) {
        if(xml != null)
            xmlAttribute(xml, tag, 
                     value != null ? XMLWrapper.dfUS.format(value).toString() : 
                     "");
        return this;
    }

    /** Adds an entity into the current element. */
    public BaseEntity xmlAttribute(WriterXML xml, 
                                   TransactionXML transaction, String tag, 
                                   Entity value) {
        if (value != null && xml != null)
            xml.xmlAttribute(tag, 
                             (transaction.isSave() && value.getIid() != null ? 
                              ("::" + value.getIid() + ":") : "") + 
                             value.getName());
        return this;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
    }

    /** Adds an element (tag / value) into the current element
     *  as like as an attribute. */
    public BaseEntity xmlOut(WriterXML xml, String tag, String value) {
        if (xml != null && value != null)
            xml.xmlOut(tag, value);
        return this;
    }

    /** Adds an element (tag / value) into the current element
     *  as like as an attribute. */
    public BaseEntity xmlOut(WriterXML xml, String tag, boolean value) {
        if (value && xml != null)
            xml.xmlOut(tag, "y");
        return this;
    }

    /** Adds an entity into the current element. */
    public BaseEntity xmlOut(WriterXML xml, TransactionXML transaction, 
                             String tag, Entity value) {
        if (value != null && xml != null)
            xml.xmlOut(tag, 
                       (transaction.isSave() && value.getIid() != null ? ("::" + 
                                                                          value.getIid() + 
                                                                          ":") : 
                        "") + value.getName());
        return this;
    }

    /** Adds a date into the current element. */
    public BaseEntity xmlOut(WriterXML xml, String tag, Date value) {
        if(xml != null)
            xmlOut(xml, tag, 
               value != null ? XMLWrapper.dsUS.format(value).toString() : "");
        return this;
    }

    /** Adds a long dateinto the current element. */
    public BaseEntity xmlOutFull(WriterXML xml, String tag, 
                                 Date value) {
        if(xml != null)
            xmlOut(xml, tag, 
               value != null ? XMLWrapper.dfUS.format(value).toString() : "");
        return this;
    }

    /** Adds an integer into the current element. */
    public BaseEntity xmlOut(WriterXML xml, String tag, int value) {
        if (xml != null && value != 0)
            xmlOut(xml, tag, Integer.toString(value));
        return this;
    }

    /** Adds a list of elements into the current element
     *  as like as an attribute. */
    public BaseEntity xmlOut(WriterXML xml, 
                                TransactionXML transaction, 
                                List list) {
        for (BaseEntity item: (List<BaseEntity>)list)
            item.xmlOut(xml, transaction, true);
        return this;
    }

    /** Finishes the output of the element. */
    protected final void xmlEnd(WriterXML xml) {
        if (xml != null)
            xml.xmlEnd();
    }

    /** Gets a string value from an XML input. */
    protected final String xmlInString(String value) {
        return value;
    }

    /** Gets an integer value from an XML input. */
    protected final int xmlInInt(WriterXML xml, String value) {
        try {
            return Integer.parseInt(value);
        } catch (java.lang.NumberFormatException e) {
            xmlError(xml, "error:incorrect:number", value);
            return 0;
        }
    }

    /** Get a date value from an XML input (short format). */
    protected final Date xmlInDate(WriterXML xml, String value) {
        try {
            return XMLWrapper.dsUS.parse(value);
        } catch (ParseException e) {
            xmlError(xml, "error:incorrect:date", "", value);
            return null;
        }
    }

    /** Gets a date value from an XML input (long format). */
    protected final Date xmlInDateFull(WriterXML xml, String value) {
        try {
            return XMLWrapper.dfUS.parse(value);
        } catch (ParseException e) {
            xmlError(xml, "error:incorrect:date", "", value);
            return null;
        }
    }

    /** Gets a boolean value from an XML input. */
    protected final boolean xmlInBoolean(String value) {
        return (value.equals("y"));
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        return false;
    }
    
    /** Gets an entity from an XML input. */
    protected final BaseEntity xmlInEntity(WriterXML xml,
                                            TransactionXML transaction,
                                            String value, 
                                            BaseEntity newEntity, 
                                            List list,
                                            String message,
                                            Entity parent) {
        if(list != null && newEntity != null) {
            newEntity.setName(value);
            int index = list.indexOf(newEntity);
            if (index >= 0) {
                return (BaseEntity) list.get(index);
            } else if (parent != null && message != null && 
                        parent.isActive() && !transaction.isDelete()) {
                xmlError(xml, message, value, parent.getName());
            }
        }
        return null;
    }

    /** Gets an entity from an XML input, create it during loading. */
    protected final BaseEntity xmlInEntityCreate(WriterXML xml,
                                            TransactionXML transaction,
                                            String value, 
                                            BaseEntity newEntity, 
                                            List list,
                                            String message,
                                            Entity parent) {
        newEntity.setName(value);
        int index = list.indexOf(newEntity);
        if (index >= 0) {
            return (BaseEntity) list.get(index);
        } else if (transaction.isLoad() || transaction.isReplicate()) {
            list.add(newEntity);
            return newEntity;
        } else if (parent != null && message != null && 
                    parent.isActive() && !transaction.isDelete()) {
            xmlError(xml, message, value, parent.getName());
        }
        return null;
    }

    /** Gets an entity from an XML input. */
    protected final BaseEntity xmlInEntity(WriterXML xml,
                                            TransactionXML transaction,
                                            String value, 
                                            BaseEntity newEntity, 
                                            List list) {
        return xmlInEntity(xml, transaction, value, newEntity, list, null, null);
    }

    /**
     * Updates a list using another one.
	 * The objects of the second list are attached in the first one.
     */
    protected void update(List list, List listOther) {
        list.clear();
        list.addAll(listOther);
    }

    /** Sends an error message in the XML output. */
    protected final void xmlError(WriterXML xml, String error, 
                                  String... args) {
        xml.xmlMessage(WriterXML.ERROR, error, args);
    }

    /** Sends a warning message in the XML output. */
    protected final void xmlWarning(WriterXML xml, String error, 
                                    String... args) {
        xml.xmlMessage(WriterXML.WARNING, error, args);
    }

    /** Sends a message in the XML output. */
    protected final void xmlMessage(WriterXML xml, String error, 
                                  String... args) {
        xml.xmlMessage(WriterXML.MESSAGE, error, args);
    }

    /** Sends a trace message in the XML output. */
    protected final void xmlTrace(WriterXML xml, String error, 
                                  String... args) {
        xml.xmlMessage(WriterXML.TRACE, error, args);
    }

    /** Returns a name (empty). */
    public String getName() {
        return "";
    }

    /** Writes a label associated to a calendar item as an XML output. */
    protected final void xmlCalendar(WriterXML xml, Date value, 
                                     String message) {
        if (value != null) {
            xmlStart(xml, "date");
            xmlAttribute(xml, "date", value);
            xmlAttribute(xml, "message", message);
            xmlEnd(xml);
        }
    }

    /** Writes a message associated to a calendar item as an XML output. */
    protected final void xmlCalendar(WriterXML xml, Date value, 
                                     String label, String message, 
                                     int complete) {
        if (value != null) {
            xmlStart(xml, "date");
            xmlAttribute(xml, "date", value);
            xmlAttribute(xml, "label", label);
            xmlAttribute(xml, "message", message);
            xmlAttribute(xml, "complete", complete);
            xmlEnd(xml);
        }
    }

    /** Writes a message associated to a calendar item,
     *  including actor and duration as an XML output. */
    protected final void xmlCalendar(WriterXML xml, Date value, 
                                     String label, int type, String who, 
                                     int duration, int durationType, 
                                     int complete) {
        if (value != null) {
            xmlStart(xml, "date");
            xmlAttribute(xml, "date", value);
            xmlAttribute(xml, "label", label);
            xmlAttribute(xml, "type", type);
            xmlAttribute(xml, "who", who);
            xmlAttribute(xml, "duration", duration);
            xmlAttribute(xml, "durationtype", durationType);
            xmlAttribute(xml, "complete", complete);
            xmlEnd(xml);
        }
    }

    /** Writes a message associated to a calendar item,
     *  including actor and duration as an XML output. */
    protected final void xmlCalendar(WriterXML xml, Date value, 
                                     String label, String message, String who, 
                                     int complete) {
        if (value != null) {
            xmlStart(xml, "date");
            xmlAttribute(xml, "date", value);
            xmlAttribute(xml, "label", label);
            xmlAttribute(xml, "who", who);
            xmlAttribute(xml, "complete", complete);
            xmlAttribute(xml, "message", message);
            xmlEnd(xml);
        }
    }
}
