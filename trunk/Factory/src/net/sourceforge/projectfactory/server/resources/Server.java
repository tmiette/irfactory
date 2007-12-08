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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/Server.java,v $
$Revision: 1.18 $
$Date: 2007/02/12 11:17:51 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.resources;

import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;


/**
 * A server definition.
 * @author David Lambert
 */
public class Server extends ServerBase {

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, 
                     transaction.isDetail() && 
					 name != null &&
					 name.equals("localhost") ? 
					 "localhost" : 
                     "server");

        super.xmlOut(xml, transaction, false);

        if (tags)
            xmlEnd(xml);
    }

    /** Provides a summary when the object is displayed in a list. */
    public String getSummary() {
        return super.getSummary() + 
            (name.equals("localhost") ? "@label:bold" : "");
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);
        port = 16450;
        inMailPort = 110;
        outMailPort = 25;
    }

    /** Defines the default server. */
    public void createDefaultServer(TransactionXML transaction) {
        defaults(transaction);
        this.active = false;
        this.name = "localhost";
        this.address = "localhost";
        this.activeInMail = false;
        this.inMailType = "1";
        this.inMailUserName = System.getProperty("user.name").toLowerCase();
        this.activeOutMail = false;
        this.outMailType = "1";
        this.outMailAuth = "1";
        this.outMailUserName = System.getProperty("user.name").toLowerCase();
    }

    /** Returns an encryption key. */
    public String getEncryptKey() {
        return encryptKey != null ? encryptKey : "";
    }

    /** Returns the server address. */
    public String getAddress() {
        return address != null && address.length() > 0 ? address : "localhost";
    }

    /** Returns the port. */
    public int getPort() {
        return port;
    }

    /** Replicate the information. */
    public boolean getReplication() {
        return replication;
    }

    /** Allow replication of the information. */
    public boolean getAllowReplication() {
        return allowReplication;
    }
    
    /** Returns the path used for code generation. */
    public String getPath() {
        return path;
    }
}
