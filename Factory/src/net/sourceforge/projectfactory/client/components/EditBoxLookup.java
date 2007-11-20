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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/EditBoxLookup.java,v $
$Revision: 1.5 $
$Date: 2006/11/13 15:32:20 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components;

import net.sourceforge.projectfactory.client.panels.PanelData;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Edit box with a lookup.
 * @author David Lambert
 */
public class EditBoxLookup extends EditBox implements ActionListener {
	
	/** Attached lookup button. */
    private ButtonLookup button = new ButtonLookup();
	
	/** Attached lookup panel. */
    private PanelDataLookup panelSearch;
	
	/** Panel where the component is attached. */
	private PanelData panel;
    
	/** Constructor. */
    public EditBoxLookup(PanelData panel, String classname) {
		this.panel = panel;
        try {
			button.setFocusable(false);
            panelSearch = new PanelDataLookup(this, classname);
            panelSearch.setFrame(panel.getFrame());
            panelSearch.init();
            panelSearch.setVisible(false);
        } catch (Exception e) {
			panel.getFrame().addMessage(e);
        }

        button.addActionListener(this);
    }

    /** Constructor. */
    public EditBoxLookup(PanelData panel, String classname, EditBox searchKey) {
        this(panel, classname);
        if(panelSearch != null)
            panelSearch.setSearchKey(searchKey);
    }

	/** Returns the attached lookup button. */
    public ButtonLookup getButton() {
        return button;
    }

	/** Returns the attached lookup panel. */
    public PanelDataLookup getPanelLookup() {
        return panelSearch;
    }

	/** Hides the lookup panel. */
    public void collapse() {
        button.setClosedState();
        panelSearch.setVisible(false);
    }

	/** Cleans the component. */
    public void clean() {
        collapse();
        setText("");
    }

	/** Manages click on lookup button. */
	public void actionPerformed(ActionEvent e) {
		if (button.isOpened()) {
			button.setClosedState();
			panelSearch.setVisible(false);
			requestFocusInWindow();
		} else {
			button.setOpenedState();
			panelSearch.clearTextSearch();
			panelSearch.setVisible(true);
			panelSearch.runLookup();
			panelSearch.requestFocus();
		}
		panel.collapseExcept(this);
	}
}
