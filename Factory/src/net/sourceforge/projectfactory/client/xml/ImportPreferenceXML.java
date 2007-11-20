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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/xml/ImportPreferenceXML.java,v $
$Revision: 1.11 $
$Date: 2007/01/31 23:59:25 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.xml;

import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.xml.FactoryReaderXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * XML Parser used to read preferences from client side.
 * @author David Lambert
 */
public class ImportPreferenceXML extends FactoryReaderXML {

    /** Main frame of the application. */
    protected FrameMain frame;

    /** Constructor. */
    public ImportPreferenceXML(FrameMain frame) {
        this.frame = frame;
    }

    /** Starts a tag. */
    public void startsTag(String tag) {
        if (tag.equals(FactoryWriterXML.RESPONSE)) {
            return;
        } else {
            new ImportPreferenceDetailXML().xmlIn(this);
        }
    }

    /**
	 * XML Parser used to read details of preferences from client side.
	 */
    private class ImportPreferenceDetailXML extends FactoryReaderXML {

        /** Starts a tag. */
        public void startsTag(String tag) {
            if (tag.equals("preferencefilter")) {
                new ImportPreferenceFilterXML().xmlIn(this);
            }
        }

        /** Interprets a tag. */
        public void getTag(String tag, String text) {
            int valueInt;

            try {
                valueInt = Integer.parseInt(text);
            } catch (java.lang.NumberFormatException ex) {
                valueInt = 0;
            }

            if (valueInt > 0) {
                if (tag.equals("posx")) {
                    frame.setLocation(valueInt, frame.getY());
                } else if (tag.equals("posy")) {
                    frame.setLocation(frame.getX(), valueInt);
                } else if (tag.equals("lenx")) {
                    frame.setSize(valueInt, frame.getHeight());
                } else if (tag.equals("leny")) {
                    frame.setSize(frame.getWidth(), valueInt);
                } else if (tag.equals("poslookup")) {
                    frame.splitVertical.setDividerLocation(valueInt);
                } else if (tag.equals("posmessages")) {
                    frame.splitHorizontal.setDividerLocation(valueInt);
                } else if (tag.equals("displayoptions")) {
                    frame.comboDisplayOptions.setSelectedCode(text);
                } else if (tag.equals("displayperiod")) {
                    frame.comboDisplayPeriod.setSelectedCode(text);
                } else if (tag.equals("count")) {
                    frame.count.setText(Integer.toString(valueInt + 1));
                }
            } else if (tag.equals("selectioncategory")) {
                frame.selectionCategory = text;
            }
        }

        /**
		 * XML Parser used to read filter preferences from client side.
		 */
        class ImportPreferenceFilterXML extends FactoryReaderXML {

            /** Category. */
            private String category = "";

            /** Search string. */
            private String search = "";

            /** Filter string. */
            private String filter = "";

            /** Interprets a tag. */
            public void getTag(String tag, String text) {
                if (tag.equals("category")) {
                    category = text;
                } else if (tag.equals("filter")) {
                    filter = text;
                } else if (tag.equals("search")) {
                    search = text;
                }
            }

            /** Ends the tag interpretation. */
            protected void end() {
                frame.setLookupParams(category, filter, search);
            }
        }
    }
}
