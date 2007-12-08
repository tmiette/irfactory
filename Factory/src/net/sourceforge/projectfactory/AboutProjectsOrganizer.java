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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/FactoryBuild.java,v $
$Revision: 1.59 $
$Date: 2007/03/13 07:00:29 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory;

import net.sourceforge.projectfactory.xml.XMLWrapper;

/**
 * Application titles and copyright.
 * @author David Lambert
 */
public class AboutProjectsOrganizer {

    /** Title used in windows ### change Manifest.mf too ###. */
	public static final String getBuild() {
		return "Factory 0.5";
	}
    /** Title used in every window title. */
    public static final String getShortTitle() {
        return "Factory";
    }

    /** Copyright. */
    public static final String getCopyright() {
        return "Copyright (c) 2007 David Lambert";
    }

    /** Licence. */
    public static final String getLicense() {
        return "GNU General Public License";
    }

    /** Home page. */
    public static final String getHomePage() {
        return "http://projectfactory.sourceforge.net";
    }

    /** Development page. */
    public static final String getDevPage() {
        return "http://sourceforge.net/projects/projectfactory";
    }

    /** Bug page. */
    public static final String getBugPage() {
        return "http://sourceforge.net/tracker/?group_id=134710&atid=730888";
    }

    /** List of extensions for managed applications. */	
    public static final String[] getApplicationExtensions() {
        return new String[] { "" };
    }

    /** Returns the path to be used in order to store data. */
    public static String getPath() {
        return XMLWrapper.USERHOME + XMLWrapper.SLASH + ".factory";
    }
}
