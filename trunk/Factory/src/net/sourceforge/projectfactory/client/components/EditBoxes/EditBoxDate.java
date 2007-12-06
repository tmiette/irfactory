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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/EditBoxDate.java,v $
$Revision: 1.4 $
$Date: 2006/04/06 04:58:54 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components.EditBoxes;

import net.sourceforge.projectfactory.client.components.ButtonLookup;
import net.sourceforge.projectfactory.client.components.PanelCalendar;
import net.sourceforge.projectfactory.client.panels.PanelData;
import net.sourceforge.projectfactory.xml.XMLWrapper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Edit box used for dates (with a lookup).
 * @author David Lambert
 */
public class EditBoxDate extends EditBox implements ActionListener {
	
	/** Lookup button. */
    private ButtonLookup button = new ButtonLookup();
	
	/** Calendar panel attached to the button. */
    private PanelCalendar panelCalendar = new PanelCalendar(this);
	
	/** Indicates the attached calendar panel is activated. */
    private boolean activated;

	/** Panel where the component is attached. */
	private PanelData panel;
	
	/** Constructor. */
    public EditBoxDate(PanelData panel) {
        super();
		this.panel = panel;

        try {
			button.setFocusable(false);
            panelCalendar.init();
            panelCalendar.setVisible(false);
        } catch (Exception e) {
			panel.getFrame().addMessage(e);
        }

        button.addActionListener(this);
    }

	/** Returns the attached lookup button. */
    public ButtonLookup getButton() {
        return button;
    }

	/** Returns the	attached calendar panel. */
    public PanelCalendar getPanelCalendar() {
        return panelCalendar;
    }

	/** Forces calendar dates refresh. */
    public void reloadDays() {
        panelCalendar.reloadDays();
    }

	/** Hides the attached calendar. */
    public void collapse() {
        button.setClosedState();
        panelCalendar.setVisible(false);
    }
	
	/** Cleans the component. */
    public void clean() {
        collapse();
        setText("");
    }

	/** Indicates if the attached calendar panel is activated. */
    public boolean isActivated() {
        return activated;
    }

	/** Activates the attached calendar panel. */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

	/** Sets text with date conversion. */
    public void setText(String text) {
        super.setText(XMLWrapper.unwrapDate(text));
    }

	/** Returns text with date conversion. */
    public String getText() {
        return XMLWrapper.wrapDate(super.getText());
    }

	/** Returns the text with no date conversion. */
    public String getPlainText() {
        return super.getText();
    }

	/** Manages click on lookup button. */
	public void actionPerformed(ActionEvent e) {
		if (button.isOpened()) {
			button.setClosedState();
			panelCalendar.setVisible(false);
			panel.collapseExcept(this);
		} else {
			button.setOpenedState();
			panelCalendar.setVisible(true);
			panelCalendar.reloadDays();
			panel.collapseExcept(this);
		}
	}
}
