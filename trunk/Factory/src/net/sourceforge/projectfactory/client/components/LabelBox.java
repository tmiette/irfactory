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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/LabelBox.java,v $
$Revision: 1.7 $
$Date: 2007/02/06 17:42:43 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components;

import javax.swing.JLabel;


/**
 * Simple label to be used on data panels.
 * @author David Lambert
 */
public class LabelBox extends JLabel {
	
	/** Label title. */
    private String title = "";
	
	/** icon name (filename). */
	private String icon = "";

    /** Constructor. */
    public LabelBox() {
        super();
    }

    /** Constructor, using a pre-defined title. */
    public LabelBox(String title) {
        super(title);
    }

	/** Assigns a title. */
    public void setTitle(String title) {
        this.title = title;
    }

	/** Returns the title. */
    public String getTitle() {
        return title;
    }
	
	/** Assigns an icon. */
	public void setIcon(String filename) {
		this.icon = filename;
		setIcon(LocalIcon.get(filename));
	}
	
	/** Returns icon name (filename). */
	public String getIconName(){
		return icon;
	}
    
    /** Inserts a text into the label. */
    public void addLabel(String label) {
        if (getText().length()>0)
            setText("<html><b>" + LocalMessage.get(label, getText()) + "</b></html>");
    }

    /** Inserts a text into the label. */
    public void addLabelPercent(String label) {
        if (getText().length()>0) {
            try {
                int fillPercent = Integer.parseInt(getText());
                if (fillPercent == 100)
                    setIcon("complete100.gif");
                else if (fillPercent >= 75)
                    setIcon("complete75.gif");
                else if (fillPercent >= 50)
                    setIcon("complete50.gif");
                else if (fillPercent >= 25)
                    setIcon("complete25.gif");
                else if (fillPercent >= 1)
                    setIcon("complete0.gif");
            } catch (java.lang.NumberFormatException ex) {
                setIcon("");
            }
            finally {
                addLabel(label);
            }
        }
    }
}
