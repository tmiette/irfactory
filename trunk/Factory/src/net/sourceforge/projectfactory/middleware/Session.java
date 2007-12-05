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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/middleware/FactorySession.java,v $
$Revision: 1.8 $
$Date: 2007/02/08 15:58:43 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.middleware;

import net.sourceforge.projectfactory.server.ApplicationServer;
import net.sourceforge.projectfactory.server.actors.Actor;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Defines a session at server level.
 * @author David Lambert
 */
public class Session {

    /** Remote session. */
    private boolean remote;

    /** Refers to the server. */
    public ApplicationServer server;

    /** Refers to the connection active on the server. */
    private Connection connection;

    /** Action during parsing.
	 *  Refers to action values defined in EasyReaderServerXML class. */
    public char attributes;

    /** Operator, owner or current user of the server. */
    private Actor operator;

    /** Operating system. */
    private String os;

    /** Network Id. */
    private String networkId;

    /** Build. */
    private String build;

    /** Host. */
    private String host;

    /** Number of requests. */
    private int requests;

    /** Constructor. */
    Session(Connection connection, ApplicationServer server) {
        this.connection = connection;
        this.server = server;
    }

    /** Constructor. */
    Session(Connection connection, ApplicationServer server, 
                   boolean remote) {
        this(connection, server);
        this.remote = remote;
    }

    /** Indicates if the session is a remote session. */
    public boolean isRemote() {
        return remote;
    }

    /** Returns operator. */
    public Actor getOperator() {
        return operator != null ? operator : server.getOperator();
    }

    /** Returns operator name. */
    public String getOperatorName() {
        return operator != null ? operator.getName() : 
               (server.getOperator() != null ? 
               server.getOperator().getName() : "");
    }

    /** Returns operator network ID. */
    public String getOperatorNetworkId() {
        return operator != null ? operator.getNetworkId() : 
               server.getOperator().getNetworkId();
    }

    /** Returns operator ID. */
    public String getOperatorIid() {
        return operator != null ? operator.getIid() : 
               server.getOperator().getIid();
    }

    /** Defines the host. */
    public void setHost(String host) {
        this.host = host;
    }

    /** Returns host name. */
    public String getHost() {
        return host;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml) {
        if (isAuthorized()) {
            xml.xmlStart("connexion");
            xml.xmlOut("username", operator.getName());
            xml.xmlOut("networkid", networkId);
            xml.xmlOut("os", os);
            xml.xmlOut("build", build);
            xml.xmlOut("host", host);
            xml.xmlOut("requests", requests);
            xml.xmlEnd();
        }
    }

    /** Defines operator.
	 *	This action can be executed only once. */
    public void setOperator(String networkId, Actor operator, String os, 
                            String build) {
        if (remote && this.operator == null) {
            this.operator = operator;
            this.os = os;
            this.networkId = networkId;
            this.build = build;
            connection.broadcastMessage("SRV", "server:connected", 
                                        operator.getName(), build, os);
        }
    }

    /** Tells the server one actor is not authorized. */
    public void notAuthorized(String networkId) {
        if (remote) {
            connection.broadcastMessage("SRV", "server:notauthorized", 
                                        networkId);
        }
    }

    /** Indicates if the session is authorized. */
    public boolean isAuthorized() {
        if (remote && this.operator == null)
            return false;
        return true;
    }

    /** Increments the number of requests. */
    public void incRequests() {
        ++requests;
    }

    /** Returns the server. */
    public ApplicationServer getServer() {
        return server;
    }

    /** Returns the connection. */
    public Connection getConnection() {
        return connection;
    }
}
