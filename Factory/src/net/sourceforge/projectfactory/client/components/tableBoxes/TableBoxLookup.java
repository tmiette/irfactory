/*

Copyright (c) 2005, 2006, 2007 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/TableBoxLookup.java,v $
$Revision: 1.5 $
$Date: 2007/02/06 17:42:43 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components.tableBoxes;

import net.sourceforge.projectfactory.client.components.ButtonLookup;
import net.sourceforge.projectfactory.client.components.PanelCalendar;
import net.sourceforge.projectfactory.client.components.PanelDataLookup;
import net.sourceforge.projectfactory.client.components.PanelLookup;
import net.sourceforge.projectfactory.client.panels.PanelData;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Table with lookup capabilities.
 * @author David Lambert
 */
public class TableBoxLookup extends TableBox {

	/** Lookup panels attached to the table. */
    private PanelLookup[] lookupPanels;
	
	/** Lookup button. */
    private ButtonLookup buttonLookup = new ButtonLookup();
	
	/** Index of current selected column. */
    private int selectedColumn;
	
	/** Used in order to create lookup button only once. */
    private boolean buttonInitialized;

    /** Constructor. */
    public TableBoxLookup(Object... definition) {
        super(definition);
    }

	/** Creates the associated objects based on number of columns. */
    public void create(int nbcols) {
        super.create(nbcols);
        lookupPanels = new PanelLookup[nbcols + 2];
    }

	/** Attachs a lookup panel associated to a column and a class name.
	 *  This lookup will retrieve information from the server. */
    public void attachLookup(int column, PanelData panel, String classname) {
        if (isHierarchical()) 
            ++column;

        try {
            lookupPanels[column] = new PanelDataLookup(this, classname);
            lookupPanels[column].init();
            lookupPanels[column].setVisible(false);
            lookupPanels[column].setFrame(panel.getFrame());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!buttonInitialized) {
            buttonPanel.add(buttonLookup, 
                            new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0, 
                                                   GridBagConstraints.CENTER, 
                                                   GridBagConstraints.NONE, 
                                                   new Insets(0, 0, 0, 0), 0, 
                                                   0));
            buttonLookup.addActionListener(new ActionSelection(panel, this));
        }

        buttonInitialized = true;
    }

	/** Attachs a lookup panel associated to a column and a class name.
	 *  This lookup will retrieve information from another table. */
    public void attachLookup(int column, PanelData panel, String classname, 
                             TableBox reference) {
        if (isHierarchical()) {
            ++column;
        }

        try {
            lookupPanels[column] = new PanelDataLookup(this, classname, reference);
            lookupPanels[column].init();
            lookupPanels[column].setVisible(false);
            lookupPanels[column].setFrame(panel.getFrame());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!buttonInitialized) {
            buttonPanel.add(buttonLookup, 
                            new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0, 
                                                   GridBagConstraints.CENTER, 
                                                   GridBagConstraints.NONE, 
                                                   new Insets(0, 0, 0, 0), 0, 
                                                   0));
            buttonLookup.addActionListener(new ActionSelection(panel, this));
        }

        buttonInitialized = true;
    }

	/** Defines the column as a date, with a lookup. */
    public void setDateType(int column, PanelData panel) {
        setDateType(column);

        try {
            lookupPanels[column] = new PanelCalendar(this);
            lookupPanels[column].init();
            lookupPanels[column].setVisible(false);
            lookupPanels[column].setFrame(panel.getFrame());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!buttonInitialized) {
            buttonPanel.add(buttonLookup, 
                            new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, 
                                                   GridBagConstraints.CENTER, 
                                                   GridBagConstraints.NONE, 
                                                   new Insets(0, 0, 0, 0), 0, 
                                                   0));
            buttonLookup.addActionListener(new ActionSelection(panel, this));
        }

        buttonInitialized = true;
    }

	/** Manages selection changes in the table. */
    public void changeSelection() {
        int column = getSelectedColumn();
        selectedColumn = column;

        if (buttonLookup.isOpened()) {
            for (int i = 0; i < countPanelLookups(); i++)
                if (lookupPanels[i] != null) {
                    lookupPanels[i].setVisible(column == i);

                    if (column == i) {
                        lookupPanels[i].runLookup();
                    }
                }
        }
    }

	/** Returns the lookup button. */
    public ButtonLookup getButtonLookup() {
        return buttonLookup;
    }

	/** Returns the number of attached lookup panels. */
    public int countPanelLookups() {
        return lookupPanels.length;
    }

	/** Returns the lookup panel at given column. */
    public PanelLookup getPanelLookup(int column) {
        return lookupPanels[column];
    }

	/** Runs the lookup panel at selected column. */
    public void runLookup() {
        lookupPanels[selectedColumn].runLookup();
    }

	/** Hides lookup panels. */
    private void collapseLookups() {
        for (int i = 0; i < countPanelLookups(); i++)
            if (lookupPanels[i] != null) {
                lookupPanels[i].setVisible(false);
            }
    }

	/** Hides lookup panels. */
    public void collapse() {
        collapseLookups();
        buttonLookup.setClosedState();
    }

	/** Cleans the table. */
    public void clean() {
        collapse();
        super.clean();
    }

	/** Delete selected row. */
    public void deleteRow() {
        super.deleteRow();

        if ((getRowCount() == 0) || (getSelectedRow() < 0) || 
            (getSelectedRow() > (getRowCount() - 1))) {
            collapse();
        }
    }

	/**
	 * Private listener attached to buttons. 
	 */
	private class ActionSelection implements ActionListener {
		
		/** Table. */
        private TableBoxLookup edit;
		
		/** Lookup panel. */
        private PanelData panel;

		/** Constructor. */
        ActionSelection(PanelData panel, TableBoxLookup edit) {
            this.edit = edit;
            this.panel = panel;
        }

		/** Manages clicks on lookup button. */
        public void actionPerformed(ActionEvent e) {
            PanelLookup panelSearch = edit.getPanelLookup(edit.selectedColumn);
            ButtonLookup button = edit.getButtonLookup();

            if ((edit.getRowCount() != 0) && (edit.getSelectedRow() >= 0)) {
                if (button.isOpened()) {
                    button.setClosedState();

                    if (panelSearch != null) {
                        panelSearch.setVisible(false);
                    }
                } else {
                    button.setOpenedState();

                    if (panelSearch != null) {
                        panelSearch.clearTextSearch();
                        panelSearch.setVisible(true);
                        panelSearch.runLookup();
                    }
                }
            }

            panel.collapseExcept(edit);
        }
    }
}
