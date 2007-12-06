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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/PanelDataLookup.java,v $
$Revision: 1.35 $
$Date: 2007/02/12 15:02:26 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import java.util.List;
import java.util.ArrayList;

import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.ComboBoxes.ComboBoxCode;
import net.sourceforge.projectfactory.client.components.EditBoxes.EditBox;
import net.sourceforge.projectfactory.client.components.EditBoxes.EditBoxLookup;
import net.sourceforge.projectfactory.client.components.TableBoxes.TableBox;
import net.sourceforge.projectfactory.client.components.TableBoxes.TableBoxLookup;
import net.sourceforge.projectfactory.client.xml.ImportDataXML;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Panel defined in order to manage lookups.
 * The goal is to populate a list of elements based on search criteria.
 * This panel is used from the main application window, in order
 * to select data and manage it (display, edit, save, delete...).
 * It's used also attached to a field in a panel: an editbox or a table.
 * In that case it's used as a 'prompt' list.
 * @author David Lambert
 */
public class PanelDataLookup extends PanelLookup implements ActionListener {

    /** Thread used in order to lookup data. */
    private ThreadLookup threadLookup;

    /** Panel used in order to manage filters and search string. */
    private JPanel panelSelection = new JPanel();

    /** Server name display. */
    private JLabel serverName = new JLabel();

    /** Combobox used for filters. */
    private ComboBoxCode comboFilter = new ComboBoxCode("");

    /** Search string. */
    private JTextField textSearch = new JTextField();

    /** Key Search string. */
    private EditBox searchKey;

    /** Root node of the tree. */
    private DefaultMutableTreeNode selectionRoot = new DefaultMutableTreeNode();

    /** Tree model. */
    private DefaultTreeModel selectionTreeModel = new DefaultTreeModel(selectionRoot);

    /** Path to selected tree item. */
    private TreePath selectionPath;

    /** Tree used in order to store and display retrieved information. */
    public JTree treeSelection = new JTree(selectionTreeModel);

    /** Editbox attached to the lookup panel. */
    private EditBoxLookup editboxlookup;

    /** Tablebox attached to the lookup panel. */
    private TableBoxLookup tableboxlookup;

    /** Classname associated to the lookup panel. */
    private String classname;

    /** Array of current parent nodes indexed by level in hierarchy. */
    private DefaultMutableTreeNode[] lastNode = new DefaultMutableTreeNode[10];

    /** Tablebox used as reference for a lookup. */
    private TableBox reference;

    /** Last search string. */
    private String lastSearch;
	
	/** Expanded nodes. */
	private List<TaskTreeNode> expandeds = new ArrayList(10);

    /** Constructor: panel search. */
    public PanelDataLookup(MainFrame frame) {
        this.frame = frame;
    }

    /** Constructor: edit box. */
    public PanelDataLookup(EditBoxLookup editboxlookup, String classname) {
        this.editboxlookup = editboxlookup;
        this.classname = classname;
    }

    /** Constructor: table. */
    public PanelDataLookup(TableBoxLookup tableboxlookup, String classname) {
        this.tableboxlookup = tableboxlookup;
        this.classname = classname;
    }

    /** Constructor: table, internal search. */
    public PanelDataLookup(TableBoxLookup tableboxlookup, String classname, 
                           TableBox reference) {
        this(tableboxlookup, classname);
        this.reference = reference;
    }

