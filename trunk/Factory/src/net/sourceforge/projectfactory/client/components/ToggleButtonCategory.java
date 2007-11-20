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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ToggleButtonCategory.java,v $
$Revision: 1.9 $
$Date: 2007/03/17 20:00:48 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Toggle button used in order to select categories in main frame.
 * @author David Lambert
 */
public class ToggleButtonCategory extends ButtonToggleFactory 
                                        implements ActionListener {

    /** Category associated to the button. */
    private String category;

    /** Main frame. */
    private FrameMain frame;

    /** Filter associated to the category. */
    private String filter = "";

    /** Search string associated to the category. */
    private String search = "";
    
    /** Menu associated to the button. */
    private JMenuItem associatedMenu;
    
    /** List of associated items attached to a category. */
    private List<ToggleButtonAction> associatedActions;
    
    /** Constructor. */
    public ToggleButtonCategory(FrameMain frame, 
                                    String category, 
                                    String title) {
        super(title);
        this.category = category;
        this.frame = frame;
        addActionListener(this);
    }

    /** Constructor. */
    public ToggleButtonCategory(FrameMain frame, 
                                    String category, 
                                    String title, 
                                    String tip) {
        this(frame, category, LocalMessage.get(title));
        associatedMenu = new JMenuItem(LocalMessage.get(title));
        associatedMenu.addActionListener(this);
        setToolTipText(LocalMessage.get(tip));
    }

    /** Constructor. */
    public ToggleButtonCategory(FrameMain frame, 
                                    String category, 
                                    String title, 
                                    String tip, 
                                    String icon) {
        this(frame, category, title, tip);
        if(icon != null)
            setIcon(LocalIcon.get(icon));
        associatedMenu = new JMenuItem(LocalMessage.get(title), LocalIcon.get(icon));
        associatedMenu.addActionListener(this);
    }

    /** Constructor. */
    public ToggleButtonCategory(FrameMain frame, 
                                    String category, 
                                    String title, 
                                    String tip, 
                                    String icon, 
                                    List<ToggleButtonAction> actions) {
        this(frame, category, title, tip, icon);
        this.associatedActions = actions;
    }

    /** Returns the associated category. */
    public String getCategory() {
        return category;
    }

    /** Selects the button using current selected category from main frame. */
    public void reset() {
        setSelected(frame.selectionCategory.equals(category));
    }

    /** Saves the search parameters associated with the button. */
    public void saveLookupParams() {
        if (frame.selectionCategory.equals(category)) {
            setFilter(frame.panelSearch.getFilter());
            setSearch(frame.panelSearch.getTextSearch());
        }
    }

    /** Retrieves the search parameters associated with the button. */
    public void getLookupParams() {
        if (frame.selectionCategory != null && frame.selectionCategory.equals(category)) {
            frame.panelSearch.setFilter(frame.selectionCategory, getFilter());
            frame.panelSearch.setTextSearch(getSearch());
        }
    }

    /** Defines the and filter search strings for the given category. */
    public void setLookupParams(String category, String filter, 
                                String search) {
        if (this.category.equals(category)) {
            setFilter(filter);
            setSearch(search);
        }
    }

    /** Defines the filter string. */
    private void setFilter(String filter) {
        this.filter = filter;
    }

    /** Defines the search string. */
    private void setSearch(String search) {
        this.search = search;
    }

    /** Returns the filter string. */
    public String getFilter() {
        return filter;
    }

    /** Returns the search string. */
    public String getSearch() {
        return search;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml) {
        xml.xmlStart("preferencefilter");
        xml.xmlAttribute("category", category);
        xml.xmlAttribute("filter", filter);
        xml.xmlAttribute("search", search);
        xml.xmlEnd();
    }

    /** Add the associated menu to the menu bar. */    
    public void addButtonMenu(JMenu menu, int key) {
        if(key <= 9)
            associatedMenu.setAccelerator(KeyStroke.getKeyStroke('0' + key, 
                                            XMLWrapper.ControlKey));
        menu.add(associatedMenu);
    }

    /** Refreshs a menu item based on a button. */
    public void refreshMenu() {
        associatedMenu.setVisible(isVisible());
        associatedMenu.setEnabled(isVisible() && isEnabled() && !isSelected());
    }

    /** Manages clicks on the button. */
    public void actionPerformed(ActionEvent e) {
        frame.actionChangeCategory(this);
    }
    
    /** Returns associated actions. */
    public List<ToggleButtonAction> getAssociatedActions() {
        return associatedActions;
    }
}
