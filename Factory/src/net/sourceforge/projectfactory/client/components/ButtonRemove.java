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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ButtonRemove.java,v $
$Revision: 1.4 $
$Date: 2006/12/20 12:03:21 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components;


/**
 * Defines a button used to remove or delete an object.
 * @author David Lambert
 */
public class ButtonRemove extends Button {
	
	/** Constructor. */
    public ButtonRemove() {
        super("");
        setIcon(LocalIcon.get("minus.gif"));
    }
}
