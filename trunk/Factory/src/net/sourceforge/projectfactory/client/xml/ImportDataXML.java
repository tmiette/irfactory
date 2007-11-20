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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/xml/ImportDataXML.java,v $
$Revision: 1.10 $
$Date: 2006/12/06 09:12:01 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.xml;

import java.util.Date;

import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.client.components.LocalMessage;
import net.sourceforge.projectfactory.xml.FactoryReaderXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * XML Parser used to read data from client side.
 * @author David Lambert
 */
public class ImportDataXML extends FactoryReaderXML {

    /** Main frame of the application. */
    protected FrameMain frame;

    /** Constructor. */
    public ImportDataXML(FrameMain frame) {
        this.frame = frame;
    }

    /** Starts a tag. */
    protected void startsTag(String tag) {
        if (tag.equals(FactoryWriterXML.RESPONSE)) {
            return;
        } else {
            frame.showPanel(tag);
            new ImportDataItemXML().xmlIn(this);
        }
    }

    /**
	 * XML Parser used to read details of data from client side.
	 */
    private class ImportDataItemXML extends FactoryReaderXML {

        /** Name of object retieved from XML stream,
		  * used in order to assign a title to main frame. */
        private String name = "";

        /** Starts a tag. */
        protected void startsTag(String tag) {
            if (tag.equals("date")) {
                new ImportDataCalendarItemXML().xmlIn(this);
            }
            else frame.getCurrent().xmlTagIn(tag, this);
        }

        /** Interprets a tag. */
        protected void getTag(String tag, String text) {
            if (tag.equals("name"))
                name = text;
            frame.getCurrent().xmlIn(tag, text);
        }

        /** Ends the tag interpretation. */
        protected void end() {
            frame.getCurrent().exitXmlIn();
            frame.reloadCalendar();
            frame.setTitleDocument(name);
        }
    }

    /**
     * XML Parser used to read calendar information from client side.
     */
    private class ImportDataCalendarItemXML extends FactoryReaderXML {

        /** Date to be interpreted in the calendar. */
        private Date dateCalendar;

        /** Message in plain text. */
        private String message = "";

        /** Label to be retrieved from dictionary. */
        private String label = "";

        /** Actor or anonymous. */
        private String who = "";

        /** Item type. */
        private int type;

        /** Duration. */
        private int duration;

        /** Duration type. */
        private int durationType;

        /** % Complete. */
        private int complete;

        /** Interprets a tag. */
        protected void getTag(String tag, String text) {
            if (tag.equals("date")) {
                try {
                    dateCalendar = XMLWrapper.dsUS.parse(text);
                } catch (java.text.ParseException ex) {
                    dateCalendar = null;
                }
            } else if (tag.equals("message")) {
                message = text;
            } else if (tag.equals("label")) {
                label = text;
            } else if (tag.equals("who")) {
                who = text;
            } else if (tag.equals("type")) {
                try {
                    type = Integer.parseInt(text);
                } catch (java.lang.NumberFormatException ex) {
                    type = 0;
                }
            } else if (tag.equals("duration")) {
                try {
                    duration = Integer.parseInt(text);
                } catch (java.lang.NumberFormatException ex) {
                    duration = 0;
                }
            } else if (tag.equals("durationtype")) {
                try {
                    durationType = Integer.parseInt(text);
                } catch (java.lang.NumberFormatException ex) {
                    durationType = 0;
                }
            } else if (tag.equals("complete")) {
                try {
                    complete = Integer.parseInt(text);
                } catch (java.lang.NumberFormatException ex) {
                    complete = 0;
                }
            }
        }

        /** Ends the tag interpretation. */
        protected void end() {
            String output = label;

            if (message.length() > 0) {
                output += LocalMessage.get(message);
            }

            if ((output.length() > 0) && (who.length() > 0)) {
                output += (" [" + who + "]");
            }

            if ((output.length() > 0) && (duration > 0)) {
                output += (": " + duration);

                switch (durationType) {
                case 1:
                    output += LocalMessage.get("label:day:short");

                    break;

                case 2:
                    output += LocalMessage.get("label:halfday:short");

                    break;

                case 3:
                    output += LocalMessage.get("label:hour:short");

                    break;
                }
            }

            frame.addCalendarMessage(dateCalendar, output, label, who, type, 
                                     duration, durationType, complete);
        }
    }
}