    /** Initialization (UI). */
    public void init() throws Exception {
        setLayout(new BorderLayout());

        if (editboxlookup != null || tableboxlookup != null)
            setBorder(new TitledBorder(""));

        setMinimumSize(new Dimension(150, 100));
        panelSelection.setLayout(new GridBagLayout());

        textSearch.addKeyListener(new ActionSearch());
        treeSelection.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeSelection.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
                    public void valueChanged(TreeSelectionEvent e) {
                        treeSelectionChanged();
                    }
                });
        treeSelection.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
					public void treeExpanded(TreeExpansionEvent e) {
						treeExpandChanged(e, true);
					}

					public void treeCollapsed(TreeExpansionEvent e) {
						treeExpandChanged(e, false);
					}
                });


        ToolTipManager.sharedInstance().registerComponent(treeSelection);

        treeSelection.setCellRenderer(new TreeRenderer());
        treeSelection.setAutoscrolls(true);
        treeSelection.setRootVisible(false);
        treeSelection.setShowsRootHandles(true);
        treeSelection.setOpaque(true);

        if (editboxlookup == null && tableboxlookup == null) {
            if (LocalMessage.isRightToLeft()) {
                comboFilter.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            }

            comboFilter.addActionListener(this);
            comboFilter.setEnabled(true);
            
            panelSelection.add(comboFilter, 
                               new GridBagConstraints(1, 10, 1, 1, 10.0, 1.0, 
                                                      GridBagConstraints.CENTER, 
                                                      GridBagConstraints.HORIZONTAL, 
                                                      new Insets(0, 0, 0, 0), 
                                                      0, 0));
        }

        if (LocalMessage.isRightToLeft()) {
            textSearch.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            treeSelection.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        panelSelection.add(textSearch, 
                           new GridBagConstraints(1, 30, 9, 1, 10.0, 1.0, 
                                                  GridBagConstraints.CENTER, 
                                                  GridBagConstraints.HORIZONTAL, 
                                                  new Insets(0, 0, 0, 0), 0, 
                                                  0));

        JScrollPane scrollListSelection = new JScrollPane(treeSelection, 
                                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollListSelection.setAutoscrolls(true);
        add(panelSelection, BorderLayout.NORTH);
        add(scrollListSelection, BorderLayout.CENTER);
        add(serverName, BorderLayout.SOUTH);
    }

    /** Manages change on search criteria. */
    public void actionPerformed(ActionEvent e) {
        lastSearch = null;
        runLookup();
    }

    /** Defines the search key. */
    public void setSearchKey(EditBox searchKey) {
        this.searchKey = searchKey;
    }

    /** Runs the lookup thread and let it populate the tree. */
    public void runLookup() {
        if (frame.blockLookup)
            return;

        frame.setCursor(true);
        frame.setServerNameProcessing();

        if (threadLookup != null) {
            threadLookup.terminate();
            try {
                while (!threadLookup.isTerminated())
                    Thread.sleep(20);
            } catch (InterruptedException ex) {
                return;
            }
        }

        if (editboxlookup != null) 
            threadLookup = new ThreadLookup(this, frame, "", classname, null);
        else if (tableboxlookup != null) 
            threadLookup = new ThreadLookup(this, frame, "", classname, reference);
        else 
            threadLookup = new ThreadLookup(this, frame, frame.selectionCategory, "", null);
    }

    /** Runs the lookup thread and wait for its completion. */
    public void runLookupAndWait() {
        if (frame.blockLookup)
            return;

        runLookup();

        if (threadLookup != null) {
            try {
                while (!threadLookup.isTerminated())
                    Thread.sleep(20);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }

    /** Manages expand or collapse. */
    private void treeExpandChanged(TreeExpansionEvent e, boolean expand) {
        TreePath expandPath = e.getPath();

        if (expandPath != null) {
            DefaultMutableTreeNode selectionNode = 
                (DefaultMutableTreeNode)expandPath.getLastPathComponent();

            TaskTreeNode selection = 
                (TaskTreeNode)(selectionNode.getUserObject());
                
            if(selection == null)
                return;
				
			if(expand)
				expandeds.add(selection);
			else
				expandeds.remove(selection);
        }
    }

    /** Manage a change of selection in the tree. */
    private void treeSelectionChanged() {
        selectionPath = treeSelection.getSelectionPath();

        if (selectionPath != null) {
            DefaultMutableTreeNode selectionNode = 
                (DefaultMutableTreeNode)selectionPath.getLastPathComponent();

            TaskTreeNode selection = 
                (TaskTreeNode)(selectionNode.getUserObject());
                
            if(selection == null)
                return;
                
            if (editboxlookup != null) {
                editboxlookup.setText(selection.name);
            } else if (tableboxlookup != null) {
                tableboxlookup.setValue(selection.name);
            } else {
                if(selection.iid.length() == 0 && selection.name.length() == 0) 
                    return;
                WriterXML query = new WriterXML("query:get");
                WriterXML answer = new WriterXML();
                query.xmlStart(selection.category);
                query.xmlAttribute("iid", selection.iid);
                query.xmlAttribute("name", selection.name);
                query.xmlEnd();
                try {
                    frame.setCursor(true);
                    frame.querySession(query, answer);
                    new ImportDataXML(frame).xmlIn(answer, null, false);
                } catch (Exception e) {
                    frame.addMessage(e);
                } finally {
                    if (frame.getCurrent() != frame.panelBlank) {
                        frame.getCurrent().setEnabled(false);
                        frame.setActionVisible(true);
                    }
                    frame.setCursor(false);
                }
            }
        }
    }

    /** Selects in the tree the item referenced by a category and a name. */
    public void selectItem(String category, String iid, String name, 
                           String summary, String tip) {
        TaskTreeNode find = new TaskTreeNode(category, iid, name, summary, tip);

        for (int i = 0; i < treeSelection.getRowCount(); i++) {
            selectionPath = treeSelection.getPathForRow(i);
            DefaultMutableTreeNode selectionNode = 
                (DefaultMutableTreeNode)selectionPath.getLastPathComponent();
            TaskTreeNode selection = (TaskTreeNode)(selectionNode.getUserObject());

            if (selection != null && selection.equals(find)) {
				treeSelection.setSelectionPath(selectionPath);
				treeSelection.scrollPathToVisible(selectionPath);
				return;
            }
        }

        frame.addMessage("WAR", LocalMessage.get("warning:notvisible", name));
        WriterXML query = new WriterXML("query:get");
        WriterXML answer = new WriterXML();
        query.xmlStart(category);
        query.xmlAttribute("iid", iid);
        query.xmlAttribute("name", name);
        query.xmlEnd();
        try {
            frame.setCursor(true);
            frame.querySession(query, answer);
            new ImportDataXML(frame).xmlIn(answer, null, false);
        } catch (Exception e) {
            frame.addMessage(e);
        } finally {
            if (frame.getCurrent() != frame.panelBlank) {
                frame.getCurrent().setEnabled(false);
                frame.setActionVisible(true);
            }
            frame.setCursor(false);
        }
    }

    /** Inserts a new item in the tree, attached to a parent one level up. */
    void addSelectionTree(DefaultMutableTreeNode selectionTree, int level) {
        synchronized (selectionRoot) {
            if (level > 1) {
                if (lastNode[level - 1] != null)
                    lastNode[level - 1].add(selectionTree);
            } else
                selectionRoot.add(selectionTree);

            lastNode[level] = selectionTree;
        }
    }

    /** Inserts a new item referenced by a category, a name and a summary
     *  in the tree. */
    void addSelectionTree(String category, String iid, String name, 
                          String summary, String tip, int level) {
        addSelectionTree(new DefaultMutableTreeNode(new TaskTreeNode(category, 
                                                                     iid, name, 
                                                                     summary, 
                                                                     tip)), 
                         level);
    }

    /** Removes all items in the tree. */
    void removeSelectionTree() {
        synchronized (selectionRoot) {
            selectionRoot.removeAllChildren();
        }
    }

    /** Reloads the tree. */
    void reloadSelectionTree() {
        synchronized (selectionRoot) {
            selectionTreeModel.reload();
			for (int i = 0; i < treeSelection.getRowCount(); i++) {
				TreePath expandPath = treeSelection.getPathForRow(i);
				DefaultMutableTreeNode selectionNode = 
					(DefaultMutableTreeNode)expandPath.getLastPathComponent();
				TaskTreeNode selection = (TaskTreeNode)(selectionNode.getUserObject());
				
				if (selection != null) {
					for(TaskTreeNode node: expandeds) {	
						if(selection.equals(node)) {
							treeSelection.expandPath(expandPath);
							break;
						}
					}
				}
			}
            displayStatus("");
        }
    }

    /** Deselects any selected item in the tree. */
    public void unselectTree() {
        treeSelection.setSelectionPath(null);
        selectionPath = null;
    }

    /** Reselects the previouly selected item in the tree. */
    public void reselectTree() {
        treeSelection.setSelectionPath(selectionPath);
        treeSelectionChanged();
    }

    /** Returns the search string. */
    public String getTextSearch() {
        return textSearch.getText();
    }

    /** Clears the search string. */
    public void clearTextSearch() {
        textSearch.setText("");
    }

    /** Defines the search string. */
    public void setTextSearch(String text) {
        textSearch.setText(text);
    }

    /** Requests focus. */
    public void requestFocus() {
        textSearch.requestFocusInWindow();
    }

    /** Assigns a text to the status bar. */
    void displayStatus(String text) {
        frame.setServerNameNoProcessing(text);
    }

    /** Locks or unlocks every search criteria. */
    public void lock(boolean lock) {
        comboFilter.setEnabled(lock);
        textSearch.setEnabled(lock);
        treeSelection.setEnabled(lock);
    }

    /** Returns the filter defined for a specific category. */
    public String getFilter() {
        return comboFilter.getSelectedCode();
    }

    /** Defines a filter for a specific category. */
    public void setFilter(String category, String filterstring) {
        comboFilter.reload("filter:" + category);
        comboFilter.setSelectedCode(filterstring);
    }

    /** Assign a name to server name. */
    public void setServerName(String text) {
        synchronized (serverName) {
            serverName.setText(" " + text);
        }
    }
	
	/** Sets a color for the server name. */
	public void setServerColor(Color color) {
		serverName.setForeground(color);
	}

    /** Sets the focus on search box. */
    public void setFocus() {
        requestFocusInWindow();
        textSearch.requestFocusInWindow();
    }

    /** Returns the search key string. */
    public String getSearchKey() {
        return searchKey != null ? searchKey.getText() : "";
    }

    /**
     * Manages one item attached in the displayed search tree.
     */
    private class TaskTreeNode {

        /** Category. */
        String category;

        /** Object ID. */
        private String iid;

        /** Name. */
        String name;

        /** Summary. */
        String summary;

        /** Tip displayed in the list. */
        private String tip;

        /** Constructor. */
        TaskTreeNode(String category, String iid, String name, String summary, 
                     String tip) {
            this.category = category;
            this.iid = (iid == null) ? "" : iid;
            this.name = (name == null) ? "" : name;
            this.summary = 
                    transformDisplayString(summary == null ? "" : summary);
            this.tip = transformDisplayString(tip == null ? "" : tip);
        }

        /** Creates summary string. */
        private String transformDisplayString(String summary) {
            String out = summary;
            out = XMLWrapper.unwrapEmbeddedDate(out);

            if (out.indexOf("@label:inactive") > 0)
                out = "<html><font color=gray>" + out + "</font></html>";
            else if (out.indexOf("@label:green") > 0)
                out = "<html><font color=green>" + out + "</font></html>";
            else if (out.indexOf("@label:orange") > 0)
                out = "<html><font color=#ff7f00>" + out + "</font></html>";
            else if (out.indexOf("@label:red") > 0)
                out = "<html><font color=red>" + out + "</font></html>";
            else if (out.indexOf("@label:underline") > 0)
                out = "<html><u>" + out + "</u></html>";
            else if (out.indexOf("@label:bold") > 0)
                out = "<html><font color=black><b>" + out + "</b></font></html>";
            else
                out = "<html><font color=black>" + out + "</font></html>";

            out = XMLWrapper.replaceAll(out, "@label:plan", LocalMessage.get("label:plan"));
            out = XMLWrapper.replaceAll(out, "@label:forecast", LocalMessage.get("label:forecast"));
            out = XMLWrapper.replaceAll(out, "@label:status", LocalMessage.get("label:status"));
            out = XMLWrapper.replaceAll(out, "@label:tracking", LocalMessage.get("label:tracking"));

            out = XMLWrapper.replaceAll(out, "@label:inactive", "");
            out = XMLWrapper.replaceAll(out, "@label:green", "");
            out = XMLWrapper.replaceAll(out, "@label:orange", "");
            out = XMLWrapper.replaceAll(out, "@label:red", "");
            out = XMLWrapper.replaceAll(out, "@label:bold", "");
            out = XMLWrapper.replaceAll(out, "@label:underline", "");
            return out;
        }

        /** Returns the item displayed string. */
        public String toString() {
            return summary;
        }

        /** Compares this item to the specified object. */
        public boolean equals(Object object) {
            if (object == this)
                return true;

            if (object.getClass() != this.getClass())
                return false;

            TaskTreeNode node = (TaskTreeNode)object;

            return node.category.equals(category) && node.iid.equals(iid) && 
                node.name.equals(name);
        }
    }

    /**
     * Keyboard adapter defined for the lookup panel.
     */
    private class ActionSearch extends KeyAdapter {

        /** Manages actions on the keyboard. */
        public void keyReleased(KeyEvent e) {
            if (!e.isActionKey() && e.getKeyCode() != KeyEvent.VK_SHIFT && 
                e.getKeyCode() != KeyEvent.VK_CONTROL && 
                e.getKeyCode() != KeyEvent.VK_ALT && 
                e.getKeyCode() != KeyEvent.VK_ALT_GRAPH) {
                String search = textSearch.getText();
                if (!search.equals(lastSearch)) {
                    runLookup();
                    lastSearch = search;
                }
            }
        }
    }

    /**
     *  Tree renderer used in order to display appropriate icons and tips.
     */
    class TreeRenderer extends DefaultTreeCellRenderer {
        /** Configures the renderer based on the passed in components. */
        public Component getTreeCellRendererComponent(JTree tree, Object value, 
                                                      boolean sel, 
                                                      boolean expanded, 
                                                      boolean leaf, int row, 
                                                      boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, 
                                               leaf, row, hasFocus);
            if (value != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
                TaskTreeNode selection = (TaskTreeNode)(node.getUserObject());
                if (selection != null) {
                    ImageIcon icon = LocalIcon.get(selection.category + ".gif");
                    setIcon(icon);
                    setDisabledIcon(icon);
                    setToolTipText(selection.tip);
                    return this;
                }
            }
            setToolTipText(null);
            setIcon(null);
            setDisabledIcon(null);
            return this;
        }
    }
}
