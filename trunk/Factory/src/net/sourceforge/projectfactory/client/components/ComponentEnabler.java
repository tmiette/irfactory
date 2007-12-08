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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ComponentEnabler.java,v $
$Revision: 1.2 $
$Date: 2006/03/08 13:03:07 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components;


/**
 * Interface defined for private components in the application.
 * @author David Lambert
 */
public interface ComponentEnabler {
	
	/** Defines if the component can be enabled or not. */
    public void setEnabler(boolean enabled);

	/** Returns true if the component can be enabled. */
    public boolean isEnabler();

	/** Defines if the component must be saved even if it's disabled or not. */
    public void setMustSave(boolean mustSave);

	/** Returns true if the component must be saved even if disabled. */
    public boolean mustSave();

	/** Sets component title. */
    public void setTitle(String title);

	/** Returns component title. */
    public String getTitle();
}
