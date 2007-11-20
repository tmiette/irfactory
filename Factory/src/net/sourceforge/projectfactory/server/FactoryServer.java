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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/FactoryServer.java,v $
$Revision: 1.45 $
$Date: 2007/02/23 16:27:50 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server;

import java.lang.StackTraceElement;

import java.util.List;

import net.sourceforge.projectfactory.FactoryBuild;
import net.sourceforge.projectfactory.middleware.FactorySession;
import net.sourceforge.projectfactory.server.FactoryServerBase;
import net.sourceforge.projectfactory.server.actions.FactoryAction;
import net.sourceforge.projectfactory.server.actors.Actor;
import net.sourceforge.projectfactory.server.actors.FactoryActor;
import net.sourceforge.projectfactory.server.actors.Server;
import net.sourceforge.projectfactory.server.core.FactoryCore;
import net.sourceforge.projectfactory.server.data.FactoryData;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.SubEntityServerXML;
import net.sourceforge.projectfactory.server.projects.FactoryProject;
import net.sourceforge.projectfactory.server.xml.QueryXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * Application Server.
 * Contains links to different sub-servers.
 * @author David Lambert
 */
public class FactoryServer extends FactoryServerBase {

    /** Operator, owner or current user of the server. */
    private Actor operator;

    /** Local server. */
    private Server localhost;

    /** Actor Server. */
    public FactoryActor actors = new FactoryActor(this);

    /** Project Server. */
    public FactoryProject projects = new FactoryProject(this);

    /** Action Server. */
    public FactoryAction actions = new FactoryAction(this);

    /** System data Server. */
    public FactoryData data = new FactoryData(this);

    /** Core Server. */
    public FactoryCore core = new FactoryCore(this);

    /** Constructor. */
    public FactoryServer() {
        super(null);
    }

    /** Constructor. */
    public FactoryServer(FactoryServerBase serverBase) {
        super(serverBase);
    }

    /** Returns home page URL. */
    public String getHomePage() {
        return FactoryBuild.getHomePage();
    }

    /** Returns developer page URL. */
    public String getDevPage() {
        return FactoryBuild.getDevPage();
    }

    /** Returns bug page URL. */
    public String getBugPage() {
        return FactoryBuild.getBugPage();
    }

    /** Copyright. */
    public String getCopyright() {
        return FactoryBuild.getCopyright();
    }

    /** Licence. */
    public String getLicense() {
        return FactoryBuild.getLicense();
    }

    /** Returns the path to be used in order to store data. */
    public String getPath() {
        return FactoryBuild.getPath();
    }

    /** Returns build number. */
    public String getBuild() {
        return FactoryBuild.getBuild();
    }

    /** List of extensions for managed applications. */ 
    public String[] getApplicationExtensions() {
        return FactoryBuild.getApplicationExtensions();
    }

    /** Executes a query toward the server. */
    public void query(FactorySession session, FactoryWriterXML query, 
                      FactoryWriterXML answer) {
        try {
            TransactionXML transaction = new TransactionXML(session, answer);
            new QueryXML(transaction).xmlIn(query, answer, false);
        } catch (Exception e) {
            returnException(answer, e);
        }
    }

    /** Sets all servers to 'dirty' flag. */
    public void setAllDirty() {
        setDirty();
        for(FactoryServerBase server: servers)
            server.setDirty();
    }

    /** Indicates if any of the servers is dirty. */
    public boolean isAnyServerDirty() {
        for(FactoryServerBase server: servers)
            if(server.isDirty()) 
                return true;
        return false;
    }

    /** Clears objects. */
    public void clear() {
        for(FactoryServerBase server: servers)
            server.clear();
    }

    /** Sorts objects. */
    public void sort() {
        for(FactoryServerBase server: servers)
            server.sort();
    }

    /** Backups all data. */
    public void backup(FactoryWriterXML xml, TransactionXML transaction, 
                       boolean demo, String iid) {
        for(FactoryServerBase server: servers)
            server.saveAll(xml, transaction, demo, iid);
    }

    /** Saves all data. */
    public void save(FactoryWriterXML xml, TransactionXML transaction) {
		data.saveAllPreferences(xml, transaction);

        for(FactoryServerBase server: servers)
            if (server.isDirty()) {
                server.saveAll(xml, transaction);
                server.setNotDirty();
            }

        setNotDirty();
    }

    /** Adds files to be opened. */
    public void addFileList(List<String> files, String prefix) {
        for(FactoryServerBase server: servers)
            server.addFileList(files, prefix);
    }

    /** Adds classes used for replication. */
    public void addClassList(List classes) {
        for(FactoryServerBase server: servers)
            server.addClassList(classes);
    }

    /** Sends the object names to the client based on a list. */
    public boolean isAvailable(TransactionXML transaction, String category, 
                               String filter, Entity entity) {
        for(FactoryServerBase server: servers)
            if (!server.isAvailable(transaction, category, filter, entity))
                return false;
        return true;
    }

    /** Creates a list based on category. */
    public void listByCategory(TransactionXML transaction, List lists, 
                               String category, String filter) {
        for(FactoryServerBase server: servers)
            server.listByCategory(transaction, lists, category, filter);
    }

    /** Creates the list based on class. */
    public void listByClass(TransactionXML transaction, List lists, 
                            String classname) {
        for(FactoryServerBase server: servers)
            server.listByClass(transaction, lists, classname);
    }

    /** Returns the appropriate parser in order to read an element. */
    public SubEntityServerXML getParser(TransactionXML transaction, String tag) {
        for(FactoryServerBase server: servers) {
            SubEntityServerXML parser = server.getParser(transaction, tag);
            if(parser != null)
                return parser;
        }
        return null;
    }

    /** Returns exception details on the error output stream. */
    public void returnException(FactoryWriterXML xml, Exception ex) {
        addMessageDictionary(xml, "FAT", "fatal:server", ex.toString());
        StackTraceElement[] stack = ex.getStackTrace();
        for (int i = 0; i < stack.length; i++)
            addMessageDictionary(xml, "FAT", "fatal:server", 
                                 stack[i].toString() + ":" + 
                                 getBuild());
        addMessageDictionary(xml, "WAR", "instruction:exception1");
        addMessageDictionary(xml, "WAR", "instruction:exception2");
        xml.end();
    }

    /** Returns operator. */
    public Actor getOperator() {
        return operator;
    }

    /** Defines operator. This action is readonly. */
    public void setOperator(Actor operator) {
        if (this.operator == null)
            this.operator = operator;
    }

    /** Returns localhost. */
    public Server getLocalhost() {
        return localhost;
    }

    /** Defines localhost. */
    public void setLocalhost(Server localhost) {
        this.localhost = localhost;
    }

    /** Returns server address. */
    public Server getServer(String serverName) {
        return actors.getServer(serverName);
    }

    /** Returns the port to be used for the local server. */
    public int getLocalPort() {
        return getLocalhost() == null ? 16450 : 
               getLocalhost().getPort();
    }

    /** Returns the encryption key. */
    public String getLocalEncryptKey() {
        return getLocalhost() == null ? "" : getLocalhost().getEncryptKey();
    }

    /** Returns if the server allows replication. */
    public boolean getAllowReplication() {
        return getLocalhost() == null ? false : 
               getLocalhost().getAllowReplication();
    }
}
