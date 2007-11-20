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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/xml/OpenXML.java,v $
$Revision: 1.26 $
$Date: 2007/02/11 20:55:25 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.xml;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import net.sourceforge.projectfactory.server.FactoryServer;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Reads data from XML files and store into memory.
 * @author David Lambert
 */
class OpenXML extends ReaderServerXML {

    /** User Id which identifies the operator who opens the database. */
    private static final String user = 
        System.getProperty("user.name").toLowerCase();

    /** File to be read for restore or import. */
    private String file = "";

    /** Restore database action. */
    private boolean restore;

    /** Import database action. */
    private boolean importation;

    /** Constructor. */
    OpenXML(TransactionXML transaction) {
        super(transaction);
    }

    /** Constructor. */
    OpenXML(TransactionXML transaction, boolean restore) {
        this(transaction);
        this.restore = restore;
    }

    /** Constructor. */
    OpenXML(TransactionXML transaction, boolean restore, boolean importation) {
        this(transaction);
        this.restore = restore;
        this.importation = importation;
    }

    /** Interprets a tag. */
    protected void getTag(String tag, String text) {
        if (tag.equals("file"))
            this.file = text;
    }

    /** Ends the tag interpretation. */
    protected void end() {
        String path =  transaction.getServer().getPath();

        transaction.setCode(TransactionXML.LOAD);
        try {
            if (restore || importation) {
                try {
                    BufferedReader inputFile = null;
                    if(file.endsWith(".gzip"))
                        inputFile = 
                            new BufferedReader(
                                new InputStreamReader(
                                    new GZIPInputStream(
                                        new FileInputStream(file)), "UTF-8"));
                    else if(file.endsWith(".xml"))
                        inputFile = 
                            new BufferedReader(
                                new InputStreamReader(
                                    new FileInputStream(file), "UTF-8"));
                                    
                    if(inputFile != null) {
                        if (restore)
                            server.clear();
                        new ExchangeXML(transaction).xmlIn(inputFile, xml, 
                                                                false);
                        inputFile.close();
                        FactoryServer.addMessageDictionary(xml, "MSG", 
                                                           "message:fileopen", 
                                                           file);
                    }
                } catch (FileNotFoundException ex) {
                    FactoryServer.addMessageDictionary(xml, "ERR", 
                                                       "error:filenotfound", 
                                                       file);
                } finally {
                    server.setAllDirty();
                }
            } else {
                List<String> files = new ArrayList();
                List<String> paths = new ArrayList();

                paths.add(XMLWrapper.USERDIR + XMLWrapper.SLASH + "lib" + 
                          XMLWrapper.SLASH);
                paths.add(XMLWrapper.USERDIR + XMLWrapper.SLASH + ".." + 
                          XMLWrapper.SLASH);
                paths.add(path + XMLWrapper.SLASH);

                files.add(path + XMLWrapper.SLASH + "preferences.xml");

                for (String extension: 
						transaction.getServer().getApplicationExtensions()) {
                    for (String pathfile: paths) {
                        files.add(pathfile + "dictionary" + extension + ".xml");
                        files.add(pathfile + "system" + extension + ".xml");
                    }
                }

                server.addFileList(files, path + XMLWrapper.SLASH);

                for (String extension: 
						transaction.getServer().getApplicationExtensions()) {
                    for (String pathfile: paths) {
                        files.add(pathfile + "demo" + extension + ".xml");
                    }
                }

                for (String filename: files) {
                    try {
                        BufferedReader inputFile = 
                            new BufferedReader(new InputStreamReader(new FileInputStream(filename), 
                                                                     "UTF-8"));

                        new ExchangeXML(transaction).xmlIn(inputFile, 
                                                                null, false);
                        inputFile.close();
                        FactoryServer.addMessageDictionary(xml, "TRC", 
                                                           "message:fileopen", 
                                                           filename);
                    } catch (FileNotFoundException ex) {
                        FactoryServer.addMessageDictionary(xml, "TRC", 
                                                           "error:filenotfound", 
                                                           filename);
                    }
                }
            }

        } catch (Exception ex) {
            transaction.getServer().returnException(xml, ex);
        } finally {
            // Defines operator
            server.setOperator(server.actors.getActor(user));

            if (server.getOperator() == null) {
                FactoryServer.addMessageDictionary(xml, "WAR", 
                                                   "warning:newactor", user);
                server.setOperator(server.actors.createActor(transaction, 
                                                                   user));
                if (server.getOperator() != null)
                    server.getOperator().create(transaction);
            } else {
                FactoryServer.addMessageDictionary(xml, "MSG", 
                                                   "message:identified", user, 
                                                   server.getOperator().getName());
            }

            // Defines localhost
            server.setLocalhost(server.actors.getServer("localhost"));
            if (server.getLocalhost() == null)
                server.setLocalhost(server.actors.createServer(transaction));
            server.sort();
        }
    }
}
