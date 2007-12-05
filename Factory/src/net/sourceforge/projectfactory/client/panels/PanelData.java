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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/PanelData.java,v $
$Revision: 1.28 $
$Date: 2007/02/05 22:16:30 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.ArrayList;

import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.PrintDocHtml;
import net.sourceforge.projectfactory.client.components.ButtonAdd;
import net.sourceforge.projectfactory.client.components.ButtonLookup;
import net.sourceforge.projectfactory.client.components.ButtonRemove;
import net.sourceforge.projectfactory.client.components.ButtonToggle;
import net.sourceforge.projectfactory.client.components.CheckBox;
import net.sourceforge.projectfactory.client.components.ComboBox;
import net.sourceforge.projectfactory.client.components.ComboBoxCode;
import net.sourceforge.projectfactory.client.components.EditBox;
import net.sourceforge.projectfactory.client.components.EditBoxDate;
import net.sourceforge.projectfactory.client.components.EditBoxLookup;
import net.sourceforge.projectfactory.client.components.EditBoxPassword;
import net.sourceforge.projectfactory.client.components.LabelBox;
import net.sourceforge.projectfactory.client.components.LabelBoxDate;
import net.sourceforge.projectfactory.client.components.LocalMessage;
import net.sourceforge.projectfactory.client.components.PanelLookup;
import net.sourceforge.projectfactory.client.components.TableBox;
import net.sourceforge.projectfactory.client.components.TableBoxLookup;
import net.sourceforge.projectfactory.client.components.TableBoxXML;
import net.sourceforge.projectfactory.client.components.TextBox;
import net.sourceforge.projectfactory.xml.ReaderXML;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Super class for panels that contain data.
 * @author David Lambert
 */
public abstract class PanelData extends JPanel implements ActionListener {

    /** Maximum number of sub panels. */
    static final int MAXPANELS = 10;

    /** Column number of the edit boxes. */
    static int EDITCOLUMN = 10;

    /** Column number of the labels. */
    static int LABELCOLUMN = 1;

    /** Name of the package that constains the components. */
    static final String CompPack = 
        "net.sourceforge.projectfactory.client.components";

    /** Name of the package that constains the data panels. */
    static final String UserPack = 
        "net.sourceforge.projectfactory.client.panels";

    /** Current row number used during creation. */
    private short currentRow;

    /** Current panel used during creation. */
    private JPanel currentPanel;

    /** Internal panel number. */
    private int panelNumber = 1;

    /** Tabs. */
    protected JTabbedPane tabs = new JTabbedPane();

    /** Panels inserted in the tabs. */
    protected JPanel[] panels;

    /** XML Tag for the panel. */
    private String tagpanel;

    /** Reference to the main frame. */
    private MainFrame frame;

    /** Object ID. */
    protected final EditBox iid = new EditBox();

    /** Parent object ID. */
    protected final EditBox parentIid = new EditBox();

    /** Entity name. */
    protected final EditBox name = new EditBox();

    /** Creation time stamp. */
    protected final LabelBoxDate created = new LabelBoxDate();

    /** Update time stamp. */
    protected final LabelBoxDate updated = new LabelBoxDate();

    /** Creation actor. */
    protected final LabelBox createdBy = new LabelBox();

    /** Update actor. */
    protected final LabelBox updatedBy = new LabelBox();

    /** Revision. */
    protected final EditBox revision = new EditBox();

    /** Active checkbox. */
    protected final CheckBox active = new CheckBox("label:active");

    /** Comments or description. */
    protected final TextBox comment = new TextBox();

    /** Lists of the components inside the panel. */
    protected List<Object> components = new ArrayList();

    /** Insets with zero values. */
    private final Insets nullInsets = new Insets(0, 0, 0, 0);

    /** Last label inserted in a panel. */
    private JLabel lastLabel;

    /** Array of classes used for code reflection. */
    private Class[] panelClasses = new Class[10];

    /** Number of classes related to the array of classes. */
    private int nbClasses;

    /** Constructor. */
    protected PanelData(MainFrame frame) {
        this.frame = frame;

        if (LocalMessage.isRightToLeft()) {
            LABELCOLUMN = 10;
            EDITCOLUMN = 1;
        }

        panels = new JPanel[MAXPANELS];

        GridBagLayout layout = new GridBagLayout();

        for (int i = 0; i < MAXPANELS; i++) {
            panels[i] = new JPanel();
            panels[i].setLayout(layout);
        }

        currentPanel = panels[0];
        components.add(currentPanel);
        // Populates the array of classes 
        // in order to be able to fetch on fields.
        for (Class c = getClass(); c != null; c = c.getSuperclass()) {
            panelClasses[nbClasses++] = c;

            if (c.getName().equals(UserPack + ".PanelData"))
                break;
        }
    }

    /** Returns the frame. */
    public final MainFrame getFrame() {
        return frame;
    }

    /** Assigns a request string to be used when the 'default' query is sent to
     *  the server. */
    public void addDefaultValue(String tag, String value) {
        getFrame().addDefaultValue(tag, value);
    }

    /** Assigns a request string to be used when the 'default' query is sent to
     *  the server. */
    public void addDefaultValue(String tag, EditBox value) {
        getFrame().addDefaultValue(tag, value.getText());
    }

    /** Assigns a request string to be used when the 'default' query is sent to
     *  the server. */
    public void addDefaultValue(String tag, ComboBoxCode value) {
        getFrame().addDefaultValue(tag, value.getSelectedCode());
    }

