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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/PanelCalendar.java,v $
$Revision: 1.8 $
$Date: 2007/02/15 13:51:43 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.projectfactory.client.components.EditBoxes.EditBoxDate;
import net.sourceforge.projectfactory.client.components.TableBoxes.TableBoxLookup;
import net.sourceforge.projectfactory.client.components.buttons.Button;
import net.sourceforge.projectfactory.client.components.buttons.ButtonFactory;
import net.sourceforge.projectfactory.client.components.buttons.ButtonRemoveLevel;
import net.sourceforge.projectfactory.client.components.buttons.ButtonUp;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Panel dedicated for the display of a calendar.
 * @author David Lambert
 */
public class PanelCalendar extends PanelLookup implements ActionListener {

    /** Dropdown list used for year selection. */
    private JComboBox comboYear = new JComboBox();

    /** Dropdown list used for month selection. */
    private JComboBox comboMonth = new JComboBox();

    /** Root node of the calendar tree. */
    private DefaultMutableTreeNode selectionRoot = new DefaultMutableTreeNode();
    
    /** Tree model. */
    private DefaultTreeModel selectionTreeModel = new DefaultTreeModel(selectionRoot);
    
    /** Selection path. */
    private TreePath selectionPath;
    
    /** Tree used to display the calendar. */
    private JTree treeSelection = new JTree(selectionTreeModel);
    
    /** Editbox the calendar is attached to .*/
    private EditBoxDate editboxdate;

    /** Table the calendar is attached to .*/
    private TableBoxLookup tableboxlookup;
    
    /** List of messages. */
    private List<MessageDayTreeNode> messages = new ArrayList(5);
    
    /** US-style calendar. */
    private CalendarTable monthTable = new CalendarTable();

    /** Button for next period display. */    
    private Button buttonNextPeriod = ButtonFactory.createAddLevelButton();

    /** Button for previous period display. */    
    private Button buttonPreviousPeriod = ButtonFactory.createRemoveLevelButton();

    /** Button for current period display. */    
    private Button buttonCurrentPeriod = ButtonFactory.createUpButton();
    
    /** Constructor. */
    public PanelCalendar(){
    }
    
    /** Constructor when the calendar is attached to an editbox. */
    public PanelCalendar(EditBoxDate editboxdate) {
        this.editboxdate = editboxdate;
    }

    /** Constructor when the calendar is attached to a tablebox. */
    public PanelCalendar(TableBoxLookup tableboxlookup) {
        this.tableboxlookup = tableboxlookup;
    }

