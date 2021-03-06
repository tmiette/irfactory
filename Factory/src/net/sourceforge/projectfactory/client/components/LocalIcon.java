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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/LocalIcon.java,v $
$Revision: 1.4 $
$Date: 2006/12/20 12:03:14 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.components;

import java.util.HashMap;

import javax.swing.ImageIcon;


/**
 * Keep in memory a collection of icons used by the client.
 * Those icons are loaded on the fly upon request.
 * @author David Lambert
 */
public class LocalIcon {

    /** The icons that are stored in memory. */
    private static final HashMap icons = new HashMap();

    /** Returns the icon with the corresponding label. */
    public static ImageIcon get(String label) {
        if (label == null || label.length() == 0)
            return null;

        ImageIcon icon = (ImageIcon)icons.get(label);

        icon = new ImageIcon(System.getProperty("user.dir") + "/lib/" + label);

        if (icon == null || icon.getIconWidth() <= 0)
            icon = new ImageIcon(System.getProperty("user.dir") + "/../" + label);

        if (icon != null && icon.getIconWidth() > 0)
            icons.put(label, icon);

        return icon;
    }
}
