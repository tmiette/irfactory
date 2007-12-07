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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/xml/ImportListXML.java,v $
$Revision: 1.15 $
$Date: 2007/02/22 15:37:39 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.xml.client;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.projectfactory.client.components.LocalMessage;
import net.sourceforge.projectfactory.client.components.PanelDataLookup;
import net.sourceforge.projectfactory.client.components.ThreadLookup;
import net.sourceforge.projectfactory.xml.ReaderXML;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Interprets a list of elements coming from a XML stream.
 * Includes, adds or attachs the elements to a recipient (panel, thread...).
 * @author David Lambert
 */
public class ImportListXML extends ReaderXML {

    /** Thread defined as recipient of the list interpreter. */
    private ThreadLookup lookupThread;

    /** Panel defined as recipient of the list interpreter. */
    private PanelDataLookup lookupPanel;

    /** Internal element counter (selected elements). */
    private int counter;

    /** Internal element counter (total of elements). */
    private int totalCounter;
    
    /** Operator Id provided from the server (Iid). */
    private String operatorId = "";

    /** Operator name provided from the server. */
    private String operatorName = "";

    /** When there is no recipient, elements are stored in a list. */
    private List<ListItem> items = new ArrayList(50);

    /** Constructor. */
    public ImportListXML(ThreadLookup lookupThread, 
                         PanelDataLookup lookupPanel) {
        this.lookupThread = lookupThread;
        this.lookupPanel = lookupPanel;
    }

    /** Starts a tag. */
    protected void startsTag(String tag) {
        if (tag.equals(WriterXML.RESPONSE) || tag.equals("list")) {
            return;
        } else if (!tag.equals("message") && !tag.equals("counter") && 
                   !tag.equals("totalcounter") && !tag.equals("operatorid") &&
                   !tag.equals("operatorname")) {
            new ImportListItemXML(tag).xmlIn(this);
        }
    }

    /** Interprets a tag. */
    protected void getTag(String tag, String text) {
        if (tag.equals("message")) {
            if (lookupThread != null)
                lookupThread.addSelectionItem(tag, "", "", 
                                              LocalMessage.get(text), "", 1);
        } else if (tag.equals("counter")) {
            counter = Integer.parseInt(text);
        } else if (tag.equals("totalcounter")) {
            totalCounter = Integer.parseInt(text);
        } else if (tag.equals("operatorid")) {
            operatorId = text;
        } else if (tag.equals("operatorname")) {
            operatorName = text;
        }
    }

    /** Returns the counter of elements. */
    public int getCounter() {
        return counter;
    }

    /** Returns the counter of total elements. */
    public int getTotalCounter() {
        return totalCounter;
    }

    /** Returns the operator Id. */
    public String getOperatorId() {
        return operatorId;
    }

    /** Returns the operator name. */
    public String getOperatorName() {
        return operatorName;
    }

    /** Returns the number of elements in the internal list. */
    public int getNamesCount() {
        return items.size();
    }

    /** Returns the class of the element stored in the internal list. */
    public String getClassName(int i) {
        return items.get(i).className;
    }

    /** Returns the element stored in the internal list. */
    public String getName(int i) {
        return items.get(i).name;
    }

    /** Returns the element stored in the internal list. */
    public String getIid(int i) {
        return items.get(i).iid;
    }

    /** Cleans lists when some elements are identical. */
    public void cleanDuplicates(ImportListXML listOther) {
        for (int i = 0; i < items.size(); i++) {
            for (int j = 0; j < listOther.items.size(); j++) {
                if (items.get(i).equals((listOther.items.get(j)))) {
                    items.remove(i--);
                    listOther.items.remove(j--);
                    break;
                }
            }
        }
    }

    /**
     * Defines an item stored in a list.
     */
    private class ListItem {

        /** Class of the element. */
        private String className;

        /** Object ID. */
        private String iid;

        /** Name of the element. */
        private String name;

        /** Update time stamp. */
        private Date updated;

        ListItem(String className, String iid, String name, Date updated) {
            this.className = className;
            this.iid = iid;
            this.name = name;
            this.updated = updated;
        }

        /** Compares this entity to the specified object. */
        public boolean equals(Object object) {
            if (object == this)
                return true;

            if (object == null || name == null)
                return false;

            ListItem otherItem = (ListItem)object;

            if (!this.className.equals(otherItem.className))
                return false;

            if(iid == null || otherItem.iid == null) {
                if (!this.name.equals(otherItem.name))
                    return false;
            }
            
            if (!this.iid.equals(otherItem.iid))
                return false;

            if (updated == null && otherItem.updated == null)
                return true;

            if (updated == null || otherItem.updated == null)
                return false;

            return this.updated.equals(otherItem.updated);
        }
    }

    /**
     * Interprets an list element coming from a XML stream.
     */
    class ImportListItemXML extends ReaderXML {

        /** Category of the element. */
        private String category;

        /** Class of the element. */
        private String className;

        /** Name of the element. */
        private String name;

        /** Object ID. */
        private String iid;

        /** Summary of the element used for display. */
        private String summary;

        /** Tip displayed in the list. */
        private String tip;

        /** Update time stamp. */
        private Date updated;

        /** Indicates if the element has been added of not. */
        private boolean added;

        /** Level of the element used for hierarchical lists. */
        private int level;

        /** Constructor. */
        ImportListItemXML(String category) {
            this.className = category;
            this.category = category;
            this.level = 1;
        }

        /** Constructor with level. */
        ImportListItemXML(String category, int level) {
            this(category);
            this.level = level;
        }

        /** Starts a tag. */
        protected void startsTag(String tag) {
            if (!tag.equals("name") && !tag.equals("iid") && 
                !tag.equals("summary") && !tag.equals("tip") && 
                !tag.equals("updated") && !tag.equals("message") && 
                !tag.equals("counter") && !tag.equals("totalcounter")) {
				end();
                new ImportListItemXML(tag, 1 + level).xmlIn(this);
            }
        }

        /** Interprets a tag. */
        protected void getTag(String tag, String text) {
            if (tag.equals("name")) {
                this.name = text;
            } else if (tag.equals("iid")) {
                this.iid = text;
            } else if (tag.equals("summary")) {
                this.summary = text;
            } else if (tag.equals("tip")) {
                this.tip = this.tip == null ? text : this.tip + " " + text;
            } else if (tag.equals("updated")) {
                try {
                    this.updated = XMLWrapper.dfUS.parse(text);
                } catch (ParseException e) {
                    this.updated = null;
                }
            }
        }

        /** Ends the tag interpretation. */
        protected void end() {
            if (!added && category != null && name != null) {
                added = true;
                if (lookupThread != null)
                    lookupThread.addSelectionItem(category, iid, name, summary, 
                                                  tip, level);
                else if (lookupPanel != null) 
                    lookupPanel.selectItem(category, iid, name, summary, tip);
                else
                    items.add(new ListItem(className, iid, name, updated));
            }
        }
    }
}
