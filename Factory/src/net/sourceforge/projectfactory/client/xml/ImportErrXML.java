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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/xml/ImportErrXML.java,v $
$Revision: 1.8 $
$Date: 2006/12/14 12:21:24 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.xml;

import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.client.components.LocalMessage;
import net.sourceforge.projectfactory.xml.FactoryReaderXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * XML Parser used to interprets error stream.
 * @author David Lambert
 */
public class ImportErrXML extends FactoryReaderXML {

    /** Maximum number of arguments. */
    private static final int MAXARGS = 5;

    /** Main frame of the application. */
    protected FrameMain frame;

    /** Indicates if the latest requests returned an error or not. */
    private boolean inError;

    /** Level of the message. */
    private String level;

    /** Label. */
    private String label;

    /** Arguments. */
    private String[] args;

    /** Number of arguments. */
    private int noArgs;

    /** Constructor. */
    public ImportErrXML(FrameMain frame) {
        this.frame = frame;
    }

    /** Starts a tag. */
    protected void startsTag(String tag) {
        if (tag.equals(FactoryWriterXML.ERRORS)) {
            return;
        } else {
            this.level = tag;
            label = "";
            noArgs = 0;
            args = new String[MAXARGS];
            new ImportErrDetailXML().xmlIn(this);
        }
    }

    /** Returns error indicator. */
    public boolean isInError() {
        return inError;
    }

    /**
	 * XML Parser used to read details in error stream.
	 */
     private class ImportErrDetailXML extends FactoryReaderXML {

         /** Interprets a tag. */
         protected void getTag(String tag, String text) {
             if (tag.equals("message")) {
                 label = text;
                 return;
             }

             if (tag.equals("arg") && noArgs < MAXARGS) {
                 args[noArgs++] = text;
                 return;
             }
         }

		/** Ends of the interpretation. */
		protected void end() {
			if (label != null && label.length() > 0) {
				String message = LocalMessage.get(label, args);
				if (level.equals("trace")) {
					frame.addMessage("TRC", message);
				} else if (level.equals("message")) {
					frame.addMessage("MSG", message);
				} else if (level.equals("warning")) {
					frame.addMessage("WAR", message);
				} else if (level.equals("error")) {
					frame.addMessage("ERR", message);
					inError = true;
				} else if (level.equals("fatal")) {
					frame.addMessage("FAT", message);
					inError = true;
				}
				label = "";
			}
		}
     }
}
