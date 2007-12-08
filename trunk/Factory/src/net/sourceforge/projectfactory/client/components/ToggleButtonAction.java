/*

Copyright (c) 2007 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ToggleButtonAction.java,v $
$Revision: 1.1 $
$Date: 2007/03/04 21:03:44 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.components;

/**
 * Action associated to a toggle button.
 * @author David Lambert
 */
public class ToggleButtonAction {

    /** Defines the label of the button in the action bar. */
    public String button;

    /** Defines the panel name to be activated by the button. */
    public String panel;

    public ToggleButtonAction(String button, String panel) {
        this.button = button;
        this.panel = panel;
    }
}
