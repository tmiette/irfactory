/*

Copyright (c) 2005, 2006, 2007 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/PrintDocHtml.java,v $
$Revision: 1.13 $
$Date: 2007/02/11 20:55:24 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client;

import java.awt.image.BufferedImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.sourceforge.projectfactory.FactoryBuild;
import net.sourceforge.projectfactory.client.components.LocalMessage;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Creates a basic HTML document including tables.
 * @author David Lambert
 */
public class PrintDocHtml {

    /** Error output stream. */
    private FactoryWriterXML err;

    /** Output steam. */
    private BufferedWriter out;

    /** Path for output files. */
    private String path;

    /** Name of generated file. */
    String filename;

    /** List of generated documents. */
    private static List<IndexItem> index = new ArrayList();


    /** Constructor. */
    public PrintDocHtml(FactoryWriterXML err) {
        this.err = err;
    }

    /** Opens documents. */
    public void open(String type, String title) throws IOException {
        path = XMLWrapper.USERHOME;

        if (!path.endsWith(XMLWrapper.SLASH))
            path = path + XMLWrapper.SLASH;

        // Copy required files from library
        String pathLib = pathOfFilesFromLib();
        String[] files = listOfFilesFromLib(pathLib);
        if(files != null) {
            for(int i=0 ; i<files.length ; i++) {
                if(files[i].endsWith(".css") || files[i].endsWith(".gif"))
                    copyFileFromLib(pathLib, files[i]);
            }
        }
        
        // Create file in tmp directory
        String newTitle = type + "." + title;
        for (int i = 0; i < newTitle.length(); i++) {
            char c = newTitle.charAt(i);
            if (!(((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || 
                  ((c >= '0') && (c <= '9')) || (c == '_') || (c == '-') || 
                  (c == '(') || (c == ')') || (c == '.') || (c == '\\')))
                newTitle = newTitle.replace(c, '-');
        }

        filename = "factory.report." + newTitle.toLowerCase();
        OutputStreamWriter file = 
            new OutputStreamWriter(new FileOutputStream(path + filename + 
                                                        ".html"), "UTF-8");
        out = new BufferedWriter(file);
        generateHeader(type, title);
        out("<p><a href=\"factory.report.index.html\">" + 
            LocalMessage.get("label:index") + "</a></p>");
        index.add(new IndexItem(type, title, filename + ".html"));
    }

    /** Generates file header (XHTML). */
    private void generateHeader(String type, String title) throws IOException {
        out("<!DOCTYPE html PUBLIC \"" + FactoryBuild.getBuild() + "\">\n");
        out("<html>\n");
        out("<head>\n");
        out("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n");
        out("<title>" + XMLWrapper.unwrapEmbeddedDate(title) + " (" + type + 
            ")</title>\n");
        out("<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" ");
        out("href=\"factory.report.styles.css\"/>\n");
        out("</head>\n");
        out("<body>\n");
        out("<div id=\"container\">\n");
        out("<div id=\"pageHeader\">\n");
        out("<h1>" + XMLWrapper.unwrapEmbeddedDate(title) + " (" + type + 
            ")</h1>\n");
        out("</div>\n");
        out("<div id=\"contentContainer\">\n");
        out("<div id=\"content\">\n");
    }

    /** Writes a title. */
    public void write(String title) throws IOException {
        if (title != null && title.length() > 0)
            out("<h2>" + title + "</h2>\n");
    }

    /** Writes a title and value in table. */
    public void write(String title, String value) throws IOException {
        out("<tr><td>" + 
            (title.length() > 0 ? "<strong>" + title + "</strong>" : "") + 
            "</td><td>" + value + "</td></tr>\n");
    }

    /** Writes a title and value in table with an icon. */
    public void write(String title, String value, 
                      String icon) throws IOException {
        out("<tr><td>" + 
            (title.length() > 0 ? "<strong>" + title + "</strong>" : "") + 
            "</td><td>" + 
            (icon != null && icon.length() > 0 ? "<img src=\"factory.report." + 
             icon + "\" align=\"bottom\"/>&nbsp;" : "") + value + 
            "</td></tr>\n");
    }

    /** Writes a picture and includes it in document. */
    public void writePiture(int i, BufferedImage bi) throws IOException {
        if (bi.getWidth() > 10) {
            File file = new File(path, filename + "_" + i + ".png");
            ImageIO.write(bi, "png", file);
            out("<div style=\"text-align: left;\"><img style=\"width: " + 
                bi.getWidth() + "px; height: " + bi.getHeight() + 
                "px;\" alt=\"\" src=\"" + filename + "_" + i + 
                ".png\" align=\"middle\"><br/><br/>\n");
        }
    }

    /** Opens a table. */
    public void openTable() throws IOException {
        out("<table border=\"0\" cellpadding=\"2\">\n");
    }

    /** Closes a table. */
    public void closeTable() throws IOException {
        out("</table><br/>\n");
    }

    /** Generates file footer. */
    private void generateFooter() throws IOException {
        out("<p>" + FactoryBuild.getBuild() + "</p>\n");
        out("</div>\n");
        out("</div>\n");
        out("<div class=\"clearer\"></div>\n");
        out("</div>\n");
        out("</body>\n");
        out("</html>\n");
        out.close();
        out = null;
    }

    /** Closes the document. */
    public void close() throws IOException {
        generateFooter();
        err.xmlMessage(FactoryWriterXML.MESSAGE, "message:filegenerated", "", 
                       path + filename);

        // Generates index
        OutputStreamWriter file = 
            new OutputStreamWriter(new FileOutputStream(path + 
                                                        "factory.report.index.html"), 
                                   "UTF-8");
        out = new BufferedWriter(file);
        generateHeader(LocalMessage.get("label:index"), 
                       FactoryBuild.getShortTitle());

        openTable();
        for (IndexItem item: index) {
            write(item.type, 
                  "<a href=\"" + item.filename + "\">" + item.title + "</a>");
        }
        closeTable();

        generateFooter();
        err.xmlMessage(FactoryWriterXML.MESSAGE, "message:filegenerated", "", 
                       path + "factory.report.index.html");
    }

    /** Displays document in system browser. */
    public void display() {
        String url = "file://" + path + filename + ".html";
        err.xmlMessage(FactoryWriterXML.MESSAGE, "message:openurl", "", url);
        if (!BrowserControl.displayURL(url)) {
            err.xmlMessage(FactoryWriterXML.ERROR, "error:badbrowser", "");
        }
    }

    /** Copies a file from the application library
		* into the repertory for temporary files. */
    private void copyFileFromLib(String pathLib, String fileName) {
        File inFile = null;
        InputStream input = null;
        try {
            inFile = new File(pathLib + fileName);
            if (inFile.exists())
                input = new FileInputStream(inFile);

            if (input != null) {
                FileOutputStream output = 
                    new FileOutputStream(new File(path + "factory.report." + 
                                                  fileName));
                int oneChar;
                while ((oneChar = input.read()) != -1) 
                    output.write(oneChar);
                input.close();
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Returns the list of files from the library. */
    private String pathOfFilesFromLib() {
        File inFile = null;

        String path = XMLWrapper.USERDIR + XMLWrapper.SLASH + "lib" + XMLWrapper.SLASH;
        inFile = new File(path);
        if (inFile.exists())
            return path;

        path = XMLWrapper.USERDIR + XMLWrapper.SLASH + ".." + XMLWrapper.SLASH;
        inFile = new File(path);
        if (inFile.exists())
            return path;

        return null;
    }

    /** Returns the list of files from the library.  */
    private String[] listOfFilesFromLib(String path) {
        File inFile = new File(path);
        if (inFile.exists())
            return inFile.list();
        return null;
    }

    /** Writes in the output stream. */
    private void out(String outString) throws IOException {
        out.write(outString);
    }

    /**
	 * Class used in order to keep the list of documents "printed".
	 * Then this list is used to generate an index.
	 */
    private class IndexItem {
        private String type;
        private String title;
        private String filename;

        /** Constructor. */
        private IndexItem(String type, String title, String filename) {
            this.type = type;
            this.title = title;
            this.filename = filename;
        }
    }
}
