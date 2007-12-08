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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/LocalSplash.java,v $
$Revision: 1.5 $
$Date: 2006/12/06 09:11:45 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components;

import net.sourceforge.projectfactory.client.FrameSplash;

import java.awt.Cursor;


/**
 * Management of splash window.
 * @author David Lambert
 */
public class LocalSplash {
	
	/** Spash window. */
    private static FrameSplash splash;

	/** Changes text on progress bar. */
    public static final void addProgressValue(String text) {
        if (splash != null) 
            splash.addProgressValue(text);
    }

	/** Makes the splash window visible. */
    public static final void show(String title,
										String copyright,
										String licence,
										String shortTitle) {
		splash = new FrameSplash(title,
									copyright,
									licence,
									shortTitle);
		splash.setLocationRelativeTo(null);
		splash.setVisible(true);
		splash.resetProgress();
		setWaitCursor();
    }

	/** Makes the splash window invisible. */
    public static final void hide() {
		if (splash != null) 
			splash.setVisible(false);
		setDefaultCursor();
		splash = null;
    }

	/** Selects wait cursor. */
    public static final void setWaitCursor() {
        if (splash != null) 
            splash.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

	/** Selects default cursor. */
    public static final void setDefaultCursor() {
        if (splash != null) 
            splash.setCursor(Cursor.getDefaultCursor());
    }
}
