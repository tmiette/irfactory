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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/xml/SaveXML.java,v $
$Revision: 1.20 $
$Date: 2007/02/07 18:13:14 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.zip.GZIPOutputStream;

import net.sourceforge.projectfactory.xml.FactoryWriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Save data from memory to XML files.
 * @author David Lambert
 */
class SaveXML extends ReaderServerXML {

    /** Maximum number of backup files. */
    private static int MAXBACKUP = 10;

    /** File name used for backup. */
    private String file = "";

    /** Indicates to process a backup of the database. */
    private boolean backup;

    /** Indicates to process a backup of demo data. */
    private boolean demo;

    /** Indicates to compress the backup. */
    private boolean gzip;

    /** Iid to be backuped. */
    private String iid = "";

    /** Filter on gzip files. */
    private FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".gzip");
            }
        };

    /** Constructor. */
    SaveXML(TransactionXML transaction) {
        super(transaction);
    }

    /** Constructor. */
    SaveXML(TransactionXML transaction, boolean backup) {
        super(transaction);
        this.backup = backup;
    }

    /** Interprets a tag. */
    protected void getTag(String tag, String text) {
        if (tag.equals("file"))
            this.file = text;
        else if (tag.equals("demo"))
            this.demo = true;
        else if (tag.equals("gzip"))
            this.gzip = true;
        else if (tag.equals("iid"))
            this.iid = text;
    }

    /** Ends the tag interpretation. */
    protected void end() {
        OutputStreamWriter fileWriter;
        BufferedWriter outputFile;
        String filename;

        String path =  transaction.getServer().getPath();

        if (backup) {
            filename = this.file;
            try {
                if (demo || !gzip)
                    fileWriter = 
                            new OutputStreamWriter(new FileOutputStream(filename), 
                                                   "UTF-8");
                else
                    fileWriter = 
                            new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(filename)), 
                                                   "UTF-8");

                outputFile = new BufferedWriter(fileWriter);
                FactoryWriterXML outputXml = 
                    new FactoryWriterXML(fileWriter, "factory", true);
                server.backup(outputXml, transaction, demo, iid);
                outputXml.end();
                outputFile.close();
                xml.xmlMessage(FactoryWriterXML.MESSAGE, 
                               "message:savingcomplete", "", filename);
            } catch (IOException e) {
                xml.xmlMessage(FactoryWriterXML.ERROR, "error:filenotsaved", 
                               "", filename);
                xml.xmlException(e);
            }
        } else {
            filename = path;
            File directory = new File(filename);
            if (!directory.exists()) {
                try {
                    if (!directory.mkdir()) {
                        xml.xmlMessage(FactoryWriterXML.ERROR, 
                                       "message:directory", "", filename);
                    }
                } catch (SecurityException e) {
                    xml.xmlMessage(FactoryWriterXML.ERROR, "error:directory", 
                                   "", filename);
                    xml.xmlException(e);
                }
            } else if (!directory.isDirectory()) {
                xml.xmlMessage(FactoryWriterXML.ERROR, "error:directory", "", 
                               filename);
            }
            if (server.isAnyServerDirty()) {
                filename = path + XMLWrapper.SLASH + "backups";
                directory = new File(filename);
                if (!directory.exists()) {
                    try {
                        if (!directory.mkdir()) {
                            xml.xmlMessage(FactoryWriterXML.ERROR, 
                                           "message:directory", "", filename);
                        }
                    } catch (SecurityException e) {
                        xml.xmlMessage(FactoryWriterXML.ERROR, 
                                       "error:directory", "", filename);
                        xml.xmlException(e);
                    }
                } else if (!directory.isDirectory()) {
                    xml.xmlMessage(FactoryWriterXML.ERROR, "error:directory", 
                                   "", filename);
                }

                filename = path + 
                        XMLWrapper.SLASH + "backups" + XMLWrapper.SLASH + 
                        System.currentTimeMillis() + ".gzip";
                try {
                    fileWriter = 
                            new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(filename)), 
                                                   "UTF-8");
                    outputFile = new BufferedWriter(fileWriter);
                    FactoryWriterXML outputXml = 
                        new FactoryWriterXML(fileWriter, "factory", true);
                    server.backup(outputXml, transaction, demo, null);
                    outputXml.end();
                    outputFile.close();
                    xml.xmlMessage(FactoryWriterXML.TRACE, 
                                   "message:savingcomplete", "", filename);
                } catch (IOException e) {
                    xml.xmlMessage(FactoryWriterXML.ERROR, 
                                   "error:filenotsaved", "", filename);
                    xml.xmlException(e);
                }

                String[] children = directory.list(filter);
                if (children != null && children.length > MAXBACKUP) {
                    for (int i = 0; i < children.length - MAXBACKUP; i++) {
                        File child = 
                            new File(directory + XMLWrapper.SLASH + children[i]);
                        child.deleteOnExit();
                    }
                }
            }
            server.save(xml, transaction);
            xml.xmlMessage(FactoryWriterXML.TRACE, "message:memory", "", 
                           "" + (double)(Runtime.getRuntime().freeMemory() / 
                                         1024));
        }
    }
}