    /** Initialization (UI). */
    public void init() throws Exception {
        setLayout(new BorderLayout());

        if ((editboxdate != null) || (tableboxlookup != null)) {
            setBorder(new TitledBorder(""));
        }

        setSize(new Dimension(50, 70));
        setMinimumSize(new Dimension(150, 100));
        JPanel panelSelection = new JPanel();
        panelSelection.setLayout(new GridBagLayout());

        Calendar rightNow = Calendar.getInstance();

        for (int i = -10; i <= 15; i++)
            comboYear.addItem("" + (i + rightNow.get(Calendar.YEAR)));

        for (int i = 1; i <= 12; i++)
            comboMonth.addItem(LocalMessage.get("label:month:" + 
                                                ((i < 10) ? "0" : "") + i));

        panelSelection.add(comboYear, 
                           new GridBagConstraints(1, 1, 10, 1, 10.0, 1.0, 
                                                  GridBagConstraints.CENTER, 
                                                  GridBagConstraints.HORIZONTAL, 
                                                  new Insets(0, 0, 0, 0), 0, 
                                                  0));
        panelSelection.add(comboMonth, 
                           new GridBagConstraints(1, 2, 10, 1, 10.0, 1.0, 
                                                  GridBagConstraints.CENTER, 
                                                  GridBagConstraints.HORIZONTAL, 
                                                  new Insets(0, 0, 0, 0), 0, 
                                                  0));

        buttonPreviousPeriod.addActionListener(this);
        buttonCurrentPeriod.addActionListener(this);
        buttonNextPeriod.addActionListener(this);
        comboYear.addActionListener(this);
        comboMonth.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPreviousPeriod.setIcon(LocalIcon.get("arrow_left.gif"));
        buttonNextPeriod.setIcon(LocalIcon.get("arrow_right.gif"));
        buttonPanel.add(buttonPreviousPeriod);
        buttonPanel.add(buttonCurrentPeriod);
        buttonPanel.add(buttonNextPeriod);
        panelSelection.add(buttonPanel, 
                           new GridBagConstraints(1, 3, 10, 1, 1.0, 1.0, 
                                                  GridBagConstraints.CENTER, 
                                                  GridBagConstraints.HORIZONTAL, 
                                                  new Insets(0, 0, 0, 0), 0, 
                                                  0));

        treeSelection.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeSelection.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
                    public void valueChanged(TreeSelectionEvent e) {
                        treeSelectionChanged(e);
                    }
                });

        treeSelection.setAutoscrolls(true);
        treeSelection.setRootVisible(false);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(new Arrow(true, 0));
        renderer.setOpenIcon(new Arrow(true, 0));
        renderer.setClosedIcon(new Arrow(true, 0));
        treeSelection.setCellRenderer(renderer);
        treeSelection.setAutoscrolls(true);
        treeSelection.setRootVisible(false);

        JScrollPane scrollListSelection = new JScrollPane();
        scrollListSelection.setViewportView(treeSelection);
        scrollListSelection.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollListSelection.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollListSelection.setAutoscrolls(true);

        add(panelSelection, BorderLayout.WEST);

        JScrollPane scroll = new JScrollPane();
        scroll.getViewport().add(monthTable, null);
        scroll.setOpaque(true);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        monthTable.setNoSort();

        for (int i = 0; i < 7; i++) {
            monthTable.setIntegerType(i);
            monthTable.setReadOnly(i);
        }

        JSplitPane splitVertical = 
            new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, 
                           scrollListSelection, scroll);
        splitVertical.setDividerLocation(((editboxdate != null) || 
                                          (tableboxlookup != null)) ? 150 : 
                                         320);
        splitVertical.setBorder(null);

        add(splitVertical, BorderLayout.CENTER);
        now();
    }

    /** Manages clicks and selection changes in the calendar. */
    private void treeSelectionChanged(TreeSelectionEvent e) {
        selectionPath = treeSelection.getSelectionPath();

        if (selectionPath != null) {
            DefaultMutableTreeNode selectionNode = 
                    (DefaultMutableTreeNode) selectionPath.getLastPathComponent();

            DayTreeNode selection = 
                (DayTreeNode)(selectionNode.getUserObject());

            if (editboxdate != null) {
                editboxdate.setText(selection.dateString);
            } else if (tableboxlookup != null) {
                tableboxlookup.setValue(selection.dateString);
            }
        }
    }

    /** Displays or redisplays the calendar based on loaded messages. */
    public void reloadDays() {
        if (editboxdate != null) {
            if (!editboxdate.isActivated()) 
                return;
        }

        if (tableboxlookup != null) {
            if (!tableboxlookup.isActivated()) 
                return;
        }

        synchronized (selectionRoot) {
            selectionRoot.removeAllChildren();
            monthTable.clean();

            int year = 0;

            try {
				if(comboYear.getSelectedItem() != null)
					year = Integer.parseInt((String)comboYear.getSelectedItem());
            } finally {
                int month = comboMonth.getSelectedIndex();
                int column = 0;
                int lastColumn = 0;
                monthTable.newRow();

                for (int i = 1; i <= 31; i++) {
                    Calendar rightNow = Calendar.getInstance();
                    rightNow.clear();
                    rightNow.set(year, month, i, 0, 0, 0);

                    if (rightNow.get(Calendar.MONTH) == month) {
                        DefaultMutableTreeNode selectionTree = 
                            new DefaultMutableTreeNode(
                                    new DayTreeNode(rightNow, i));
                        selectionRoot.add(selectionTree);

                        switch (rightNow.get(Calendar.DAY_OF_WEEK)) {
                        case Calendar.MONDAY:
                            column = 0;

                            break;

                        case Calendar.TUESDAY:
                            column = 1;

                            break;

                        case Calendar.WEDNESDAY:
                            column = 2;

                            break;

                        case Calendar.THURSDAY:
                            column = 3;

                            break;

                        case Calendar.FRIDAY:
                            column = 4;

                            break;

                        case Calendar.SATURDAY:
                            column = 5;

                            break;

                        case Calendar.SUNDAY:
                            column = 6;

                            break;
                        }

                        if (lastColumn == 6) 
                            monthTable.newRow();

                        lastColumn = column;

                        // Look into messages
                        monthTable.setHighlightAt(false, column);

                        boolean found = false;

                        synchronized(messages) {
                            for (MessageDayTreeNode message: messages) {
                                if (message.date.get(Calendar.YEAR) == year && 
                                    message.date.get(Calendar.MONTH) == month && 
                                    message.date.get(Calendar.DAY_OF_MONTH) == i) {
                                    selectionTree.add(new DefaultMutableTreeNode(
                                        new DayTreeNode(rightNow, i, message.message)));
                                    found = true;
                                }
                            }
                        }

                        if (found) 
                            monthTable.setHighlightAt(true, column);

                        monthTable.setValueAt("" + i, column);
                    } else {
                        break;
                    }
                }
            }

            selectionTreeModel.reload();

            for (int i = 0; i < treeSelection.getRowCount(); i++)
                treeSelection.expandRow(i);
        }
    }

    /** Displays current month. */
    public void now() {
        synchronized (selectionRoot) {
            Calendar rightNow = Calendar.getInstance();
            comboYear.setSelectedItem("" + rightNow.get(Calendar.YEAR));
            comboMonth.setSelectedIndex(rightNow.get(Calendar.MONTH));
        }
    }

    /** Triggers a thread for redisplay of the calendar. */
    public void runLookup() {
        synchronized (selectionRoot) {
            new ThreadLoadCalendar();
        }
    }

    /** Displays next month. */
    private void nextMonth() {
        synchronized (selectionRoot) {
            if (comboMonth.getSelectedIndex() < 
                (comboMonth.getItemCount() - 1)) {
                comboMonth.setSelectedIndex(comboMonth.getSelectedIndex() + 1);
            } else {
                if (comboYear.getSelectedIndex() < 
                    (comboYear.getItemCount() - 1)) {
                    comboYear.setSelectedIndex(comboYear.getSelectedIndex() + 
                                               1);
                }

                comboMonth.setSelectedIndex(0);
            }
        }
    }

    /** Displays previous month. */
    private void previousMonth() {
        synchronized (selectionRoot) {
            if (comboMonth.getSelectedIndex() > 0) {
                comboMonth.setSelectedIndex(comboMonth.getSelectedIndex() - 1);
            } else {
                if (comboYear.getSelectedIndex() > 0) {
                    comboYear.setSelectedIndex(comboYear.getSelectedIndex() - 
                                               1);
                }

                comboMonth.setSelectedIndex(11);
            }
        }
    }

    /** Stores a message defined for the calendar. */
    public void addCalendarMessage(Date dateMessage, String message) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dateMessage);
        messages.add(new MessageDayTreeNode(rightNow, message));
    }

    /** Removes all messages used for display of calendar. */
    public void clearCalendarMessages() {
        messages.clear();
    }

    /** Selects and displays a date in the calendar. */
    public void selectItem(Calendar date) {
        DayTreeNode find = new DayTreeNode(date, 0);
        DayTreeNode selection = null;

        for (int i = 0; i < treeSelection.getRowCount(); i++) {
            selectionPath = treeSelection.getPathForRow(i);
            DefaultMutableTreeNode selectionNode = 
                    (DefaultMutableTreeNode)selectionPath.getLastPathComponent();
            selection = (DayTreeNode)(selectionNode.getUserObject());

            if (selection != null) {
                if (selection.equals(find)) {
                    treeSelection.setSelectionPath(selectionPath);
                    treeSelection.scrollPathToVisible(selectionPath);

                    return;
                }
            }
        }
    }

    /** Manages clicks on buttons. */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == buttonNextPeriod) {
            nextMonth();
        } else if (source == buttonCurrentPeriod) {
            now();
        } else if (source == buttonPreviousPeriod) {
            previousMonth();
        } else if (source == comboYear || source == comboMonth) {
            new ThreadLoadCalendar();
        }
    }

    /**
     * Represents a table used in order to display a US-style calendar.
     */
    private class CalendarTable extends TableBoxLookup {
    
        /** Constructor. */
        public CalendarTable() {
            super("", "label:monday", 0, 
                                      "", "label:tuesday", 0, 
                                      "", "label:wednesday", 0, 
                                      "", "label:thursday", 0, 
                                      "", "label:friday", 0, 
                                      "", "label:saturday", 0, 
                                      "", "label:sunday", 0);                             
        }

        /** Manages selection change in the table. */
        public void changeSelection() {
            super.changeSelection();

            int year = 0;
            int day = 0;

            try {
                year = Integer.parseInt((String)comboYear.getSelectedItem());
				Object dayObject = getValueAt(getSelectedRow(), getSelectedColumn());
				day = dayObject != null ? Integer.parseInt(dayObject.toString())
										: 0;
            } finally {
                if (day > 0) {
                    int month = comboMonth.getSelectedIndex();
                    Calendar rightNow = Calendar.getInstance();
                    rightNow.clear();
                    rightNow.set(year, month, day, 0, 0, 0);

                    String dateString = XMLWrapper.dsLocal.format(rightNow.getTime());

                    if (editboxdate != null) 
                        editboxdate.setText(dateString);
                    else if (tableboxlookup != null) 
                        tableboxlookup.setValue(dateString);

                    selectItem(rightNow);
                }
            }
        }
    }

    /**
     * Represents a thread used in order to populate the calendar.
     */
    private class ThreadLoadCalendar implements Runnable {

        /** Constructor. */  
        private ThreadLoadCalendar() {
            Thread runner = new Thread(this);
            runner.start();
        }

        /** Executes the thread. */
        public void run() {
            reloadDays();
        }
    }

    /**
     * Represents an item displayed with the calendar.
     */
    private class DayTreeNode {
    
        /** Displayed summary of the item. */
        String summary;
        
        /** String that represents the date. */
        String dateString;
        
        /** Message associated to the item. */
        String message;
        
        /** Bold indicator. */
        private boolean bold;
        
        /** Italic indicator. */
        private boolean italic;
        
        /** Underline indicator. */
        private boolean underline;
        
        /** Date of the item. */
        private Calendar date;

        /** Constructor. */
        DayTreeNode(Calendar date, int day) {
            int dayWeek = date.get(Calendar.DAY_OF_WEEK);
            this.date = date;
            this.summary = 
                    LocalMessage.get("label:day:" + dayWeek) + " " + day;

            if (dayWeek == Calendar.MONDAY) {
                this.summary = 
                        this.summary + " (" + LocalMessage.get("label:week") + 
                        " " + date.get(Calendar.WEEK_OF_YEAR) + ")";
                this.underline = true;
            }

            if ((dayWeek == Calendar.SATURDAY) || 
                (dayWeek == Calendar.SUNDAY)) {
                this.italic = true;
            }

            if ((date.get(Calendar.YEAR) == 
                 Calendar.getInstance().get(Calendar.YEAR)) && 
                (date.get(Calendar.DAY_OF_YEAR) == 
                 Calendar.getInstance().get(Calendar.DAY_OF_YEAR))) {
                this.summary = 
                        LocalMessage.get("label:today") + " - " + this.summary;
                this.bold = true;
            }

            dateString = XMLWrapper.dsLocal.format(date.getTime());
        }

        /** Constructor. */
        DayTreeNode(Calendar date, int day, String message) {
            this(date, day);
            this.message = message;
            this.underline = false;
            this.bold = false;
        }

        /** Converts the item into a string. */
        public String toString() {
            String retSummary = (message != null) ? message : summary;

            if (bold) {
                retSummary = 
                        "<font color=blue><b>" + retSummary + "</b></font>";
            }

            if (italic) {
                retSummary = 
                        "<font color=gray><i>" + retSummary + "</i></font>";
            }

            if (underline) {
                retSummary = 
                        "<font color=black><u>" + retSummary + "</u></font>";
            }

            return "<html><font color=black>" + retSummary + "</font></html>";
        }

        /** Compares this item to the specified object. */
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }

            return ((DayTreeNode)object).date.equals(date);
        }
    }

    /**
     * Represents messages attached to the calendar.
     */
    private class MessageDayTreeNode {
    
        /** Label of the message. */
        String message;
        
        /** Date of the message. */
        Calendar date;

        /** Constructor. */
        MessageDayTreeNode(Calendar date, String message) {
            this.date = date;
            this.message = message;
        }
    }
}
