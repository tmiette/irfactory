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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/PanelLookup.java,v $
$Revision: 1.2 $
$Date: 2006/03/24 15:03:43 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components;

import net.sourceforge.projectfactory.client.FrameMain;

import javax.swing.JPanel;


/**
 * Lookup panel base class.
 * Lookups are used in order to display a list of elements
 * either from the server or the client and to allow selection.
 * This list could be a basic list or not (calendar view).
 * @author David Lambert
 */
public abstract class PanelLookup extends JPanel {

    /** Frame. */
    protected FrameMain frame;

	/** Initialization. */
    public void init() throws Exception {
    }

	/** Runs the lookup. */
    public void runLookup() {
    }

	/** Clears the search string. */
    public void clearTextSearch() {
    }

	/** Associates the main frame to the looup. */
    public void setFrame(FrameMain frame) {
        this.frame = frame;
    }
}
