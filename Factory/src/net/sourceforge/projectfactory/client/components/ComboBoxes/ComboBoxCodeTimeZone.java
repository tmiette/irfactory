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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ComboBoxCodeTimeZone.java,v $
$Revision: 1.2 $
$Date: 2007/01/03 16:39:30 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.components.ComboBoxes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;


/**
 * Represents a combobox managing time zones.
 * @author David Lambert
 */
public class ComboBoxCodeTimeZone extends ComboBoxCode {

    /** Constructor. */
    public ComboBoxCodeTimeZone() {
        super();

        List<String> list = new ArrayList(200);
        String[] timeZones = TimeZone.getAvailableIDs();

        for (int i = 0; i < timeZones.length; i++)
            list.add(timeZones[i]);

        Collections.sort(list);

        for (int i = 0; i < timeZones.length; i++)
            addItem(list.get(i).toString(), 
                    "<html><font color=black>" + list.get(i) + 
                    "</font></html>");
    }
}
