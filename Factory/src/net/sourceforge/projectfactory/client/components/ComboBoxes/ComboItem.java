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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ComboItem.java,v $
$Revision: 1.5 $
$Date: 2006/12/22 12:24:07 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.components.ComboBoxes;

import javax.swing.ImageIcon;

import net.sourceforge.projectfactory.client.components.LocalIcon;

/**
 * Represents an item stored in comboboxes
 * composed by a code, a label and an icon.
 * @author David Lambert
 */
public class ComboItem {
	
	/** Code. */
	String code;
	
	/** Label. */
	String label;
	
	/** Icon. */
	ImageIcon icon;
	
	/** Icon file name. */
	String filename;
	
	/** Constructor. Code and label. */
	public ComboItem(String code, String label) {
		this.code = code;
		this.label = label;
	}
	
	/** Constructor. Code, label and icon file name. */
	public ComboItem(String code, String label, String filename) {
		this(code, label);
		if(filename != null) this.icon = LocalIcon.get(filename);
		this.filename = filename;
	}
    
    /** Constructor. Clone. */
    public ComboItem(ComboItem item) {
        this(item != null ? item.code : "0", 
                item != null ? item.label : " ", 
                item != null ? item.filename : "");
    }
	
	/** Returns the label to be displayed in the combobox. */
	public String toString() {
		return label;
	}
	
	/** Compares this item to the specified object. */
	public boolean equals(Object object) {
		if (object == this) 
			return true;
		
		if(object == null) 
			return false;
		
		if(object.getClass() == getClass()) 
			return this.code.equals(((ComboItem)object).code);
		
		return this.label.equals(object.toString());
	}
}

