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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/xml/ListXML.java,v $
$Revision: 1.14 $
$Date: 2007/01/17 22:11:42 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.xml.server;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Generates a list of elements based on category and filter.
 * @author David Lambert
 */
class ListXML extends ReaderServerXML {

    /** Category to be listed. */
    private String category = "";

    /** Search key (text) to be used as a filter. */
    private String search = "";

    /** Class (name) to be listed. */
    private String classname = "";

    /** Name of a filter (managed by the server). */
    private String filter = "";

    /** Search key string used as a filter. */
    private String searchKey = "";

    /** Indicates if the active objects on a second level are shown or not. */
    private boolean expand;

    /** Indicates if the operator Iid should be provided. */
    private boolean getOperatorId;

    /** Indicates if the operator name should be provided. */
    private boolean getOperatorName;

    /** Stores the number of objects loaded in the list. */
    private int counter;

    /** Stores the total number of objects retrieved during construction of the list. */
    private int totalCounter;

    /** Constructor. */
    ListXML(TransactionXML transaction) {
        super(transaction);
    }

    /** Interprets a tag. */
    protected void getTag(String tag, String text) {
        if (tag.equals("category"))
            this.category = text;
        else if (tag.equals("search"))
            this.search = text;
        else if (tag.equals("class"))
            this.classname = text;
        else if (tag.equals("filter"))
            this.filter = text;
        else if (tag.equals("searchkey"))
            this.searchKey = text;
        else if (tag.equals("expand"))
            this.expand = true;
        else if (tag.equals("getoperatorid"))
            this.getOperatorId = true;
        else if (tag.equals("getoperatorname"))
            this.getOperatorName = true;
    }

    /** Sends the object names to the client based on a list. */
    protected void listByCategory(WriterXML xml, 
                                  TransactionXML transaction, List list) {
        if (list == null)
            return;

        int size = list.size();
        for (int i = 0; i < size; i++) {
            Entity entity = (Entity)list.get(i);
            ++totalCounter;

            if (!server.isAvailable(transaction, category, filter, 
                                    entity))
                continue;

            if (search.length() > 0 && !entity.matches(search))
                continue;

            transaction.setCode(expand ? TransactionXML.EXPAND : 
                                TransactionXML.SUMMARY);
            entity.xmlOut(xml, transaction, true);
            ++counter;
        }
    }

    /** Sends the object names to the client based on a list. */
    protected void listByClass(WriterXML xml, 
                               TransactionXML transaction, List list) {
        if (list == null)
            return;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Entity entity = (Entity)list.get(i);
            ++totalCounter;

            if (search.length() > 0 && !entity.matches(search))
                continue;

            if (searchKey.length() > 0 && !entity.matchesSearchKey(searchKey))
                continue;

            transaction.setCode(TransactionXML.SHORTSUMMARY);
            entity.xmlOut(xml, transaction, true);
            ++counter;
        }
    }

    /** Creates the list based on category. */
    protected void listByCategory() {
        List<List> lists = new ArrayList();
        server.listByCategory(transaction, lists, category, filter);
        for (List list: lists) {
            listByCategory(xml, transaction, list);
        }
    }

    /** Creates the list based on class. */
    protected void listByClass() {
        List<List> lists = new ArrayList();
        server.listByClass(transaction, lists, classname);
        for (List list: lists) {
            listByClass(xml, transaction, list);
        }
    }

    /** Ends the tag interpretation.
	 *  Selects the correct objects list to be used based on parameters. */
    protected void end() {
        totalCounter = 0;
        counter = 0;
        xml.xmlStart("list");
        if (category.length() > 0)
            listByCategory();
        if (classname.length() > 0)
            listByClass();
        xml.xmlOut("counter", counter);
        xml.xmlOut("totalcounter", totalCounter);
        if (counter == 0)
            xml.xmlOut("message", "message:nodata");
        if (getOperatorId)
            xml.xmlOut("operatorid", 
                       transaction.getSession().getOperatorIid());
        if (getOperatorName)
            xml.xmlOut("operatorname", 
                       transaction.getSession().getOperatorName());
        xml.xmlEnd();
    }
}
