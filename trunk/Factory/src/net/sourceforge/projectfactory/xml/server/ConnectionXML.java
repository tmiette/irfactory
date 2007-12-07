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

 $Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/xml/ConnectionXML.java,v $
 $Revision: 1.17 $
 $Date: 2007/03/04 21:03:52 $
 $Author: ddlamb_2000 $

 */
package net.sourceforge.projectfactory.xml.server;

import java.text.ParseException;

import java.util.Date;

import net.sourceforge.projectfactory.server.ApplicationServer;
import net.sourceforge.projectfactory.server.data.ActionBar;
import net.sourceforge.projectfactory.server.resources.Resource;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * XML Parser used for connection and authentification.
 * @author David Lambert
 */
class ConnectionXML extends ReaderServerXML {

    /** Network identifier for the actor who requests a connection. */
    private String networkId;

    /** Build number for the actor who requests a connection. */
    private String build;

    /** OS for the actor who requests a connection. */
    private String os;

    /** Operator Id provided from the server (Iid). */
    private String operatorId = "";

    /** Operator name provided from the server. */
    private String operatorName = "";

    /** Local date sent by the client. */
    private Date localDate;

    /** Disconnection flag. */
    private boolean disconnect;

    /** Requires a list of connexions. */
    private boolean list;

    /** Requires a list of menus. */
    private boolean menuList;

    /** Constructor. */
    ConnectionXML(TransactionXML transaction) {
        super(transaction);
    }

    /** Starts a tag. */
    protected void startsTag(String tag) {
    }

    /** Interprets a tag. */
    protected void getTag(String tag, String text) {
        if (tag.equals("networkid")) {
            networkId = text;
        } else if (tag.equals("build")) {
            build = text;
        } else if (tag.equals("os")) {
            os = text;
        } else if (tag.equals("operatorid")) {
            operatorId = text;
        } else if (tag.equals("operatorname")) {
            operatorName = text;
        } else if (tag.equals("localdate")) {
            try {
                localDate = XMLWrapper.dfUS.parse(text);
            } catch (ParseException e) {
                localDate = null;
            }
        } else if (tag.equals("disconnect")) {
            if(text.equals("y"))
                disconnect = true;
        } else if (tag.equals("list")) {
            if(text.equals("y"))
                list = true;
        } else if (tag.equals("menulist")) {
            if(text.equals("y"))
                menuList = true;
        }
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml) {
        xml.xmlStart("connexion");
        transaction.getSession().getConnection().xmlOut(xml);
        xml.xmlOut("serverbuild", transaction.getServer().getBuild());
        xml.xmlOut("osname", System.getProperty("os.name"));
        xml.xmlOut("osarch", System.getProperty("os.arch"));
        xml.xmlOut("osversion", System.getProperty("os.version"));
        xml.xmlOut("javahome", System.getProperty("java.home"));
        xml.xmlOut("javavmversion", System.getProperty("java.vm.version"));
        xml.xmlOut("javavmvendor", System.getProperty("java.vm.vendor"));
        xml.xmlOut("javavmname", System.getProperty("java.vm.name"));
        xml.xmlOut("javaclasspath", System.getProperty("java.class.path"));
        xml.xmlOut("userhome", System.getProperty("user.home"));
        xml.xmlOut("userdir", System.getProperty("user.dir"));
        xml.xmlEnd();
    }

    /** Ends the tag interpretation. */
    protected void end() {
        if (list) {
            xmlOut(xml);
        } else if(menuList) {
            transaction.setCode(TransactionXML.DETAIL);
            for(ActionBar actionBar: transaction.getServer().data.actionBars) {
                if(actionBar.administratorOnly &&
                    !transaction.getSession().getOperator().isAdministrator())
                        continue;
                if(actionBar.localOnly &&
                    transaction.getSession().isRemote())
                        continue;
                actionBar.xmlOut(xml, transaction, true);
            }
        } else if (!disconnect) {
            if (networkId == null) {
                ApplicationServer.addMessageDictionary(xml, "ERR", 
                                                   "server:notidentified", "");
                return;
            }

            Resource actor = server.actors.getActor(networkId);

            if (actor == null) {
                ApplicationServer.addMessageDictionary(xml, "ERR", 
                                                   "server:notidentified", 
                                                   networkId);
                ApplicationServer.addMessageDictionary(xml, "ERR", 
                                                   "server:close:client", "");
                transaction.getSession().notAuthorized(networkId);
                return;
            }

            // Controls if the operator name matches
            if (operatorName == null || 
                (!actor.getName().equals(operatorName))) {
                ApplicationServer.addMessageDictionary(xml, "ERR", 
                                                   "server:notidentified", 
                                                   operatorName);
                ApplicationServer.addMessageDictionary(xml, "ERR", 
                                                   "server:close:client", "");
                transaction.getSession().notAuthorized(networkId);
                return;
            }

            // Controls if the local date is not too much different than on the server
            if (localDate != null) {
                long delta = localDate.getTime() - (new Date()).getTime();
                long TOLERANCE = 2 * 60 * 1000;
                if (delta > TOLERANCE || delta < -TOLERANCE) {
                    ApplicationServer.addMessageDictionary(xml, "WAR", 
                                                       "server:timeissue", 
                                                       XMLWrapper.dfUS.format(new Date()).toString(), 
                                                       XMLWrapper.dfUS.format(localDate).toString());
                }
            }

            // Controls builds
            if (build != null) {
                if (!build.equals(transaction.getServer().getBuild())) {
                    ApplicationServer.addMessageDictionary(xml, "WAR", 
                                                       "warning:build", 
                                                       transaction.getServer().getBuild(), 
                                                       build);
                }
            }

            // If the connexion comes with another iid, 
            // then the local iid is changed in order to be in synch
            if (operatorId != null && 
                (actor.getIid() == null || (actor.getIid() != null && 
                                            !actor.getIid().equals(operatorId)))) {
                actor.changeIId(transaction, operatorId);
            }

            transaction.getSession().setOperator(networkId, actor, os, build);

            ApplicationServer.addMessageDictionary(xml, "MSG", "server:greeting", 
                                               transaction.getServer().getBuild(), 
                                               System.getProperty("os.name"), 
                                               System.getProperty("os.arch"));

            ApplicationServer.addMessageDictionary(xml, "MSG", 
                                               "message:identified", networkId, 
                                               actor.getName());
        }
    }
}
