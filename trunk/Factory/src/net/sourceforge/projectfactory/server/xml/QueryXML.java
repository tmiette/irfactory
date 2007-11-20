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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/xml/QueryXML.java,v $
$Revision: 1.20 $
$Date: 2007/02/26 15:30:44 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.xml;


/**
 * XML Server-side parser used to query data.
 * @author David Lambert
 */
public class QueryXML extends ReaderServerXML {

    /** Constructor. */
    public QueryXML(TransactionXML transaction) {
        super(transaction);
    }

    /** Starts a tag. */
    protected void startsTag(String tag) {
        if (tag.equals("query:get")) {
            transaction.setCode(TransactionXML.GET);
            new ExchangeXML(transaction).xmlIn(this);
        } else if (tag.equals("query:new")) {
            transaction.setCode(TransactionXML.NEW);
            new ExchangeXML(transaction).xmlIn(this);
        } else if (tag.equals("query:update")) {
            transaction.setCode(TransactionXML.UPDATE);
            new ExchangeXML(transaction).xmlIn(this);
        } else if (tag.equals("query:delete")) {
            transaction.setCode(TransactionXML.DELETE);
            new ExchangeXML(transaction).xmlIn(this);
        } else if (tag.equals("query:default")) {
            transaction.setCode(TransactionXML.DEFAULT);
            new ExchangeXML(transaction).xmlIn(this);
        } else if (tag.equals("query:save")) {
            transaction.setCode(TransactionXML.SAVE);
            new ExchangeXML(transaction).xmlIn(this);
        } else if (tag.equals("query:replicate")) {
            transaction.setCode(TransactionXML.REPLICATE);
            new ExchangeXML(transaction).xmlIn(this);
        } else if (tag.equals("query:list")) {
            transaction.setCode(TransactionXML.LIST);
            new ListXML(transaction).xmlIn(this);
        } else if (tag.equals("query:saveall")) {
            transaction.setCode(TransactionXML.SAVE);
            new SaveXML(transaction).xmlIn(this);
        } else if (tag.equals("query:backup")) {
            transaction.setCode(TransactionXML.SAVE);
            new SaveXML(transaction, true).xmlIn(this);
        } else if (tag.equals("query:open")) {
            transaction.setCode(TransactionXML.OPEN);
            new OpenXML(transaction).xmlIn(this);
        } else if (tag.equals("query:restore")) {
            transaction.setCode(TransactionXML.OPEN);
            new OpenXML(transaction, true).xmlIn(this);
        } else if (tag.equals("query:import")) {
            transaction.setCode(TransactionXML.OPEN);
            new OpenXML(transaction, false, true).xmlIn(this);
        } else if (tag.equals("query:connect")) {
            transaction.setCode(TransactionXML.CONNECT);
            new ConnectionXML(transaction).xmlIn(this);
        } else if (tag.equals("query:about")) {
            xml.xmlOut("about");
        } else if (tag.equals("query:getnetworkrecipients")) {
            transaction.getSession().getOperator().xmlOutRecipient(
                    xml, transaction);
        }
    }

    /** Ends the tag interpretation. */
    protected void end() {
        xml.end();
    }
}
