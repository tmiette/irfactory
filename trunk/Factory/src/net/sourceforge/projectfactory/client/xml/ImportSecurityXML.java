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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/xml/ImportSecurityXML.java,v $
$Revision: 1.2 $
$Date: 2007/03/04 21:04:51 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.xml;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.client.components.ToggleButtonAction;
import net.sourceforge.projectfactory.client.components.ToggleButtonCategory;
import net.sourceforge.projectfactory.xml.FactoryReaderXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * XML Parser used to read security setup from client side.
 * @author David Lambert
 */
public class ImportSecurityXML extends FactoryReaderXML {

    /** Main frame of the application. */
    protected FrameMain frame;

    /** Actions associated to a button. */    
    private List<ToggleButtonAction> actions = new ArrayList(2);

    /** Constructor. */
    public ImportSecurityXML(FrameMain frame) {
        this.frame = frame;
    }

    /** Starts a tag. */
    public void startsTag(String tag) {
        if (tag.equals(FactoryWriterXML.RESPONSE)) {
            return;
        } else if (tag.equals("actionbar")) {
            new ImportSecurityActionBarXML().xmlIn(this);
        }
    }

    /**
     * XML Parser used to read action bar definition from client side.
     */
    private class ImportSecurityActionBarXML extends FactoryReaderXML {

        /** Category. */
        private String category;

        /** Search string. */
        private String title;

        /** Filter string. */
        private String icon;
        
        /** Starts a tag. */
        public void startsTag(String tag) {
            if (tag.equals("item")) {
                new ImportSecurityButtonActionBarXML().xmlIn(this);
            }
        }

        /** Interprets a tag. */
        public void getTag(String tag, String text) {
            if (tag.equals("name")) {
                category = text;
            } else if (tag.equals("labelbutton")) {
                title = text;
            } else if (tag.equals("icon")) {
                icon = text;
            }
        }

        /** Ends the tag interpretation. */
        protected void end() {
            if(category != null && title != null) {
                ToggleButtonCategory button =
                                            new ToggleButtonCategory(
                                                                    frame, 
                                                                    category, 
                                                                    title, 
                                                                    title + ":tip", 
                                                                    icon,
                                                                    actions);
                frame.addActionBar(button);
                actions = new ArrayList(2);
            }
        }
    }

    /**
     * XML Parser used to read associated button in the action bar.
     */
    private class ImportSecurityButtonActionBarXML extends FactoryReaderXML {

        /** Category. */
        private String button;

        /** Search string. */
        private String panel;

        /** Interprets a tag. */
        public void getTag(String tag, String text) {
            if (tag.equals("button")) {
                button = text;
            } else if (tag.equals("panel")) {
                panel = text;
            }
        }

        /** Ends the tag interpretation. */
        protected void end() {
            if(button != null && panel != null)
                actions.add(new ToggleButtonAction(button, panel));
        }
    }
}
