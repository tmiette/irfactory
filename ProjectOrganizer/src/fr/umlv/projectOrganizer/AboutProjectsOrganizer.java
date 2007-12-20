package fr.umlv.projectOrganizer;

import fr.umlv.projectOrganizer.xml.XMLWrapper;




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
