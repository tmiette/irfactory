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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/ComboBoxCode.java,v $
$Revision: 1.10 $
$Date: 2007/01/27 19:55:15 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.components.comboBoxes;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.sourceforge.projectfactory.client.components.LocalMessage;


/**
 * Represents a combobox managing codes, labels and icons.
 * @author David Lambert
 */
public class ComboBoxCode extends ComboBox {

    /** Default constructor. */
    public ComboBoxCode() {
        setRenderer(new ComboBoxRenderer());
        addItem("0", " ");
    }

    /** Constructor. */
    public ComboBoxCode(String label) {
        this(LocalMessage.getComboInstance(label));
    }

    /** Constructor used in order to make a clone. */
    public ComboBoxCode(ComboBoxCode other) {
        setRenderer(new ComboBoxRenderer());
        if(other != null) {
            ComboItem item;
            int size = other.getItemCount();
            for (int i = 0; i < size; i++) {
                item = (ComboItem)other.getItemAt(i);
                if (item != null)
                    addItem(new ComboItem(item.code, item.label, item.filename));
            }
        }
        else
            addItem(new ComboItem(" ", " "));
    }

    /** Reload in order. */
    public void reload(String label) {
        ComboBoxCode other = LocalMessage.getComboInstance(label);
        removeAllItems();
        if(other != null) {
            ComboItem item;
            int size = other.getItemCount();
            for (int i = 0; i < size; i++) {
                item = (ComboItem)other.getItemAt(i);
                if (item != null)
                    addItem(new ComboItem(item.code, item.label, item.filename));
            }
        }
    }

    /** Add an item: code and label. */
    public void addItem(String code, String label) {
        addItem(new ComboItem(code, label));
    }

    /** Add an item: code and label with no duplicate. */
    public void addItemNoDup(String code, String label) {
        ComboItem item;
        int size = getItemCount();

        for (int i = 0; i < size; i++) {
            item = (ComboItem)getItemAt(i);
            if (item.code.equals(code))
                return;
        }
        addItem(new ComboItem(code, label));
    }

    /** Add an item: code, label and icon file name. */
    public void addItem(String code, String label, String filename) {
        addItem(new ComboItem(code, label, filename));
    }

    /** Add an item: code only. The label is retrieved from dictionary. */
    public void addItem(String code) {
        addItem(code, LocalMessage.get(code));
    }

    public void removeItemCode(String code) {
        ComboItem item;
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            item = (ComboItem)getItemAt(i);
            if (item.code.equals(code)) {
                removeItemAt(i);
                return;
            }
        }
    }

    public void removeLastItems(int index) {
        for (int i = getItemCount() - 1; i >= index; i--)
            removeItemAt(i);
    }

    /** Returns the code associated to the given label. */
    public String getItemCode(String label) {
        ComboItem item;
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            item = (ComboItem)getItemAt(i);
            if (item.label.equals(label))
                return item.code;
        }
        return "";
    }

    /** Returns the item associated to the given code. */
    public ComboItem getItem(String label) {
        ComboItem item;
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            item = (ComboItem)getItemAt(i);
            if (item.code.equals(label))
                return item;
        }
        return null;
    }

    /** Returns the label associated to the given code. */
    public String getItemLabel(String code) {
        ComboItem item;
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            item = (ComboItem)getItemAt(i);
            if (item.code.equals(code))
                return item.label;
        }
        return "";
    }

    /** Returns the icon associated to the given label. */
    public ImageIcon getIconLabel(String label) {
        ComboItem item;
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            item = (ComboItem)getItemAt(i);
            if (item.label.equals(label))
                return item.icon;
        }
        return null;
    }

    /** Returns the icon file name associated to the given label. */
    public String getIconName(String label) {
        ComboItem item;
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            item = (ComboItem)getItemAt(i);
            if (item.label.equals(label))
                return item.filename;
        }
        return null;
    }

    /** Returns the code associated to selected item. */
    public String getSelectedCode() {
        ComboItem item = (ComboItem)getSelectedItem();
        if (item == null)
            return "";
        return item.code;
    }

    /** Returns the label associated to selected item. */
    public String getSelectedLabel() {
        ComboItem item = (ComboItem)getSelectedItem();
        if (item == null)
            return "";
        return item.label;
    }

    /** Returns the icon file name associated to selected item. */
    public String getSelectedIconName() {
        ComboItem item = (ComboItem)getSelectedItem();
        if (item == null)
            return "";
        if (item.icon == null)
            return "";
        return item.filename;
    }

    /** Selects the given code in the combobox. */
    public void setSelectedCode(String code) {
        ComboItem item;
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            item = (ComboItem)getItemAt(i);
            if (code.equals(item.code)) {
                setSelectedIndex(i);
                return;
            }
        }
    }

    /**
	 *  Represents the class used in order to render or display
	 *  the items stored in the combobox.
	 */
    private class ComboBoxRenderer extends JLabel implements ListCellRenderer {

        /** Constructor. */
        public ComboBoxRenderer() {
            setOpaque(true);
        }

        /** Returns the component used for display. */
        public Component getListCellRendererComponent(JList list, Object value, 
                                                      int index, 
                                                      boolean isSelected, 
                                                      boolean cellHasFocus) {

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            ComboItem item = (ComboItem)value;
            if (item != null) {
                setText(item.label);
                setIcon(item.icon);
            }
            return this;
        }
    }
}
