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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ButtonCommand.java,v $
$Revision: 1.2 $
$Date: 2006/03/08 13:06:14 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components.buttons;


/**
 * Default interface button used for actions.
 * @author David Lambert
 */
public class ButtonCommand extends Button {
	
	/** Constructor. */
    ButtonCommand(String title) {
        super(title);
    }

	/** Hides or unhides the button. */
    public void setVisible(boolean b) {
        super.setVisible(b);
    }
}
