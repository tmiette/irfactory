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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/BrowserControl.java,v $
$Revision: 1.2 $
$Date: 2006/03/07 20:41:12 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client;

import java.io.IOException;

/**
 * Static class to display an URL in the system browser. Under
 * Windows and Mac OS X, this will bring up the default browser.
 * For other systems the browser is hard coded and should be defined
 * in your PATH for this to work.
 * This has been tested with the following platforms: AIX, HP-UX and Solaris.
 * Examples: 
 * BrowserControl.displayURL("http://www.javaworld.com");
 * BrowserControl.displayURL("file://c:\\docs\\index.html");
 * BrowserContorl.displayURL("file:///user/joe/index.html"); 
 * Note - you must include the url type -- either "http://" or "file://".
 * Based on Steven Spencer's Java Tip in JavaWorld:
 * http://www.javaworld.com/javaworld/javatips/jw-javatip66.html
 */
public class BrowserControl {

    /** Indicates if the system runs on Mac OS. */
    public static boolean isMac = 
        System.getProperty("os.name").indexOf("Mac") != -1;

    /** Indicates if the system runs on Windows. */
    public static boolean isWindows = 
        System.getProperty("os.name").indexOf("Win") != -1;

    /**
     * Displays an URL in the system browser.
     * If you want to display a file, you
     * must include the absolute path name.
     */
    public static boolean displayURL(String url) {
        if (isMac)
			// Default system browser
            return runCmdLine("open " + url);
        if (isWindows)
			// Default system browser
            return runCmdLine("rundll32 url.dll,FileProtocolHandler " + url);
		// HTMLView
        if (runCmdLine("htmlview " + url))
            return true;
		// Mozilla
        if (runCmdLine("mozilla -remote openURL(" + url + ",new-window)", 
                       "mozilla"))
            return true;
		// Konqueror (KDE)
        if (runCmdLine("konqueror " + url))
            return true;
		// Netscape
        if (runCmdLine("netspace -remote openURL(" + url + ")", "netscape"))
            return true;
        return false;
    }

	/** Runs command line with no fallback. */
    private static boolean runCmdLine(String cmdLine) {
        return runCmdLine(cmdLine, null);
    }

	/** Runs command line with fallback. */
    private static boolean runCmdLine(String cmdLine, String fallBackCmdLine) {
        try {
            Process p = Runtime.getRuntime().exec(cmdLine);
            if (fallBackCmdLine != null && p.waitFor() != 0)
                Runtime.getRuntime().exec(fallBackCmdLine);
        } catch (InterruptedException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
