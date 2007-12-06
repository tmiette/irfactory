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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ThreadLookup.java,v $
$Revision: 1.12 $
$Date: 2007/02/06 17:42:43 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.components;

import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.tableBoxes.TableBox;
import net.sourceforge.projectfactory.client.xml.ImportListXML;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Thread used in order to populate a lookupPanel list.
 * @author David Lambert
 */
public class ThreadLookup implements Runnable {

    /** Selection category used as search criterion. */
    private String selectionCategory;

    /** Class name used as search criterion. */
    private String classname;

    /**Panel lookupPanel used as recipient.
     */
    private PanelDataLookup lookupPanel;

    /** Main frame of the application. */
    private MainFrame frame;

    /**Reference to a table that constains
     * the values to be returned by the lookupPanel.
     */
    private TableBox reference;

    /** Indicates the thread needs to be terminated. */
    private volatile boolean terminate = false;

    /** Indicates the thread is terminated. */
    private volatile boolean terminated = false;

    /** Constructor. */
    public ThreadLookup(PanelDataLookup lookup, MainFrame frame, 
                        String selectionCategory, String classname, 
                        TableBox reference) {
        this.lookupPanel = lookup;
        this.frame = frame;
        this.selectionCategory = selectionCategory;
        this.classname = classname;
        this.reference = reference;
        Thread runner = new Thread(this);
        runner.start();
    }

    /** Constructor. */
    public ThreadLookup(PanelDataLookup lookup, MainFrame frame, 
                        String classname) {
        this(lookup, frame, "", classname, null);
    }

    /** Add the item defined by a category, a name, summary and level to the
	  * recipient. */
    public void addSelectionItem(String category, String iid, String name, 
                                 String summary, String tip, int level) {
        if (!terminate && lookupPanel != null)
            lookupPanel.addSelectionTree(category, iid, name, summary, tip, level);
    }

    /** Foreces the thread to terminate. */
    public void terminate() {
        terminate = true;
    }

    /** Indicates the thread is terminated. */
    public boolean isTerminated() {
        return terminated;
    }

    /**Runs the lookupPanel.
     */
    public void run() {
        try {
            if (terminate)
                return;

            String search = lookupPanel.getTextSearch().trim().toLowerCase();
            String filterString = lookupPanel.getFilter();
            String searchKey = lookupPanel.getSearchKey();
            ImportListXML importList;

            lookupPanel.removeSelectionTree();

            if (reference == null) {
                WriterXML query = new WriterXML("query:list");
                query.xmlOut("category", selectionCategory);
                query.xmlOut("class", classname);
                query.xmlOut("filter", filterString);
                query.xmlOut("search", search);
                query.xmlOut("searchkey", searchKey);
                WriterXML answer = new WriterXML();
                frame.querySession(query, answer);
                importList = new ImportListXML(this, null);
                importList.xmlIn(answer, null, false);
            } else {
                WriterXML query = new WriterXML("list");
                reference.generateList(query, classname, search);
                importList = new ImportListXML(this, null);
                importList.xmlIn(query.getOutWriter(), null, false);
            }

            if (terminate)
                return;

            lookupPanel.reloadSelectionTree();

            if (terminate)
                return;

            frame.setServerNameNoProcessing(
                    LocalMessage.get(
                        (importList.getCounter() > 1) ? 
                        "label:items" : 
                        "label:item", 
                        "" + importList.getCounter(), 
                        "" + importList.getTotalCounter()));
                                                      
        } catch (Exception e) {
            frame.addMessage(e);
        } finally {
            terminated = true;
            frame.setCursor(false);
        }
    }
}
