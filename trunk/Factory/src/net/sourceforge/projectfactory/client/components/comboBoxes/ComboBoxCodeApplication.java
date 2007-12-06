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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ComboBoxCodeApplication.java,v $
$Revision: 1.2 $
$Date: 2007/02/04 23:12:14 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.components.comboBoxes;

import net.sourceforge.projectfactory.client.panels.PanelData;


/**
 * Represents a combobox managing application extensions.
 * @author David Lambert
 */
public class ComboBoxCodeApplication extends ComboBoxCode {

    /** Constructor. */
    public ComboBoxCodeApplication(PanelData panel) {
        super();
        removeItemCode("0");
        for (String extension: panel.getFrame().getApplicationExtensions())
            addItem(extension, extension.length() > 0 ? extension : " ");
    }
}

