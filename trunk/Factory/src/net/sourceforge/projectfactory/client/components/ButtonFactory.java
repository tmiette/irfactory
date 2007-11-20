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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ButtonFactory.java,v $
$Revision: 1.4 $
$Date: 2006/11/15 17:31:43 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;


/**
 * Default interface button.
 * @author David Lambert
 */
public class ButtonFactory extends JButton {
	
    /** Constructor with an icon. */
    public ButtonFactory(String title, Icon icon) {
        super(title, icon);
        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
    }

    /** Constructor. */
    public ButtonFactory(String title) {
        this(title, null);
    }
}
