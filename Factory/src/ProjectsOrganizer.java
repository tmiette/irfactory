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

$Source: /cvsroot/projectfactory/development/Factory.java,v $
$Revision: 1.10 $
$Date: 2007/02/04 23:12:13 $
$Author: ddlamb_2000 $

*/
import net.sourceforge.projectfactory.AboutProjectsOrganizer;
import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.LocalMessage;
import net.sourceforge.projectfactory.client.components.LocalSplash;

import net.sourceforge.projectfactory.middleware.Connection;

import java.io.FileNotFoundException;

import javax.swing.JComponent;
import javax.swing.UIManager;

/**
 * Main class used by the application, including language option, splash window
 * and main frame construction.
 * This class is not included in any package in order to simplify the
 * program command line: java -classpath Factory.jar Factory
 * 
 * Arguments are:
 * 
 *    -language (language_code) : force the language code
 *    -console : no window is displayed, output is sent to standard console
 * 
 * @author David Lambert
 */
public class ProjectsOrganizer {
    /** Main function.  */
    public static void main(String[] args) {
        String language = JComponent.getDefaultLocale().getLanguage();
        boolean showWindow = true;
        // Loop on arguments
        for(int i=0 ; i<args.length ; i++) {
            if(args[i].equalsIgnoreCase("-language")) {
                if(i<args.length-1)
                    language = args[++i];
            }
            else if(args[i].equalsIgnoreCase("-console")) {
                showWindow = false;
            }
        }
        try {
            if(showWindow) {
                // Adopt system user interface
            	//FIXME Bug under Ubuntu
                //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            try {
				// Load dictionary based on system language
				new LocalMessage(language, 
									AboutProjectsOrganizer.getApplicationExtensions());
            } catch (FileNotFoundException ex) {
				System.err.println(
					"The system is no able to load correctly resources.");
				System.err.println(
					"The directory 'lib' must be located in your Factory");
				System.err.println(
					"installation directory.");
				System.err.println(
					"Control if this directory can be located in your system and if it's");
				System.err.println(
					"not damaged. Move it in your installation directory, or run");
				System.err.println(
					"a new installation of this software.");
            }
            if(showWindow) {
                // Display splash window
                LocalSplash.show(AboutProjectsOrganizer.getBuild(),
                                    AboutProjectsOrganizer.getCopyright(),
                                    AboutProjectsOrganizer.getLicense(),
                                    AboutProjectsOrganizer.getShortTitle());
                // Create main window associated to a new server
                new MainFrame(new Connection());
            }
            else new Connection();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }
}
