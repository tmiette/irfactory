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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ComboBox.java,v $
$Revision: 1.3 $
$Date: 2007/02/22 15:37:59 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components;

import java.awt.Color;

import javax.swing.JComboBox;

import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Default combo box in the application.
 * @author David Lambert
 */
public class ComboBox extends JComboBox implements ComponentEnabler {

	/** Component can be enabled. */
    private boolean enabler = true;
	
	/** Component must be saved. */
    private boolean mustSave = false;
	
	/** Component title. */
    private String title = "";
	
	/** Defines if the component can be enabled or not. */
    public void setEnabler(boolean enabler) {
        this.enabler = enabler;
    }

	/** Returns true if the component can be enabled. */
    public boolean isEnabler() {
        return enabler;
    }

	/** Defines if the component must be saved even if it's disabled or not. */
    public void setMustSave(boolean mustSave) {
        this.mustSave = mustSave;
    }

	/** Returns true if the component must be saved even if disabled. */
    public boolean mustSave() {
        return mustSave;
    }

	/** Sets component title. */
    public void setTitle(String title) {
        this.title = title;
    }

	/** Returns component title. */
    public String getTitle() {
        return title;
    }

	/** Enables or disables the component. */
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setForeground(Color.BLACK);
        setBackground(enabled ? XMLWrapper.editColor : Color.WHITE);
        setOpaque(enabled);
    }
}