    /** Action on a 'new' button when pressed from a data panel. */
    public void actionNew(String panelName) {
        getFrame().actionNew(panelName);
    }

    /** Returns the panel from index number. */
    protected final JPanel getPanel(int panelnum) {
        try {
            return panels[panelnum - 1];
        } catch (Exception e) {
            frame.addMessage("FAT", "Incorrect panel number:" + panelnum);
            return null;
        }
    }

    /** Defines a new panel in the panel stack and sets it as current one. */
    protected final void nextPanel() {
        ++panelNumber;

        try {
            currentPanel = panels[panelNumber - 1];
            components.add(currentPanel);
        } catch (Exception e) {
            frame.addMessage("FAT", "Incorrect panel number:" + panelNumber);
        }
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        this.tagpanel = tagpanel;
        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }

    /** Adds in the panel a text. */
    private final void add(String labeltext) {
        ++currentRow;

        if (labeltext.length() > 0) {
            currentPanel.add(lastLabel = 
                             new JLabel(LocalMessage.get(labeltext) + 
                                        " "), 
                             new GridBagConstraints(LABELCOLUMN, currentRow, 1, 
                                                    1, 0.0, 0.0, 
                                                    LocalMessage.isRightToLeft() ? 
                                                    GridBagConstraints.WEST : 
                                                    GridBagConstraints.EAST, 
                                                    GridBagConstraints.NONE, 
                                                    nullInsets, 0, 0));
        }
    }

    /** Attachs the latest created label to the new component. */
    private void attachLastLabel(Component component) {
        if (lastLabel != null)
            lastLabel.setLabelFor(component);
        lastLabel = null;
    }

