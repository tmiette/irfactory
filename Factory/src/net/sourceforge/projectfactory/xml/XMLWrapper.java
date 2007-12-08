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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/xml/XMLWrapper.java,v $
$Revision: 1.10 $
$Date: 2007/02/22 15:37:50 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.xml;

import java.awt.Color;
import java.awt.Event;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Date;
import java.util.Locale;
import java.lang.StringBuffer;

/**
 * Utility static class that manages reserved characters in XML messages.
 * @author David Lambert
 */
public class XMLWrapper {
    
    /** Date format used to read/write object - Small form : date only. */
    public static final DateFormat dsUS = 
        DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);

    /** Date format used to display - Small form. */
    public static final DateFormat dsLocal = 
        DateFormat.getDateInstance(DateFormat.SHORT);

    /** Date format used to read/write object - Full form. */
    public static final DateFormat dfUS = 
        DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, 
                                       Locale.US);

    /** Date format used to display - Full form. */
    public static final DateFormat dfLocal = 
        DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);

    /** File separator. */
    public final static String SLASH = System.getProperty("file.separator");

    /** Application directory. */
    public final static String USERDIR = System.getProperty("user.dir");

    /** User home directory. */
    public final static String USERHOME = System.getProperty("user.home");

    /** Indicates if the system runs on Mac OS. */
    public static boolean isMac = 
        System.getProperty("os.name").indexOf("Mac") != -1;

    /** System key modifier. */
    public static int ControlKey = 
        isMac ? Event.META_MASK : Event.CTRL_MASK;

    /** Color used as a background during edition. */
    public static final Color editColor = new Color(200, 255, 200);

    /** Color used as a background during edition in a darker version. */
    public static final Color editColorDark = new Color(180, 255, 180);

    /** Color used as a background as a very light gray. */
    public static final Color lightGray = 
        isMac ? new Color(240, 240, 240) : new Color(230, 230, 230);

    /** Prepares a string for HTML output. */
    public static final String wrapHTML(String text) {
        String out = replaceAll(text, "<", "&lt;");
        out = replaceAll(out, ">", "&gt;");
        out = fixHTML(out);
        return out;
    }
    
    /** Prepares a string for HTML output. */
    public static final String fixHTML(String text) {
        String out = replaceAll(text, "\n", "<br/>");
        out = replaceAll(out, "\r", "");
        return out;
    }
    
    /** Converts a date in local format into US format. */
    public static final String unwrapDate(String text) {
        if (text.length() > 0) {
            Date date = null;

            try {
                date = dsUS.parse(text);
            } catch (ParseException ex) {
                return text;
            }

            if (date != null) {
                return dsLocal.format(date);
            }
        }

        return "";
    }

    /** Converts a date in local format into US format. */
    public static final String wrapDate(String text) {
        if (text.length() > 0) {
            Date date = null;

            try {
                date = dsLocal.parse(text);
            } catch (ParseException ex) {
                return text;
            }

            if (date != null)
                return dsUS.format(date);
        }

        return "";
    }

    /** Converts a long date in US format into local format. */
    public static final String unwrapLongDate(String text) {
        if (text.length() > 0) {
            Date date = null;

            try {
                date = dfUS.parse(text);
            } catch (ParseException ex) {
                return text;
            }

            if (date != null)
                return dfLocal.format(date);
        }

        return "";
    }

    /** Converts a long date in local format into US format. */
    public static final String wrapLongDate(String text) {
        if (text.length() > 0) {
            Date date = null;

            try {
                date = dfLocal.parse(text);
            } catch (ParseException ex) {
                return text;
            }

            if (date != null)
                return dfUS.format(date);
        }

        return "";
    }

    /** Replaces in a text a date with form "[yy/mm/dd]" into local format. */
    public static final String unwrapEmbeddedDate(String text) {
        String out = text;
        int begin = text.indexOf("[");
        int end = text.indexOf("]");

        if (begin >= 0 && end > 0 && end > begin) {
            String date = text.substring(begin + 1, end);
            String localDate = unwrapDate(date);

            if (!localDate.equals(""))
                out = text.substring(0, begin) + 
                        localDate + text.substring(end + 1, text.length());
        }

        return out;
    }
    
    /** Replaces the sequence to be found by a replacement string. 
     *  Method used in replacement of java.String.replaceAll method
     *  in order to not manage regular expression. */
    public static final String replaceAll(String text, String find, String replace){
        int sizeText = text.length();
        int sizeFind = find.length();
        int sizeMatch = 1;
        String subString;
        StringBuffer out = new StringBuffer(sizeText);
        
        for (int pos = 0 ; pos + sizeMatch <= sizeText ; ) {
            subString = text.substring(pos, pos + sizeMatch);
            if(subString.equals(find.substring(0, sizeMatch))){
                ++sizeMatch;
                if (sizeMatch > sizeFind) {
                    out.append(replace);
                    pos += sizeMatch - 1;
                    sizeMatch = 1;
                }
                else if (pos + sizeMatch > sizeText)
                    out.append(subString);
            }
            else {
                out.append(subString);
                pos += sizeMatch;
                sizeMatch = 1;
            }
        }
        return out.toString();
    }

    /** Returns system date without time. */
    public static final Date systemDate() {
        try {
            return dsUS.parse(dsUS.format(new Date()));
        } catch (ParseException ex) {
            return null;
        }
    }
}
