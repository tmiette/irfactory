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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/EditBoxPassword.java,v $
$Revision: 1.2 $
$Date: 2007/02/22 15:37:59 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components.editBoxes;

import java.awt.Color;

import javax.swing.JPasswordField;

import net.sourceforge.projectfactory.client.components.ComponentEnabler;
import net.sourceforge.projectfactory.xml.Base64;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Default edit box used for passwords in the application.
 * @author David Lambert
 */
public class EditBoxPassword extends JPasswordField implements ComponentEnabler {

	/** Component can be enabled. */
    private boolean enabler = true;
	
	/** Component must be saved. */
    private boolean mustSave = false;
	
	/** Component title. */
    private String title = "";
	
	/** Constructor. */
    public EditBoxPassword() {
        super();
        setOpaque(true);
        setDisabledTextColor(Color.BLACK);
    }

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
    }

	/** Returns content prepared for storage. */
    public String getWrappedText() {
        return Base64.encodeBytes((new String(getPassword()).getBytes()));
    }

	/** Returns content prepared for display. */
    public void setWrappedText(String text) {
        setText(new String(Base64.decode(text)));
    }
}
