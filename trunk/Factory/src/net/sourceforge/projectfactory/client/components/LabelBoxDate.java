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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/LabelBoxDate.java,v $
$Revision: 1.3 $
$Date: 2006/04/06 04:58:54 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components;

import net.sourceforge.projectfactory.xml.XMLWrapper;

/**
 * Label used to display a date.
 * @author David Lambert
 */
public class LabelBoxDate extends LabelBox {

    /** Constructor. */
    public LabelBoxDate() {
        super();
    }

    /** Constructor, using a pre-defined title. */
    public LabelBoxDate(String title) {
        super(title);
    }

	/** Assigns a text with date conversion. */
    public void setText(String text) {
        super.setText(XMLWrapper.unwrapLongDate(text));
    }
}
