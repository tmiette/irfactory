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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ButtonToggleFactory.java,v $
$Revision: 1.4 $
$Date: 2007/03/04 21:03:44 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components;

import javax.swing.JToggleButton;



/**
  * Toggle Button.
  * @author David Lambert
  */
public class ButtonToggle extends JToggleButton {

    /** Associated panel. */
    private String associatedPanel;
	
    /** Constructor. */
    public ButtonToggle(String title) {
        super(title);
    }

    /** Constructor with tooltip and icon. */
    public ButtonToggle(String text, String tip, String icon) {
        super(LocalMessage.get(text), LocalIcon.get(icon));
        setToolTipText(LocalMessage.get(tip));
    }

    /** Sets associated panel. */
    public void setAssociatedPanel(String associatedPanel) {
        this.associatedPanel = associatedPanel;
    }

    /** Gets associated panel. */
    public String getAssociatedPanel() {
        return associatedPanel;
    }
}
