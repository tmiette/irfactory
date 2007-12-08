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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/LocalMessage.java,v $
$Revision: 1.20 $
$Date: 2007/02/11 20:55:24 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.components;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.HashMap;

import net.sourceforge.projectfactory.xml.ReaderXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Keeps messages in local memory. The messages are loaded for the
 * appropriate language.
 * @author David Lambert
 */
public class LocalMessage {

    /** The string messages that are stored in memory */
    private static final HashMap<String, String> messages = new HashMap();

    /** The comboboxes that are stored in memory */
    private static final HashMap<String, ComboBoxCode> comboboxes = 
        new HashMap();

    /** Indicates if the writing system is right to left. */
    private static boolean rightToLeft = false;

    /** Language. */
    private String language;

    /** Loads and stores messages in memory from an xml file. */
    public LocalMessage(String language, String[] extensions) throws FileNotFoundException, 
                                                IOException {
        this.language = language;

        // Right to left languages
        rightToLeft = language.equals("ar");

        BufferedReader inputFile;
        messages.clear();

        for (String extension: extensions) {
            String filename;
            int noFound = 0;

            for (int no = 1; no <= 2; no++) {
                try {
                    switch (no) {
                    case 1:
                        filename = 
                                XMLWrapper.USERDIR + XMLWrapper.SLASH + "lib" + 
                                extension + XMLWrapper.SLASH + "dictionary" + 
                                extension + ".xml";
                        break;
                    case 2:
                        filename = 
                                XMLWrapper.USERDIR + XMLWrapper.SLASH + ".." + 
                                XMLWrapper.SLASH + "dictionary" + 
                                extension + ".xml";
                        break;
                    default:
                        filename = "";
                        break;
                    }
                    inputFile = 
                            new BufferedReader(
								new InputStreamReader(
									new FileInputStream(filename), "UTF-8"));
                    new LocalMessageXML().xmlIn(inputFile, null, false);
                    inputFile.close();
                } catch (FileNotFoundException ex1) {
                    ++noFound;
                } catch (IOException ex2) {
                    throw ex2;
                }
            }
        }
    }

    /** Returns the message with the corresponding label. */
    public static String get(String label) {
        if (label.length() == 0)
            return label;
        String message = messages.get(label);
        if (message == null) {
            System.err.println("Message '" + label + "' not found.");
            return label;
        }
        return message;
    }