    /** Adds in the panel a text followed by an editbox. */
    protected final void add(String labeltext, EditBox text) {
        text.setTitle(LocalMessage.get(labeltext));
        add(labeltext);

        if (LocalMessage.isRightToLeft()) {
            text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        components.add(text);
        attachLastLabel(text);
        currentPanel.add(text, 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 1, 1, 
                                                10.0, 0.0, 
                                                GridBagConstraints.WEST, 
                                                GridBagConstraints.HORIZONTAL, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a text followed by an editbox. */
    protected final void add(String labeltext, EditBoxPassword text) {
        text.setTitle(LocalMessage.get(labeltext));
        add(labeltext);

        if (LocalMessage.isRightToLeft()) {
            text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        components.add(text);
        attachLastLabel(text);
        currentPanel.add(text, 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 1, 1, 
                                                10.0, 0.0, 
                                                GridBagConstraints.WEST, 
                                                GridBagConstraints.HORIZONTAL, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a text followed by a labelbox. */
    protected final void add(String labeltext, LabelBox label) {
        label.setTitle(LocalMessage.get(labeltext));
        add(labeltext);

        if (LocalMessage.isRightToLeft()) {
            label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        components.add(label);
        attachLastLabel(label);
        currentPanel.add(label, 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 1, 1, 
                                                10.0, 0.0, 
                                                GridBagConstraints.WEST, 
                                                GridBagConstraints.HORIZONTAL, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a subpanel. */
    protected final void add(JPanel apanel) {
        ++currentRow;
        currentPanel.add(apanel, 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 10, 10, 
                                                10.0, 10.0, 
                                                GridBagConstraints.CENTER, 
                                                GridBagConstraints.BOTH, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a checkbox. */
    protected final void add(JCheckBox checkbox) {
        ++currentRow;

        if (LocalMessage.isRightToLeft()) {
            checkbox.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        components.add(checkbox);
        currentPanel.add(checkbox, 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 1, 1, 
                                                10.0, 0.0, 
                                                GridBagConstraints.WEST, 
                                                GridBagConstraints.HORIZONTAL, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a label. */
    protected final void add(LabelBox label) {
        ++currentRow;
        components.add(label);
        currentPanel.add(label, 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 1, 1, 
                                                10.0, 0.0, 
                                                GridBagConstraints.WEST, 
                                                GridBagConstraints.HORIZONTAL, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a text used for instructions. */
    protected final void addInstruction(String text) {
        ++currentRow;

        JLabel label = new JLabel("<html><i>" + LocalMessage.get(text) + "</i></html>");

        if (LocalMessage.isRightToLeft()) {
            label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        currentPanel.add(label, 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 1, 1, 
                                                10.0, 0.0, 
                                                GridBagConstraints.WEST, 
                                                GridBagConstraints.HORIZONTAL, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a text followed by a text field and a button. */
    protected final void add(String labeltext, JTextField text, 
                             AbstractButton button) {
        add(labeltext);
        attachLastLabel(text);

        if (LocalMessage.isRightToLeft()) {
            text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            button.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        currentPanel.add(button, 
                         new GridBagConstraints(2, currentRow, 2, 1, 0.0, 0.0, 
                                                GridBagConstraints.CENTER, 
                                                GridBagConstraints.NONE, 
                                                nullInsets, 0, 0));
        currentPanel.add(text, 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 1, 1, 
                                                10.0, 0.0, 
                                                GridBagConstraints.WEST, 
                                                GridBagConstraints.HORIZONTAL, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a text followed by an edit box with lookup. */
    protected final void add(String labeltext, EditBoxLookup edit) {
        edit.setTitle(LocalMessage.get(labeltext));
        attachLastLabel(edit);

        if (LocalMessage.isRightToLeft()) {
            edit.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        add(labeltext, edit, edit.getButton());
        ++currentRow;
        components.add(edit);
        currentPanel.add(edit.getPanelLookup(), 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 1, 1, 
                                                10.0, 0.0, 
                                                GridBagConstraints.WEST, 
                                                GridBagConstraints.BOTH, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a text followed by an edit box date. */
    protected final void add(String labeltext, EditBoxDate edit) {
        edit.setTitle(LocalMessage.get(labeltext));
        attachLastLabel(edit);

        if (LocalMessage.isRightToLeft()) {
            edit.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        add(labeltext, edit, edit.getButton());
        ++currentRow;
        components.add(edit);
        currentPanel.add(edit.getPanelCalendar(), 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 1, 1, 
                                                10.0, 0.0, 
                                                GridBagConstraints.WEST, 
                                                GridBagConstraints.BOTH, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a text followed by a combobox. */
    protected final void add(String labeltext, ComboBox edit) {
        edit.setTitle(LocalMessage.get(labeltext));
        attachLastLabel(edit);

        if (LocalMessage.isRightToLeft()) {
            edit.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        add(labeltext);
        components.add(edit);
        currentPanel.add(edit, 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 1, 1, 
                                                10.0, 0.0, 
                                                LocalMessage.isRightToLeft() ? 
                                                GridBagConstraints.EAST : 
                                                GridBagConstraints.WEST, 
                                                GridBagConstraints.BOTH, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a text followed by a scroll panel. */
    protected final void add(String labeltext, JScrollPane scroll) {
        add(labeltext);
        attachLastLabel(scroll);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        currentPanel.add(scroll, 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 1, 1, 
                                                10.0, 3.0, 
                                                LocalMessage.isRightToLeft() ? 
                                                GridBagConstraints.EAST : 
                                                GridBagConstraints.WEST, 
                                                GridBagConstraints.BOTH, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a text followed by a scroll panel and a subpanel. */
    private final void add(String labeltext, JScrollPane scroll, 
                           JPanel subPanel) {
        add(labeltext);
        attachLastLabel(scroll);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        currentPanel.add(subPanel, 
                         new GridBagConstraints(2, currentRow, 1, 1, 0.0, 0.0, 
                                                GridBagConstraints.CENTER, 
                                                GridBagConstraints.NONE, 
                                                nullInsets, 0, 0));
        currentPanel.add(scroll, 
                         new GridBagConstraints(EDITCOLUMN, currentRow, 1, 1, 
                                                10.0, 3.0, 
                                                LocalMessage.isRightToLeft() ? 
                                                GridBagConstraints.EAST : 
                                                GridBagConstraints.WEST, 
                                                GridBagConstraints.BOTH, 
                                                nullInsets, 0, 0));
    }

    /** Adds in the panel a text followed by a table. */
    protected final void add(String labeltext, TableBox table) {
        table.setTitle(LocalMessage.get(labeltext));

        if (LocalMessage.isRightToLeft()) {
            table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        JScrollPane scroll = new JScrollPane();
        scroll.getViewport().add(table, null);
        scroll.setOpaque(true);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        components.add(table);
        add(labeltext, scroll, table.getButtonPanel());
    }

    /** Adds in the panel a text followed by a table with lookup. */
    protected final void add(String labeltext, TableBoxLookup table) {
        table.setTitle(LocalMessage.get(labeltext));

        if (LocalMessage.isRightToLeft()) {
            table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        JScrollPane scroll = new JScrollPane();
        scroll.getViewport().add(table, null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        components.add(table);
        add(labeltext, scroll, table.getButtonPanel());
        ++currentRow;

        for (int i = 0; i < table.countPanelLookups(); i++) {
            PanelLookup lookup = table.getPanelLookup(i);

            if (lookup != null) {
                currentPanel.add(lookup, 
                                 new GridBagConstraints(EDITCOLUMN, currentRow++, 
                                                        1, 1, 10.0, 0.0, 
                                                        GridBagConstraints.WEST, 
                                                        GridBagConstraints.BOTH, 
                                                        nullInsets, 0, 0));
            }
        }
    }

    /** Adds in the panel a text followed by a text box. */
    protected final void add(String labeltext, TextBox text) {
        text.setTitle(LocalMessage.get(labeltext));

        if (LocalMessage.isRightToLeft()) {
            text.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        JScrollPane scroll = new JScrollPane();
        scroll.setOpaque(true);
        scroll.getViewport().add(text, null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        components.add(text);
        add(labeltext, scroll);
    }

    /** Adds in the panel a predefined header but nothing visible. */
    protected final void addHeader() {
        add("", iid);
        add("", parentIid);
        add("", name);
        add(active);
        name.setVisible(false);
        iid.setVisible(false);
        parentIid.setVisible(false);
    }

    /** Adds in the panel a predefined header (name, active flag...). */
    protected final void addHeaderName() {
        add("", iid);
        add("", parentIid);
        add("label:name", name);
        add(active);
        iid.setVisible(false);
        parentIid.setVisible(false);
    }

    /** Adds in the panel predefined comment text box. */
    protected final void addComments() {
        add("label:comment", comment);
    }

    /** Adds in the panel predefined audit information. */
    protected final void addAudit() {
        add("", created);
        add("", updated);
        add("", revision);
        created.setVisible(false);
        updated.setVisible(false);
        revision.setVisible(false);
        add("label:created", createdBy);
        add("label:updated", updatedBy);
    }
    
    /** Returns iid. */
    public String getIid() {
        return iid.getText();
    }

    /** Returns name. */
    public String getName() {
        return name.getText();
    }

    /** Adds in the panel a label retrieved from dictionary. */
    protected final void addPanel(String labeltext) throws Exception {
        tabs.addTab(LocalMessage.get(labeltext), currentPanel);
        currentPanel.setName(LocalMessage.get(labeltext));
    }

    /** Adds in the panel a label retrieved from dictionary and an icon. */
    protected final void addPanel(String labeltext, 
                                  Icon icon) throws Exception {
        addPanel(labeltext);
        tabs.setIconAt(0, icon);
    }

    /** Cleans or resets every component in the panel. */
    public final void clean() {
        String fieldType;

        try {
            for (int ic = nbClasses - 1; ic >= 0; ic--) {
                Class c = panelClasses[ic];
                Field[] fields = c.getDeclaredFields();

                for (int i = 0; i < fields.length; i++) {
                    if (!Modifier.isPrivate(fields[i].getModifiers())) {
                        fieldType = fields[i].getType().getName();

                        if (fieldType.equals(CompPack + ".EditBox")) {
                            ((EditBox)fields[i].get(this)).setText("");
                        } else if (fieldType.equals(CompPack + 
                                                    ".EditBoxPassword")) {
                            ((EditBoxPassword)fields[i].get(this)).setText("");
                        } else if (fieldType.equals(CompPack + 
                                                    ".EditBoxLookup")) {
                            ((EditBoxLookup)fields[i].get(this)).clean();
                        } else if (fieldType.equals(CompPack + 
                                                    ".EditBoxDate")) {
                            ((EditBoxDate)fields[i].get(this)).clean();
                        } else if (fieldType.equals(CompPack + ".TextBox")) {
                            ((TextBox)fields[i].get(this)).setText("");
                        } else if (fieldType.equals(CompPack + ".CheckBox")) {
                            ((CheckBox)fields[i].get(this)).setSelected(false);
                        } else if (fieldType.startsWith(CompPack + 
                                                        ".LabelBox")) {
                            ((LabelBox)fields[i].get(this)).setText("");
                        } else if (fieldType.equals(CompPack + ".TableBox")) {
                            ((TableBox)fields[i].get(this)).clean();
                        } else if (fieldType.startsWith(CompPack + 
                                                        ".ComboBox")) {
                            ((ComboBox)fields[i].get(this)).setSelectedIndex(0);
                        } else if (fieldType.equals(CompPack + 
                                                    ".TableBoxLookup")) {
                            ((TableBoxLookup)fields[i].get(this)).clean();
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            frame.addMessage(e);
        }
    }

    /** Hides every lookup in the panel except specified one. */
    public final void collapseExcept(Object exclude) {
        String fieldType;

        try {
            for (int ic = nbClasses - 1; ic >= 0; ic--) {
                Class c = panelClasses[ic];
                Field[] fields = c.getDeclaredFields();

                for (int i = 0; i < fields.length; i++) {
                    if (!Modifier.isPrivate(fields[i].getModifiers())) {
                        fieldType = fields[i].getType().getName();

                        if (fields[i].get(this) == exclude) {
                            continue;
                        } else if (fieldType.equals(CompPack + 
                                                    ".EditBoxLookup")) {
                            ((EditBoxLookup)fields[i].get(this)).collapse();
                        } else if (fieldType.equals(CompPack + 
                                                    ".EditBoxDate")) {
                            ((EditBoxDate)fields[i].get(this)).collapse();
                        } else if (fieldType.equals(CompPack + 
                                                    ".TableBoxLookup")) {
                            ((TableBoxLookup)fields[i].get(this)).collapse();
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            frame.addMessage(e);
        }
    }

    /** Enables or disables data panel contents (fields). */
    public final void setEnabled(boolean enabled) {
        setEnabledExcept(enabled, null);
    }

    /** Enables or disables data panel contents (fields).
	 *  Applies to every field except one specified. */
    public final void setEnabledExcept(boolean enabled, Object exclude) {
        String fieldType;
        EditBox editbox;
        EditBoxPassword editboxpassword;
        EditBoxLookup editboxlookup;
        EditBoxDate editboxdate;
        TextBox textbox;
        CheckBox checkbox;
        ComboBox combobox;
        TableBox tablebox;
        ButtonAdd buttonadd;
        ButtonRemove buttonremove;
        ButtonLookup buttonlookup;
        ButtonToggle buttoncommand;

        try {
            for (int ic = nbClasses - 1; ic >= 0; ic--) {
                Class c = panelClasses[ic];
                Field[] fields = c.getDeclaredFields();

                for (int i = 0; i < fields.length; i++) {
                    if (!Modifier.isPrivate(fields[i].getModifiers())) {
                        fieldType = fields[i].getType().getName();

                        if (fields[i].get(this) == exclude)
                            continue;

                        if (fieldType.equals(CompPack + ".EditBox")) {
                            editbox = (EditBox)fields[i].get(this);
                            editbox.setEnabled(enabled && editbox.isEnabler());
                        } else if (fieldType.equals(CompPack + 
                                                    ".EditBoxPassword")) {
                            editboxpassword = 
                                    (EditBoxPassword)fields[i].get(this);
                            editboxpassword.setEnabled(enabled && 
                                                       editboxpassword.isEnabler());
                        } else if (fieldType.equals(CompPack + 
                                                    ".EditBoxLookup")) {
                            editboxlookup = (EditBoxLookup)fields[i].get(this);
                            editboxlookup.setEnabled(enabled && 
                                                     editboxlookup.isEnabler());
                            editboxlookup.getButton().setVisible(enabled && 
                                                                 editboxlookup.isEnabler());
                        } else if (fieldType.equals(CompPack + 
                                                    ".EditBoxDate")) {
                            editboxdate = (EditBoxDate)fields[i].get(this);
                            editboxdate.setEnabled(enabled && 
                                                   editboxdate.isEnabler());
                            editboxdate.getButton().setVisible(enabled && 
                                                               editboxdate.isEnabler());

                            if (enabled)
                                editboxdate.setActivated(enabled);
                        } else if (fieldType.equals(CompPack + ".TextBox")) {
                            textbox = (TextBox)fields[i].get(this);
                            textbox.setEnabled(enabled && textbox.isEnabler());
                        } else if (fieldType.equals(CompPack + ".CheckBox")) {
                            checkbox = (CheckBox)fields[i].get(this);
                            checkbox.setEnabled(enabled && 
                                                checkbox.isEnabler());
                        } else if (fieldType.startsWith(CompPack + 
                                                        ".ComboBox")) {
                            combobox = (ComboBox)fields[i].get(this);
                            combobox.setEnabled(enabled && 
                                                combobox.isEnabler());
                        } else if (fieldType.equals(CompPack + ".ButtonAdd")) {
                            buttonadd = (ButtonAdd)fields[i].get(this);
                            buttonadd.setVisible(enabled);
                        } else if (fieldType.equals(CompPack + 
                                                    ".ButtonRemove")) {
                            buttonremove = (ButtonRemove)fields[i].get(this);
                            buttonremove.setVisible(enabled);
                        } else if (fieldType.equals(CompPack + 
                                                    ".ButtonLookup")) {
                            buttonlookup = (ButtonLookup)fields[i].get(this);
                            buttonlookup.setVisible(enabled);
                        } else if (fieldType.startsWith(CompPack + 
                                                        ".TableBox")) {
                            tablebox = (TableBox)fields[i].get(this);
                            tablebox.setSoftEnabled(enabled && 
                                                    tablebox.isEnabler());
                            tablebox.getButtonPanel().setVisible(enabled && 
                                                                 tablebox.isEnabler());

                            if (enabled) {
                                tablebox.setActivated(enabled);
                            }
                        } else if (fieldType.equals(CompPack + 
                                                    ".ButtonToggleFactory")) {
                            buttoncommand = 
                                    (ButtonToggle)fields[i].get(this);
                            buttoncommand.setEnabled(!enabled);
                            buttoncommand.setSelected(false);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            frame.addMessage(e);
        }
    }

    /** Moves the focus to the first available and visible field. */
    public final void setFocus() {
        String fieldType;
        EditBox editbox;
        EditBoxPassword editboxpassword;
        TextBox textbox;
        CheckBox checkbox;
        ComboBox combobox;
        TableBox tablebox;

        requestFocusInWindow();
        try {
            for (int ic = nbClasses - 1; ic >= 0; ic--) {
                Class c = panelClasses[ic];
                Field[] fields = c.getDeclaredFields();

                for (int i = 0; i < fields.length; i++) {
                    if (!Modifier.isPrivate(fields[i].getModifiers())) {
                        fieldType = fields[i].getType().getName();

                        if (fieldType.startsWith(CompPack + 
                                                 ".EditBoxPassword")) {
                            editboxpassword = 
                                    (EditBoxPassword)fields[i].get(this);
                            if (editboxpassword.isEnabler() && 
                                editboxpassword.requestFocusInWindow())
                                return;
                        } else if (fieldType.startsWith(CompPack + 
                                                        ".EditBox")) {
                            editbox = (EditBox)fields[i].get(this);
                            if (editbox.isEnabler() && 
                                editbox.requestFocusInWindow())
                                return;
                        } else if (fieldType.equals(CompPack + ".TextBox")) {
                            textbox = (TextBox)fields[i].get(this);
                            if (textbox != comment && textbox.isEnabler() && 
                                textbox.requestFocusInWindow())
                                return;
                        } else if (fieldType.equals(CompPack + ".CheckBox")) {
                            checkbox = (CheckBox)fields[i].get(this);
                            if (checkbox != active && checkbox.isEnabler() && 
                                checkbox.requestFocusInWindow())
                                return;
                        } else if (fieldType.startsWith(CompPack + 
                                                        ".ComboBox")) {
                            combobox = (ComboBox)fields[i].get(this);
                            if (combobox.isEnabler() && 
                                combobox.requestFocusInWindow())
                                return;
                        } else if (fieldType.startsWith(CompPack + 
                                                        ".TableBox")) {
                            tablebox = (TableBox)fields[i].get(this);
                            if (tablebox.isEnabler() && 
                                tablebox.requestFocusInWindow())
                                return;
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            frame.addMessage(e);
        }
    }

    /** Writes the object as an XML output. */
    public final void xmlOut(WriterXML xml, 
                             boolean details) throws IOException {
        String fieldType;
        EditBox editbox;
        EditBoxPassword editboxpassword;
        TextBox textbox;
        ComboBox combobox;
        ComboBoxCode comboboxcode;
        CheckBox checkbox;
        TableBox tablebox;
        xml.xmlStart(tagpanel);
        if (details) {
            try {
                for (int ic = nbClasses - 1; ic >= 0; ic--) {
                    Class c = panelClasses[ic];
                    Field[] fields = c.getDeclaredFields();

                    for (int i = 0; i < fields.length; i++) {
                        if (!Modifier.isPrivate(fields[i].getModifiers())) {
                            fieldType = fields[i].getType().getName();

                            if (fieldType.startsWith(CompPack + 
                                                     ".EditBoxPassword")) {
                                editboxpassword = 
                                        (EditBoxPassword)fields[i].get(this);

                                if ((editboxpassword.getWrappedText().length() > 
                                     0) && 
                                    (editboxpassword.isEnabler() || editboxpassword.mustSave())) {
                                    xml.xmlOut(fields[i].getName().toLowerCase(), 
                                               editboxpassword.getWrappedText());
                                }
                            } else if (fieldType.startsWith(CompPack + 
                                                            ".EditBox")) {
                                editbox = (EditBox)fields[i].get(this);

                                if ((editbox.getText().length() > 0) && 
                                    (editbox.isEnabler() || 
                                     editbox.mustSave())) {
                                    xml.xmlOut(fields[i].getName().toLowerCase(), 
                                               editbox.getText());
                                }
                            } else if (fieldType.equals(CompPack + 
                                                        ".TextBox")) {
                                textbox = (TextBox)fields[i].get(this);

                                if ((textbox.getText().length() > 0) && 
                                    (textbox.isEnabler() || 
                                     textbox.mustSave())) {
                                    xml.xmlOut(fields[i].getName().toLowerCase(), 
                                               textbox.getText());
                                }
                            } else if (fieldType.startsWith(CompPack + 
                                                            ".ComboBoxCode")) {
                                comboboxcode = 
                                        (ComboBoxCode)fields[i].get(this);

                                int index = comboboxcode.getSelectedIndex();

                                if (((index >= 0) && 
                                     comboboxcode.isEnabler()) || 
                                    comboboxcode.mustSave()) {
                                    xml.xmlOut(fields[i].getName().toLowerCase(), 
                                               comboboxcode.getSelectedCode());
                                }
                            } else if (fieldType.startsWith(CompPack + 
                                                            ".ComboBox")) {
                                combobox = (ComboBox)fields[i].get(this);

                                int index = combobox.getSelectedIndex();

                                if (((index >= 0) && combobox.isEnabler()) || 
                                    combobox.mustSave()) {
                                    xml.xmlOut(fields[i].getName().toLowerCase(), 
                                               combobox.getSelectedItem().toString());
                                }
                            } else if (fieldType.equals(CompPack + 
                                                        ".CheckBox")) {
                                checkbox = (CheckBox)fields[i].get(this);

                                if (checkbox.isEnabler() || 
                                    checkbox.mustSave()) {
                                    xml.xmlOut(fields[i].getName().toLowerCase(), 
                                               (checkbox.isSelected() ? "y" : 
                                                "n"));
                                }
                            } else if (fieldType.startsWith(CompPack + 
                                                            ".TableBox")) {
                                tablebox = (TableBox)fields[i].get(this);

                                if (tablebox.isEnabler() || 
                                    tablebox.mustSave()) {
                                    tablebox.xmlOut(xml, 
                                                    fields[i].getName().toLowerCase());
                                }
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                frame.addMessage(e);
            }
        }
        frame.xmlOutDefaultValues(xml);
        xml.xmlEnd();
    }

    /** Starts the object from an XML input. */
    public final void xmlTagIn(String tag, ReaderXML reader) {
        try {
            for (int ic = nbClasses - 1; ic >= 0; ic--) {
                Class c = panelClasses[ic];
                Field[] fields = c.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].getName().toLowerCase().equals(tag)) {
                        if (!Modifier.isPrivate(fields[i].getModifiers())) {
                            String fieldType = fields[i].getType().getName();
                            if (fieldType.startsWith(CompPack + ".TableBox")) {
                                TableBox tablebox = 
                                    (TableBox)fields[i].get(this);
                                new TableBoxXML(tablebox).xmlIn(reader);
                                tablebox.sort();
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            frame.addMessage(e);
        }
    }

    /** Reads the object from an XML input. */
    public final void xmlIn(String tag, String value) {
        try {
            for (int ic = nbClasses - 1; ic >= 0; ic--) {
                Class c = panelClasses[ic];
                Field[] fields = c.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].getName().toLowerCase().equals(tag)) {
                        if (!Modifier.isPrivate(fields[i].getModifiers())) {
                            String fieldType = fields[i].getType().getName();
                            if (fieldType.startsWith(CompPack + 
                                                     ".EditBoxPassword")) {
                                ((EditBoxPassword)fields[i].get(this)).setWrappedText(value);
                            } else if (fieldType.startsWith(CompPack + 
                                                            ".EditBox")) {
                                ((EditBox)fields[i].get(this)).setText(value);
                            } else if (fieldType.equals(CompPack + 
                                                        ".TextBox")) {
                                ((TextBox)fields[i].get(this)).setWrapText(value);
                            } else if (fieldType.startsWith(CompPack + 
                                                            ".ComboBoxCode")) {
                                ((ComboBoxCode)fields[i].get(this)).setSelectedCode(value);
                            } else if (fieldType.startsWith(CompPack + 
                                                            ".ComboBox")) {
                                ((ComboBox)fields[i].get(this)).setSelectedItem(value);
                            } else if (fieldType.equals(CompPack + 
                                                        ".CheckBox")) {
                                ((CheckBox)fields[i].get(this)).setSelected(value.equals("y"));
                            } else if (fieldType.equals(CompPack + 
                                                        ".LabelBox")) {
                                ((LabelBox)fields[i].get(this)).setText(replaceLabels(value));
                            } else if (fieldType.equals(CompPack + 
                                                        ".LabelBoxDate")) {
                                ((LabelBoxDate)fields[i].get(this)).setText(value);
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            frame.addMessage(e);
        }
    }

    /** Manages panel content after it's populated from XML. */
    public void exitXmlIn() {
        if(createdBy.isVisible())
            createdBy.setText(createdBy.getText() + 
                          ((created.getText().length() > 0) ? 
                           (" [" + created.getText() + "]") : ""));
        if(updatedBy.isVisible())
            updatedBy.setText(updatedBy.getText() + 
                          ((updated.getText().length() > 0) ? 
                           (" [" + updated.getText() + "] - " + 
                            LocalMessage.get("label:revision") + " " + 
                            revision.getText()) : ""));
        if(!canEdit())
            setEnabled(false);
    }
	
	/** Creates an action based on the provided string. */
	public void createAction(String value) {
	}

    /** Indicates if the panel can be edited. */
    public boolean canEdit() {
        return true;
    }

    /** Indicates if the panel can be deleted. */
    public boolean canDelete() {
        return true;
    }

    /** Indicates if the panel can be saved. */
    public boolean canSave() {
        return true;
    }

    /** Returns the tag associated to the panel. */
    public String getTagpanel() {
        return tagpanel;
    }

    /** Replaces labels defined with a tag by the appropriate label
	 *  retrieved from dictionary. */
    private String replaceLabels(String text) {
        String out = XMLWrapper.replaceAll(text, "@label:hours", LocalMessage.get("label:hours"));
        out = XMLWrapper.replaceAll(out, "@label:hour", LocalMessage.get("label:hour"));
        out = XMLWrapper.replaceAll(out, "@label:halfdays", LocalMessage.get("label:halfdays"));
        out = XMLWrapper.replaceAll(out, "@label:halfday", LocalMessage.get("label:halfday"));
        out = XMLWrapper.replaceAll(out, "@label:days", LocalMessage.get("label:days"));
        out = XMLWrapper.replaceAll(out, "@label:day", LocalMessage.get("label:day"));
        return out;
    }

    /** Adds an object to the frame action bar. */
    protected final void addActionBarButton(ButtonToggle button) {
        button.setVisible(false);
        frame.addActionBarButton(button);
    }

    /** Attaches a local button to a menu item. */    
    protected void refreshMenu(JMenuItem menuItem, ButtonToggle button) {
        menuItem.setText(LocalMessage.get("menu:new:menu", button.getText()));
        menuItem.setVisible(true);
        menuItem.addActionListener(this);
    }

    /** Refreshs menus attached to the panel.
	 *  Does nothing by default and may be overriden by subclasses. */
    public void refreshMenus() {
    }

    /** Hides or unhides command buttons. */
    public final void setButtonsVisible(boolean visible) {
        String fieldType;

        try {
            for (int ic = nbClasses - 1; ic >= 0; ic--) {
                Class c = panelClasses[ic];
                Field[] fields = c.getDeclaredFields();

                for (int i = 0; i < fields.length; i++) {
                    if (!Modifier.isPrivate(fields[i].getModifiers())) {
                        fieldType = fields[i].getType().getName();

                        if (fieldType.equals(CompPack + 
                                             ".ButtonToggleFactory")) {
                            ((ButtonToggle)fields[i].get(this)).setVisible(visible);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            frame.addMessage(e);
        }
    }

    /** Inserted in HTML output document a title. */
    private final void print(PrintDocHtml doc, 
                             String title) throws IOException {
        doc.write(title);
    }

    /** Prepares a string for HTML output. */
    final String prepareHtml(String out) {
        String ret = XMLWrapper.replaceAll(out, "<html>", "");
        ret = XMLWrapper.replaceAll(ret, "</html>", "");
        ret = XMLWrapper.replaceAll(ret, 
                                    "label:yes", 
                                    LocalMessage.get("label:yes"));
        ret = XMLWrapper.replaceAll(ret, 
                                    "label:no", 
                                    LocalMessage.get("label:no"));
        ret = XMLWrapper.fixHTML(ret);
        return ret;
    }

    /** Inserted in HTML output document a title and a value. */
    private final void print(PrintDocHtml doc, String title, 
                             String value) throws IOException {
        if (value.length() > 0)
            doc.write(title, prepareHtml(value));
    }

    /** Inserted in HTML output document a title, a value and an icon. */
    private final void print(PrintDocHtml doc, String title, String value, 
                             String icon) throws IOException {
        if (value.length() > 0)
            doc.write(title, prepareHtml(value), icon);
    }

    /** Inserted in HTML output document a title and a table. */
    private final void print(PrintDocHtml doc, String title, 
                             TableBox table) throws IOException {
        if (table.getRowCount() == 0)
            return;

        doc.write(title, prepareHtml(table.toHtml()));
    }

    /** Generates a printable HTML document. */
    public final void print(WriterXML err, boolean diagramOnly, 
                            boolean bug) throws IOException {
        String fieldType;
        EditBox editbox;
        EditBoxDate editboxdate;
        EditBoxPassword editboxpassword;
        TextBox textbox;
        ComboBox combobox;
        ComboBoxCode comboboxcode;
        CheckBox checkbox;
        TableBox tablebox;
        LabelBox labelbox;
        LabelBoxDate labelboxdate;
        JPanel panel;
        boolean table = false;

        PrintDocHtml doc = new PrintDocHtml(err);

        doc.open(LocalMessage.get("label:" + tagpanel) + 
                 (diagramOnly ? "-" + LocalMessage.get("tab:diagram") : "") + 
                 (bug ? "-" + LocalMessage.get("label:bugreport") : ""), 
                 name.getText());

        if (bug) {
            print(doc, LocalMessage.get("label:bugreport"));
            doc.openTable();
            print(doc, LocalMessage.get("label:instructions"), 
                  LocalMessage.get("instruction:bugreport"));
            print(doc, LocalMessage.get("label:url"), 
                  "<a href=" + frame.getBugPage() + ">" + frame.getBugPage() + 
                  "</a>");
            print(doc, LocalMessage.get("label:bug:navigation"), 
                  LocalMessage.get("instruction:bug:navigation"));
            doc.closeTable();
            print(doc, LocalMessage.get("label:messages"));
            doc.openTable();
            print(doc, "", frame.getMessages());
            doc.closeTable();
        }

        if (!diagramOnly) {
            for (Object field: components) {
                fieldType = field.getClass().getName();

                if (fieldType.equals("javax.swing.JPanel")) {
                    if (table)
                        doc.closeTable();

                    panel = (JPanel)field;
                    print(doc, panel.getName());
                    doc.openTable();
                    table = true;
                } else if (fieldType.equals(CompPack + ".EditBoxDate")) {
                    editboxdate = (EditBoxDate)field;

                    if (editboxdate.isVisible() && 
                        (editboxdate.getPlainText().length() > 0)) {
                        print(doc, editboxdate.getTitle(), 
                              editboxdate.getPlainText());
                    }
                } else if (fieldType.startsWith(CompPack + 
                                                ".EditBoxPassword")) {
                    editboxpassword = (EditBoxPassword)field;

                    if (editboxpassword.isVisible() && 
                        (editboxpassword.getPassword().length > 0)) {
                        print(doc, editboxpassword.getTitle(), "*****");
                    }
                } else if (fieldType.startsWith(CompPack + ".EditBox")) {
                    editbox = (EditBox)field;

                    if (editbox.isVisible() && 
                        (editbox.getText().length() > 0)) {
                        print(doc, editbox.getTitle(), editbox.getText());
                    }
                } else if (fieldType.equals(CompPack + ".TextBox")) {
                    textbox = (TextBox)field;

                    if (textbox.isVisible() && 
                        (textbox.getText().length() > 0)) {
                        print(doc, textbox.getTitle(), textbox.getHTMLText());
                    }
                } else if (fieldType.startsWith(CompPack + ".ComboBoxCode")) {
                    comboboxcode = (ComboBoxCode)field;

                    int index = comboboxcode.getSelectedIndex();

                    if (comboboxcode.isVisible() && (index >= 0)) {
                        print(doc, comboboxcode.getTitle(), 
                              comboboxcode.getSelectedLabel(), 
                              comboboxcode.getSelectedIconName());
                    }
                } else if (fieldType.startsWith(CompPack + ".ComboBox")) {
                    combobox = (ComboBox)field;

                    int index = combobox.getSelectedIndex();

                    if (combobox.isVisible() && (index >= 0)) {
                        print(doc, combobox.getTitle(), 
                              combobox.getSelectedItem().toString());
                    }
                } else if (fieldType.equals(CompPack + ".CheckBox")) {
                    checkbox = (CheckBox)field;

                    if (checkbox.isVisible()) {
                        print(doc, checkbox.getTitle(), 
                              checkbox.isSelected() ? "label:yes" : 
                              "label:no");
                    }
                } else if (fieldType.equals(CompPack + ".LabelBox")) {
                    labelbox = (LabelBox)field;

                    if (labelbox.isVisible() && 
                        (labelbox.getText().length() > 0)) {
                        print(doc, labelbox.getTitle(), labelbox.getText(), 
                              labelbox.getIconName());
                    }
                } else if (fieldType.equals(CompPack + ".LabelBoxDate")) {
                    labelboxdate = (LabelBoxDate)field;

                    if (labelboxdate.isVisible() && 
                        (labelboxdate.getText().length() > 0)) {
                        print(doc, labelboxdate.getTitle(), 
                              labelboxdate.getText());
                    }
                } else if (fieldType.startsWith(CompPack + ".TableBox")) {
                    tablebox = (TableBox)field;
                    print(doc, tablebox.getTitle(), tablebox);
                }
            }

            if (table) {
                doc.closeTable();
            }
        } else {
            for (int i = 1; i <= frame.getPeriods(); i++)
                doc.writePiture(i, frame.getDiagram(i));
        }

        doc.close();
        doc.display();
    }

    /** Manages buttons and menu items. */
    public void actionPerformed(ActionEvent e) {
    }
}
