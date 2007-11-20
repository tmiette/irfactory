/*

Copyright (c) 2006 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/xml/TransactionXML.java,v $
$Revision: 1.2 $
$Date: 2006/12/04 15:31:10 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.server.xml;

import net.sourceforge.projectfactory.middleware.FactorySession;
import net.sourceforge.projectfactory.server.FactoryServer;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * Defines a transaction with refers a session and a transactionCode.
 * @author David Lambert
 */
public class TransactionXML {

    /** Used to send data from the server to the client to display on a panel. */
    public static final int DETAIL = 0;

    /** Used to send data from the server to the client to display a summary on a list. */
    public static final int SUMMARY = 1;

    /** Used to default the object. */
    public static final int DEFAULT = 2;

    /** Used to tell to the server the object is new. */
    public static final int NEW = 3;

    /** Used to tell to the server the object is updated. */
    public static final int UPDATE = 4;

    /** Used to tell to the server the object is deleted. */
    public static final int DELETE = 5;

    /** Used to tell to the server the object must be retrieved (from memory). */
    public static final int GET = 6;

    /** Used to tell to the server the object must be loaded (from a file). */
    public static final int LOAD = 7;

    /** Used to tell to the server a file must be opened. */
    public static final int OPEN = 8;

    /** Used to tell to the server a list is required. */
    public static final int LIST = 9;

    /** Used to tell to the server to save data. */
    public static final int SAVE = 11;

    /** Used to tell to the server to expand a list with attached objects. */
    public static final int EXPAND = 12;

	/** Used for connection and authentification. */
    public static final int CONNECT = 13;
	
    /** Used to tell to the server the object is replicated. */
    public static final int REPLICATE = 14;

    /** Used to send data from the server to the client to display a short summary on a list. */
    public static final int SHORTSUMMARY = 15;
    
	/** Refers to the session. */
	private FactorySession session;
    
    /** XML Streams used for output. */
    private FactoryWriterXML xml;
    
    /** Action during parsing. Refers to action values defined in the class. */
    private int code;
    
    /** Constructor. */
    public TransactionXML(FactorySession session, FactoryWriterXML xml) {
		this.session = session;
        this.xml = xml;
        session.incRequests();
    }
    
    /** Sets the transaction code. */
    public void setCode(int code)  {
        this.code = code;
    }

    /** Returns the transaction code. */
    public int getCode()  {
        return code;
    }

    /** Returns the session. */
    public FactorySession getSession() {
        return session;
    }

    /** Returns the server. */
    public FactoryServer getServer() {
        return session.getServer();
    }
    
    /** Returns the XML streams. */
    public FactoryWriterXML getXml() {
        return xml;
    }

    /** Tells if it's a delete transaction. */    
    public boolean isDelete() {
        return code == DELETE;
    }

    /** Tells if it's a detail transaction. */    
    public boolean isDetail() {
        return code == DETAIL;
    }

    /** Tells if it's a save transaction. */    
    public boolean isSave() {
        return code == SAVE;
    }

    /** Tells if it's a summary transaction. */    
    public boolean isSummary() {
        return code == SUMMARY;
    }

    /** Tells if it's a short summary transaction. */    
    public boolean isShortSummary() {
        return code == SHORTSUMMARY;
    }

    /** Tells if it's an expand transaction. */    
    public boolean isExpand() {
        return code == EXPAND;
    }

    /** Tells if it's an update transaction. */    
    public boolean isUpdate() {
        return code == UPDATE;
    }

    /** Tells if it's a replicate transaction. */    
    public boolean isReplicate() {
        return code == REPLICATE;
    }

    /** Tells if it's a load transaction. */    
    public boolean isLoad() {
        return code == LOAD;
    }
}