    /** Returns the message with the corresponding label using the value of the
     *  provided arguments (stored in an array). */
    public static String get(String label, String... args) {
        String message = get(label);
        if (messages.get(label) != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    args[i] = args[i].replace('\\', '/');
                    message = 
                            XMLWrapper.replaceAll(message, "%" + (i + 1), args[i]);
                } else
                    break;
            }
        } else {
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    args[i] = args[i].replace('\\', '/');
                    message = message + "(" + args[i] + ") ";
                } else
                    break;
            }
        }
        return message;
    }

    /** Indicates if the writing system is right to left. */
    public static boolean isRightToLeft() {
        return rightToLeft;
    }

    /** Returns the combobox with the corresponding label. */
    public static ComboBoxCode getCombo(String label) {
        ComboBoxCode combo = getComboInstance(label);
        return combo == null ? null : new ComboBoxCode(combo);
    }

    /** Returns the combobox with the corresponding label. */
    public static ComboBoxCode getComboInstance(String label) {
        if (label.length() == 0)
            return null;
        ComboBoxCode combo = comboboxes.get(label);
        if (combo == null) {
            System.err.println("Combobox '" + label + "' not found.");
            return null;
        }
        return combo;
    }

    /**
     * Interprets a messages set coming from a XML stream.
     */
    private class LocalMessageXML extends ReaderXML {

        /** Starts a tag. */
        protected void startsTag(String tag) {
            if (tag.equals("factory")) {
                new LocalMessageXML().xmlIn(this);
                return;
            } else if (tag.equals("dictionary")) {
                new LocalMessageItemXML().xmlIn(this);
                return;
            } else if (tag.equals("combobox")) {
                new LocalComboBoxXML().xmlIn(this);
                return;
            }
        }
    }

    /**
	 * Interprets a message item coming from a XML stream.
	 */
    private class LocalMessageItemXML extends ReaderXML {

        /** Label of message. */
        private String label;

        /** Content of message. */
        private String message;

        /** Content of message in English. */
        private String englishMessage;

        /** Interprets a tag. */
        protected void getTag(String tag, String text) {
            if (tag.equals("name")) {
                this.label = text;
                return;
            } else if ((language.equals("fr") && tag.equals("french")) || 
                       (language.equals("es") && tag.equals("spanish")) || 
                       (language.equals("ar") && tag.equals("arabic"))) {
                this.message = 
                        this.message == null ? text : this.message + "\n" + 
                        text;
                return;
            } else if (tag.equals("english")) {
                this.englishMessage = 
                        this.englishMessage == null ? text : this.englishMessage + 
                        "\n" + text;
                return;
            }
        }

        /** Ends the tag interpretation. */
        protected void end() {
            if (message != null)
                messages.put(label, message);
            else if (englishMessage != null)
                messages.put(label, englishMessage);
            else
                messages.put(label, "");
        }
    }

    /**
     * Interprets a combobox definition coming from a XML stream.
     */
    private class LocalComboBoxXML extends ReaderXML {

        /** Name of the combobox. */
        private String name;

        /** Allows a blank row. */
        private boolean blankRow;

        /** Combobox created during xml reading. */
        private ComboBoxCode combo = new ComboBoxCode();

        /** Starts a tag. */
        protected void startsTag(String tag) {
            if (tag.equals("item")) {
                new LocalComboBoxItemXML(combo).xmlIn(this);
            }
        }

        /** Interprets a tag. */
        protected void getTag(String tag, String text) {
            if (tag.equals("name")) {
                this.name = text;
                return;
            }
            if (tag.equals("blankrow")) {
                this.blankRow = text.equals("y");
                return;
            }
        }

        /** Ends the tag interpretation. */
        protected void end() {
            if (!blankRow)
                combo.removeItemCode("0");
            comboboxes.put(name, combo);
        }
    }

    /**
     * Interprets a combobox item definition coming from a XML stream.
     */
    private class LocalComboBoxItemXML extends ReaderXML {

        /** Combobox created during xml reading. */
        private ComboBoxCode combo = new ComboBoxCode();

        /** Code. */
        private String code;

        /** Label. */
        private String label;

        /** Style. */
        private String style;

        /** Icon. */
        private String icon;

        /** Constructor. */
        LocalComboBoxItemXML(ComboBoxCode combo) {
            this.combo = combo;
        }

        /** Interprets a tag. */
        protected void getTag(String tag, String text) {
            if (tag.equals("code")) {
                this.code = text;
                return;
            }
            if (tag.equals("label")) {
                this.label = text;
                return;
            }
            if (tag.equals("style")) {
                this.style = text;
                return;
            }
            if (tag.equals("icon")) {
                this.icon = text;
                return;
            }
        }

        /** Ends the tag interpretation. */
        protected void end() {
            String message = get(label);
            if (style.equals("normal"))
                message = "<font color=black>" + message + "</font>";
            else if (style.equals("bold"))
                message = "<font color=black><b>" + message + "</b></font>";
            else if (style.equals("gray"))
                message = "<font color=gray>" + message + "</font>";
            else if (style.equals("green"))
                message = "<font color=green>" + message + "</font>";
            else if (style.equals("orange"))
                message = "<font color=#ff7f00>" + message + "</font>";
            else if (style.equals("red"))
                message = "<font color=red>" + message + "</font>";
            message = "<html>" + message + "</html>";
            combo.addItem(code, message, icon);
        }
    }
}
