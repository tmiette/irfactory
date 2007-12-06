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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/TableBox.java,v $
$Revision: 1.26 $
$Date: 2007/02/22 15:37:59 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.components.tableBoxes;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.sourceforge.projectfactory.client.components.Arrow;
import net.sourceforge.projectfactory.client.components.ComponentEnabler;
import net.sourceforge.projectfactory.client.components.LocalIcon;
import net.sourceforge.projectfactory.client.components.LocalMessage;
import net.sourceforge.projectfactory.client.components.buttons.Button;
import net.sourceforge.projectfactory.client.components.buttons.ButtonAdd;
import net.sourceforge.projectfactory.client.components.buttons.ButtonAddLevel;
import net.sourceforge.projectfactory.client.components.buttons.ButtonDown;
import net.sourceforge.projectfactory.client.components.buttons.ButtonFactory;
import net.sourceforge.projectfactory.client.components.buttons.ButtonRemove;
import net.sourceforge.projectfactory.client.components.buttons.ButtonRemoveLevel;
import net.sourceforge.projectfactory.client.components.buttons.ButtonUp;
import net.sourceforge.projectfactory.client.components.comboBoxes.ComboBoxCode;
import net.sourceforge.projectfactory.client.components.comboBoxes.ComboItem;
import net.sourceforge.projectfactory.client.panels.PanelData;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Default table in the application.
 * @author David Lambert
 */
public class TableBox extends JTable implements ComponentEnabler, 
                                                ActionListener {

    /** String type. */
    protected static final int TYPE_STRING = 0;

    /** Integer type. */
    protected static final int TYPE_INTEGER = 1;

    /** Boolean type. */
    protected static final int TYPE_BOOLEAN = 2;

    /** Date type. */
    protected static final int TYPE_DATE = 3;

    /** Percentage type. */
    protected static final int TYPE_PERCENT = 4;

    /** Action type. */
    protected static final int TYPE_ACTION = 5;

    /** Descending status used for sorting. */
    protected static final int DESCENDING = -1;

    /** No status used for sorting. */
    protected static final int NOT_SORTED = 0;

    /** Ascending status used for sorting. */
    protected static final int ASCENDING = 1;

    /** Empty icon. */
    protected static final Arrow nullArrow = new Arrow(true, 0);

    /** Component can be enabled. */
    private boolean enabler = true;

    /** Component must be saved. */
    private boolean mustSave = false;

    /** Component title. */
    private String title = "";

    /** Indicates if the table is activated or not. */
    private boolean activated;

    /** Table model that represents the content of table. */
    private PanelTableModel tableModel;

    /** Number of columns in the table. */
    private int nbcols;

    /** Navigation panel. */
    protected JPanel buttonPanel = new JPanel();

    /**  Add row button. */
    private Button buttonAdd = ButtonFactory.createAddButton();

    /** Remove row button. */
    private Button buttonRemove = ButtonFactory.createRemoveButton();

    /** Move up button. */
    private Button buttonUp = ButtonFactory.createUpButton();

    /** Move down button. */
    private Button buttonDown = ButtonFactory.createDownButton();

    /** Ident button. */
    private Button buttonAddLevel = ButtonFactory.createAddLevelButton();

    /** Outdent button. */
    private Button buttonRemoveLevel = ButtonFactory.createRemoveLevelButton();

    /** Indicates the table can't be sorted. */
    private boolean noSort;

    /** Columns size. */
    private int[] sizes;

    /** Border to be displayed when a cell is selected. */
    private final Border selectedBorder = 
        BorderFactory.createMatteBorder(2, 2, 2, 2, getSelectionBackground());

    /** Border to be displayed when a cell is not selected. */
    private final Border unselectedBorder = 
        BorderFactory.createMatteBorder(1, 1, 1, 1, getBackground());
		
	/** Panel data in which the table is attached. */ 
	private PanelData panel;

    /** Constructor. Default. */
    public TableBox() {
        super();
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowSelectionAllowed(false);
    }

    /** Constructor. */
    public TableBox(Object... definition) {
        this();
        create(definition.length / 3);
        for(int i=0 ; i<nbcols ; i++) {
            setColumnName((String) definition[3*i+1], i);
            setTagName((String) definition[3*i], i);
        }
        init();
        for(int i=0 ; i<nbcols ; i++) {
            setColumnSize(((Integer) definition[3*i+2]).intValue(), i);
        }
        sizes();
    }

    /** Defines if the component can be enabled or not. */
    public void setEnabler(boolean enabler) {
        this.enabler = enabler;
    }

    /** Returns true if the component can be enabled. */
    public boolean isEnabler() {
        return enabler;
    }

    /** Defines if the component must be saved even if it's disabled or not. */
    public void setMustSave(boolean mustSave) {
        this.mustSave = mustSave;
    }

    /** Returns true if the component must be saved even if disabled. */
    public boolean mustSave() {
        return mustSave;
    }

    /** Sets component title. */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Returns component title. */
    public String getTitle() {
        return title;
    }

    /** Defines specified column size. */
    private void setColumnSize(int size, int col) {
        sizes[col] = size;
    }

    /** Sets prefered sizes for all columns. */
    private void sizes() {
        for (int i = 0; i < (nbcols + (isHierarchical() ? 1 : 0)); i++)
            setPreferredWidth(sizes[i], i);
    }

    /** Creates the associated objects based on number of columns. */
    public void create(int nbcols) {
        this.nbcols = nbcols;
        tableModel = new PanelTableModel();
        sizes = new int[nbcols + 2];
        setDefaultRenderer(String.class, new LabelRenderer());
        setDefaultRenderer(Integer.class, new LabelRenderer());
        setDefaultRenderer(ComboItem.class, new LabelRenderer());
        setDefaultRenderer(Boolean.class, new CheckBoxRenderer());
    }

    /** Initializes the user interface. */
    public void init() {
        setModel(tableModel);
        setGridColor(Color.LIGHT_GRAY);
        setShowHorizontalLines(false);
        setShowVerticalLines(true);
        buttonPanel.setLayout(new GridBagLayout());
        buttonAdd.addActionListener(this);
        buttonRemove.addActionListener(this);
        buttonUp.addActionListener(this);
        buttonDown.addActionListener(this);
        buttonPanel.add(buttonAdd, 
                        new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, 
                                               GridBagConstraints.NONE, 
                                               new Insets(0, 0, 0, 0), 0, 0));
        buttonPanel.add(buttonRemove, 
                        new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, 
                                               GridBagConstraints.CENTER, 
                                               GridBagConstraints.NONE, 
                                               new Insets(0, 0, 0, 0), 0, 0));
        buttonPanel.add(buttonUp, 
                        new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, 
                                               GridBagConstraints.NONE, 
                                               new Insets(0, 0, 0, 0), 0, 0));
        buttonPanel.add(buttonDown, 
                        new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, 
                                               GridBagConstraints.CENTER, 
                                               GridBagConstraints.NONE, 
                                               new Insets(0, 0, 0, 0), 0, 0));
        buttonAdd.setFocusable(false);
        buttonRemove.setFocusable(false);
        buttonUp.setFocusable(false);
        buttonDown.setFocusable(false);
        buttonPanel.setFocusable(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        if (!((ListSelectionModel)e.getSource()).isSelectionEmpty()) {
                            changeSelection();
                        }
                    }
                });

        getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        if (!((ListSelectionModel)e.getSource()).isSelectionEmpty()) {
                            changeSelection();
                        }
                    }
                });
    }

    /** Assigns a name to the given column. */
    public void setColumnName(String name, int col) {
        tableModel.setColumnName(LocalMessage.get(name), col);
    }

    /** Assigns an XML tag to the given column. */
    private void setTagName(String tag, int col) {
        tableModel.setTagName(tag, col);
    }

    /** Assigns a prefered or default size to the given column. */
    private void setPreferredWidth(int size, int col) {
        if (size > 0) {
            getColumnModel().getColumn(col).setPreferredWidth(size);
            getColumnModel().getColumn(col).setWidth(size);
        }
    }

    /** Defines a width to the given column. */
    private void setWidth(int size, int col) {
        if (size > 0) {
            getColumnModel().getColumn(col).setMaxWidth(size);
            getColumnModel().getColumn(col).setWidth(size);
            getColumnModel().getColumn(col).setResizable(false);
        }
    }

    /** Inserts a new row at the end with defaults. */
    public void newRow() {
        tableModel.newRow(true);
    }

    /** Inserts a new row at the end with defaults or not. */
    protected void newRow(boolean defaults) {
        tableModel.newRow(defaults);
        revalidate();
    }

    /** Inserts a new row at the end or after the selected row. */
    private void newRowCurrent() {
        if (getRowCount() == 0) {
            tableModel.newRow(true);
            addRowSelectionInterval(0, 0);
            addColumnSelectionInterval(0, 0);
        } else {
            int row = getSelectedRow();
            int column = getSelectedColumn();

            if (row < 0)
                row = getRowCount() - 1;

            if (row > getRowCount() - 1)
                row = getRowCount() - 1;

            if (column < 0)
                column = 0;

            tableModel.newRow(row, true);
            addRowSelectionInterval(row + 1, row + 1);
            addColumnSelectionInterval(column, column);
        }

        revalidate();
    }

    /** Deletes selected row. */
    protected void deleteRow() {
        int row = getSelectedRow();
        tableModel.deleteRow(row);
        revalidate();
    }

    /** Deletes all table rows. */
    private void deleteAllRows() {
        tableModel.deleteAllRows();
        revalidate();
    }

    /** Assigns a value at given row and column. */
    public void setValueAt(Object value, int row, int col) {
        tableModel.setValueAt(value, row, col);
    }

    /** Assigns a value at given column and current row. */
    public void setValueAt(Object value, int col) {
        tableModel.setValueAt(value, col);
    }

    /** Highlights the given row and column. */
    public void setHighlightAt(boolean value, int row, int col) {
        tableModel.setHighlightAt(value, row, col);
    }

    /** Highlights the given column and current row. */
    public void setHighlightAt(boolean value, int col) {
        tableModel.setHighlightAt(value, col);
    }

    /** Returns the navigation panel. */
    public JPanel getButtonPanel() {
        return buttonPanel;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, String tagElement) {
        if (getCellEditor() != null)
            getCellEditor().stopCellEditing();
        tableModel.xmlOut(xml, tagElement);
    }

    /** Reads the object from an XML input. */
    public void xmlIn(String tag, String value) {
        tableModel.xmlIn(tag, value);
    }

    /** Converts the table content into HTML. */
    public String toHtml() throws IOException {
        return tableModel.toHtml();
    }

    /** Assigns a string value at selected row and column. */
    public void setValue(String value) {
        int row = getSelectedRow();
        int column = getSelectedColumn();
        setValueAt(value, row, column);
    }

    /** Cleans the table. */
    public void clean() {
        int row = getSelectedRow();
        int column = getSelectedColumn();

        if (getRowCount() > 0) {
            if ((row >= 0) && (row < getRowCount())) {
                removeRowSelectionInterval(row, row);
            }
        }

        if (getColumnCount() > 0) {
            if ((column >= 0) && (column < getColumnCount())) {
                removeColumnSelectionInterval(column, column);
            }
        }

        deleteAllRows();
    }

    /** Defines the column as a boolean. */
    public void setBooleanType(int column) {
        if (isHierarchical())
            ++column;

        tableModel.setColumnType(TYPE_BOOLEAN, column);
    }

    /** Defines the column as an action. */
    public void setActionType(int column, PanelData panel) {
		this.panel = panel;
        if (isHierarchical())
            ++column;

        tableModel.setColumnType(TYPE_ACTION, column);
    }

    /** Defines the column as a date. */
    public void setDateType(int column) {
        if (isHierarchical())
            ++column;

        tableModel.setColumnType(TYPE_DATE, column);
    }

    /** Defines the column as an integer. */
    public void setIntegerType(int column) {
        if (isHierarchical())
            ++column;

        tableModel.setColumnType(TYPE_INTEGER, column);
    }

    /** Defines the column as a percent. */
    public void setPercentType(int column) {
        if (isHierarchical())
            ++column;

        tableModel.setColumnType(TYPE_PERCENT, column);
    }

    /** Attachs to the column a combobox. */
    public void setCombo(int column, ComboBoxCode combo) {
        if (isHierarchical())
            ++column;

        if (column < getColumnCount()) {
            tableModel.setCombo(combo, column);
            getColumnModel().getColumn(column).setCellEditor(new DefaultCellEditor(combo));
        }
    }

    /** Defines a default value for the column. */
    public void setColumnDefault(String value, int column) {
        if (isHierarchical())
            ++column;

        if (column < getColumnCount())
            tableModel.setColumnDefault(value, column);
    }

    /** Defines the column as a reference in order to highlight the row. */
    public void setHighlight(int column) {
        setBooleanType(column);
        
        if (isHierarchical())
            ++column;

        tableModel.setHighlight(column);
    }

    /** Defines the column as read-only. */
    public void setReadOnly(int column) {
        if (isHierarchical())
            ++column;

        tableModel.setReadOnly(column);
    }

    /** Defines the column as centered. */
    public void setCentered(int column) {
        if (isHierarchical())
            ++column;

        tableModel.setCentered(column);
    }

    /** Sorts the table. */
    public void sort() {
        if (!noSort)
            tableModel.sort();
    }

    /** Indicates if the table is hierarchical. */
    public boolean isHierarchical() {
        return tableModel.isHierarchical();
    }

    /** Defines the table as hierarchical. */
    public void setHierarchical() {
        tableModel.setHierarchical();
        buttonAddLevel.addActionListener(this);
        buttonRemoveLevel.addActionListener(this);
        buttonPanel.add(buttonRemoveLevel, 
                        new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, 
                                               GridBagConstraints.CENTER, 
                                               GridBagConstraints.NONE, 
                                               new Insets(0, 0, 0, 0), 0, 0));
        buttonPanel.add(buttonAddLevel, 
                        new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, 
                                               GridBagConstraints.CENTER, 
                                               GridBagConstraints.NONE, 
                                               new Insets(0, 0, 0, 0), 0, 0));

        buttonAddLevel.setFocusable(false);
        buttonRemoveLevel.setFocusable(false);

        for (int i = nbcols - 1; i >= 0; i--)
            sizes[i + 1] = sizes[i];

        sizes();
    }

    /** Moves the selected row one level up. */
    private void addLevel() {
        if (getRowCount() != 0) {
            int row = getSelectedRow();

            if (row < 0)
                return;

            tableModel.addLevel(row);
        }
    }

    /** Moves the selected row one level down. */
    private void removeLevel() {
        if (getRowCount() != 0) {
            int row = getSelectedRow();

            if (row < 0)
                return;

            tableModel.removeLevel(row);
        }
    }

    /** Moves the selected row up. */
    private void up() {
        if (getRowCount() != 0) {
            int row = getSelectedRow();

            if (row <= 0) {
                return;
            }

            tableModel.up(row);
            setRowSelectionInterval(row - 1, row - 1);
        }
    }

    /** Moves the selected row down. */
    private void down() {
        if (getRowCount() != 0) {
            int row = getSelectedRow();

            if ((row < 0) || (row >= (getRowCount() - 1))) {
                return;
            }

            tableModel.down(row);
            setRowSelectionInterval(row + 1, row + 1);
        }
    }

    /** Defines if the table can be enabled. */
    public void setSoftEnabled(boolean enabled) {
        tableModel.setSoftEnabled(enabled);
    }

    /** Returns the icon used as sort indicator of given column. */
    private Icon getHeaderRendererIcon(int column) {
        int direction = tableModel.getDirective(column);

        return (direction == NOT_SORTED) ? null : 
               ((direction == DESCENDING) ? LocalIcon.get("descending.gif") : 
                LocalIcon.get("ascending.gif"));
    }

    /** Indicates if the table is activated. */
    public boolean isActivated() {
        return activated;
    }

    /** Activates the table. */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    /** Hides navigation buttons. */
    public void hideButtons() {
        buttonAdd.setVisible(false);
        buttonRemove.setVisible(false);
        buttonUp.setVisible(false);
        buttonDown.setVisible(false);
        buttonAddLevel.setVisible(false);
        buttonRemoveLevel.setVisible(false);
    }

    /** Defines the column to be duplicated after an insert ot a new row. */
    public void setDupMode(int col) {
        if (isHierarchical())
            ++col;

        tableModel.setDupMode(col);
    }

    /** Generates as list when the table is used as lookup .*/
    public void generateList(WriterXML output, String classname, 
                             String search) throws IOException {
        tableModel.generateList(output, classname, search);
    }

    /** Forces the table to be not sortable. */
    public void setNoSort() {
        noSort = true;
    }

    /** Manages selection changes in the table.
	  * Does nothing by default by may be overriden by sub classes. */
    public void changeSelection() {
    }

    /** Manages actions on navigation buttons. */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (getCellEditor() != null)
            getCellEditor().stopCellEditing();

        if (source instanceof ButtonAdd) {
            newRowCurrent();
            requestFocusInWindow();
        } else if (source instanceof ButtonRemove) {
            deleteRow();
            requestFocusInWindow();
        } else if (source instanceof ButtonAddLevel) {
            addLevel();
            requestFocusInWindow();
        } else if (source instanceof ButtonRemoveLevel) {
            removeLevel();
            requestFocusInWindow();
        } else if (source instanceof ButtonUp) {
            up();
            requestFocusInWindow();
        } else if (source instanceof ButtonDown) {
            down();
            requestFocusInWindow();
        }
    }
    
    /** Copies the value of one column
     *  to another table using a reference column identified
     *  in the two tables. */
    public void copyColumnsValue(TableBox other, int columnFrom, 
                                 int referenceFrom, int columnTo, 
                                 int referenceTo) {
        for (int row1 = 0; row1 < other.getRowCount(); row1++) {
            String referenceValueFrom = 
                (String)other.getValueAt(row1, referenceFrom);
            for (int row2 = 0; row2 < getRowCount(); row2++) {
                String referenceValueTo = 
                    (String)getValueAt(row2, referenceTo);
                if (referenceValueFrom.equals(referenceValueTo)) {
                    other.setValueAt(getValueAt(row2, columnFrom), row1, 
                                     columnTo);
                }
            }
        }
    }

    /** Sets the value of one column used for highlighting to true
     *  to another table using a reference column identified
     *  in the two tables. */
    public void copyHighlightColumns(TableBox other, int referenceFrom, 
                                     int columnTo, int referenceTo) {
        for (int row1 = 0; row1 < other.getRowCount(); row1++) {
            String referenceValueFrom = 
                (String)other.getValueAt(row1, referenceFrom);
            for (int row2 = 0; row2 < getRowCount(); row2++) {
                String referenceValueTo = 
                    (String)getValueAt(row2, referenceTo);
                if (referenceValueFrom.equals(referenceValueTo)) {
                    other.setValueAt(Boolean.valueOf(true), row1, columnTo);
                }
            }
        }
    }

    /**
	 * Represents the content of the table and manages underlying data.
	 */
    private class PanelTableModel extends AbstractTableModel {

        /** Columns names. */
        private String[] columnName;

        /** Default values for columns. */
        private String[] columnDefault;

        /** Columns types. */
        private int[] columnType;

        /** Columns sort indicator. */
        private int[] columnSort;

        /** Columns readonly indicators. */
        private boolean[] readOnly;

        /** Columns center indicators. */
        private boolean[] centered;

        /** Duplicate column. */
        private boolean[] duplicate;

        /** Columns associated combboboxes. */
        private ComboBoxCode[] combo;

        /** Columns associated XML tags. */
        private String[] tags;

        /** Stored data. */
        private List<TableCell> data = new ArrayList(50);

        /** Current table row. */
        private int currentRow;

        /** Table header. */
        private JTableHeader header;

        /** Mouse listener. */
        private MouseListener mouseListener;

        /** Indicates the table is hierarchical. */
        private boolean isHierarchical;

        /** Indicates the table can be enabled. */
        private boolean softEnabled = true;

        /** Column used to hightlight a row. */
        private int highlightColumn = -1;

        /** Constructor. */
        public PanelTableModel() {
            columnName = new String[nbcols + 2];
            columnType = new int[nbcols + 2];
            columnDefault = new String[nbcols + 2];
            columnSort = new int[nbcols + 2];
            combo = new ComboBoxCode[nbcols + 2];
            tags = new String[nbcols + 2];
            readOnly = new boolean[nbcols + 2];
            centered = new boolean[nbcols + 2];
            duplicate = new boolean[nbcols + 2];

            for (int i = 0; i < nbcols; i++) {
                columnName[i] = new String();
                columnType[i] = TYPE_STRING;
                columnSort[i] = NOT_SORTED;
                tags[i] = new String();
                readOnly[i] = false;
                centered[i] = false;
            }

            this.mouseListener = new MouseHandler();
            setTableHeader(getTableHeader());
        }

        /** Assigns a table header and renderer. */
        public void setTableHeader(JTableHeader tableHeader) {
            if (this.header != null) {
                this.header.removeMouseListener(mouseListener);

                TableCellRenderer defaultRenderer = 
                    this.header.getDefaultRenderer();

                if (defaultRenderer instanceof SortableHeaderRenderer) {
                    this.header.setDefaultRenderer(
						((SortableHeaderRenderer)defaultRenderer).tableCellRenderer);
                }
            }

            this.header = tableHeader;

            if (this.header != null) {
                this.header.addMouseListener(mouseListener);
                this.header.setDefaultRenderer(
					new SortableHeaderRenderer(this.header.getDefaultRenderer()));
            }
        }

        /** Returns the number of columns in the table. */
        public int getColumnCount() {
            return nbcols;
        }

        /** Returns the number of rows in the table. */
        public int getRowCount() {
            return data.size();
        }

        /** Creates a new row after given row number with defaults or not. */
        private void newRow(int row, boolean defaults) {
            if (row > (data.size() - 1))
                row = data.size() - 1;

            TableCell cell = new TableCell(this);

            for (int col = 0; col < nbcols; col++) {
                String defaultValue = "";
                int type = columnType[col];

                if (defaults && row >= 0 && duplicate[col]) {
                    Object value = getValueAt(row, col);
                    defaultValue = value != null ? value.toString() : "";
                    if (combo[col] != null)
                        defaultValue = combo[col].getItemCode(defaultValue);
                } else if (defaults && (columnDefault[col] != null))
                    defaultValue = columnDefault[col];

                if (type == TYPE_BOOLEAN) {
                    if (defaults && defaultValue.equals("y"))
                        cell.columns.add(Boolean.valueOf(true));
                    else
                        cell.columns.add(Boolean.valueOf(col == 0 && 
                                                         isHierarchical()));
                } else if (type == TYPE_ACTION) {
                    cell.columns.add(Boolean.valueOf(false));
                } else if (type == TYPE_INTEGER || type == TYPE_PERCENT) {
                    cell.columns.add((defaultValue.length() > 0) ? 
                                     new Integer(defaultValue) : 
                                     new Integer(0));
                } else {
                    if (combo[col] != null)
                        cell.columns.add(
							new ComboItem(combo[col].getItem(defaultValue)));
                    else
                        cell.columns.add(
							new String(defaultValue));
                }

                cell.highlight.add(Boolean.valueOf(false));
            }

            data.add(row + 1, cell);
            currentRow = row + 1;
        }

        /** Creates a new row at the end with defaults or not. */
        private void newRow(boolean defaults) {
            newRow(data.size() - 1, defaults);
        }

        /** Deletes specified row. */
        private void deleteRow(int row) {
            if (row < 0 || row > (data.size() - 1))
                return;

            data.remove(row);
        }

        /** Deletes all rows in the table. */
        private void deleteAllRows() {
            while (data.size() > 0)
                data.remove(0);
        }

        /** Defines a name to given column. */
        public void setColumnName(String name, int col) {
            if (col < 0 || col > nbcols - 1)
                return;

            columnName[col] = name;
        }

        /** Returns column name. */
        public String getColumnName(int col) {
            if (col < 0 || col > nbcols - 1)
                return "";

            return columnName[col].toString();
        }

        /** Defines an XML tag to given column. */
        public void setTagName(String tag, int col) {
            if (col < 0 || col > nbcols - 1)
                return;

            tags[col] = tag;
        }

        /** Returns column type. */
        public int getColumnType(int col) {
            if (col < 0 || col > nbcols - 1)
                return TYPE_STRING;

            return columnType[col];
        }

        /** Defines type to given column. */
        public void setColumnType(int type, int col) {
            if (col < 0 || col > nbcols - 1)
                return;

            columnType[col] = type;
        }

        /** Defines default value to given column. */
        public void setColumnDefault(String value, int col) {
            if (col < 0 || col > nbcols - 1)
                return;

            columnDefault[col] = value;
        }

        /** Defines given column as hightlight. */
        public void setHighlight(int col) {
            if (col < 0 || col > nbcols - 1)
                return;

            highlightColumn = col;
        }

        /** Defines given column as read only. */
        public void setReadOnly(int col) {
            if (col < 0 || col > nbcols - 1)
                return;

            readOnly[col] = true;
        }

        /** Defines given column as centered. */
        public void setCentered(int col) {
            if (col < 0 || col > nbcols - 1)
                return;

            centered[col] = true;
        }

        /** Assigns a combobox to given column. */
        public void setCombo(ComboBoxCode combo, int col) {
            if (col < 0 || col > nbcols - 1)
                return;

            this.combo[col] = combo;
        }

        /** Defines default value to given column. */
        public void setDupMode(int col) {
            if (col < 0 || col > nbcols - 1)
                return;

            duplicate[col] = true;
        }

        /** Returns value at given row and column. */
        public Object getValueAt(int row, int col) {
            if (row < 0 || col < 0 || row > (data.size() - 1) || 
                col > nbcols - 1)
                return null;

            return data.get(row).columns.get(col);
        }

        /** Returns highlight indicator at gieven row and column. */
        public boolean getHighlightAt(int row, int col) {
            if (row < 0 || col < 0 || row > (data.size() - 1) || 
                col > nbcols - 1)
                return false;

            return data.get(row).highlight.get(col).booleanValue();
        }

        /** Returns class associated to given column. */
        public Class getColumnClass(int col) {
            return getValueAt(0, col).getClass();
        }

        /** Returns hierarchical level of given row. */
        public int getLevelAt(int row) {
            if (row < 0 || row > (data.size() - 1))
                return -1;

            return data.get(row).getLevel();
        }

        /** Assigns a hierarchical level to given row. */
        public void setLevelAt(int delta, int row) {
            if (row < 0 || row > (data.size() - 1)) {
                return;
            }

            data.get(row).setDeltaLevel(delta);
            if (row > 0)
                fireTableRowsUpdated(row - 1, row);
        }

        /** Indicates if the row is hidden in a hierarchical table. */
        public boolean hasHidden(int row) {
            if (row < 0 || row > (data.size() - 1))
                return false;

            return ((TableCell)data.get(row)).hidden != null;
        }

        /** Indicates if the cell at given row and column is editable. */
        public boolean isCellEditable(int row, int col) {
            if (col > nbcols)
                return false;

            if (isHierarchical()) {
                int level = getLevelAt(row);
                int nextLevel = getLevelAt(row + 1);
                if (col == 0) {
                    return (nextLevel > 0 && level < nextLevel) || hasHidden(row);
                }
                else if(col > 1) {
                    if((nextLevel > 0 && level < nextLevel) || hasHidden(row))
                        return false;
                }
            }
			
            if (columnType[col] == TYPE_ACTION)
                return !softEnabled;

            if (readOnly[col])
                return false;

            return softEnabled;
        }

        /** Assigns a value at given row and column. */
        public void setValueAt(Object value, int row, int col) {
            if (row < 0 || row > (data.size() - 1) || col < 0 || 
                col > nbcols - 1)
                return;

            TableCell cell = data.get(row);
            int type = columnType[col];

            // Manages hide and unhide of rows on a hierarchical table.
            if (col == 0 && isHierarchical() && value instanceof Boolean) {
                boolean flag = ((Boolean)value).booleanValue();

                if (!flag) {
                    cell.hidden = new ArrayList();

                    int rowmax = 0;
                    for (int i = row + 1; i < data.size(); i++) {
                        TableCell secondcell = data.get(i);
                        if (secondcell.level <= cell.level) 
                            break;
                        rowmax = i;
                        cell.hidden.add(secondcell);
                    }

                    if (rowmax > 0) {
                        for (int i = 0; i < cell.hidden.size(); i++)
                            data.remove(cell.hidden.get(i));
                        fireTableRowsDeleted(row + 1, rowmax);
                    }
                } else if (cell.hidden != null) {
                    int rowmax = row + 1;
                    for (int i = 0; i < cell.hidden.size(); i++)
                        data.add(rowmax++, cell.hidden.get(i));
                    cell.hidden = null;
                    fireTableRowsInserted(row + 1, rowmax);
                }
            }

            if (value instanceof Boolean && type == TYPE_ACTION && panel != null) {
                boolean flag = ((Boolean)value).booleanValue();
				if(flag) 
					panel.createAction(getValueAt(row, isHierarchical() ? 1 : 0).toString());
			}

            if (value == null) {
                if (type == TYPE_BOOLEAN) {
                    cell.columns.set(col, Boolean.valueOf(false));
                } else if (type == TYPE_ACTION) {
                    cell.columns.set(col, Boolean.valueOf(false));
                } else if (type == TYPE_DATE) {
                    cell.columns.set(col, new String(""));
                } else if (type == TYPE_INTEGER || type == TYPE_PERCENT) {
                    cell.columns.set(col, new Integer(0));
                } else {
                    cell.columns.set(col, "");
                }
            } else if (value instanceof String) {
                if (type == TYPE_BOOLEAN) {
                    cell.columns.set(col, 
                                     Boolean.valueOf(value.toString().equals("y")));
                } else if (type == TYPE_ACTION) {
                    cell.columns.set(col, 
                                     Boolean.valueOf(value.toString().equals("y")));
                } else if (type == TYPE_DATE) {
                    cell.columns.set(col, new String(value.toString()));
                } else if (type == TYPE_INTEGER || type == TYPE_PERCENT) {
                    cell.columns.set(col, 
                                     new Integer((value.toString().length() == 
                                                  0) ? "0" : 
                                                 value.toString()));
                } else if (combo[col] != null) {
                    cell.columns.set(col, 
                                     new ComboItem(combo[col].getItem(value.toString())));
                } else
                    cell.columns.set(col, value);
            } else
                cell.columns.set(col, value);

            fireTableCellUpdated(row, col);
        }

        /** Assigns a value at given column and current row. */
        private void setValueAt(Object value, int col) {
            setValueAt(value, currentRow, col);
        }

        /** Assigns a highlight indicator at given row and column. */
        public void setHighlightAt(boolean value, int row, int col) {
            if (row < 0 || row > (data.size() - 1))
                return;

            if (col < 0 || col > nbcols - 1)
                return;

            data.get(row).highlight.set(col, Boolean.valueOf(value));
            fireTableCellUpdated(row, col);
        }

        /** Assigns a highlight indicator at given column and current row. */
        private void setHighlightAt(boolean value, int col) {
            setHighlightAt(value, currentRow, col);
        }

        /** Assigns a hierarchical level at current row. */
        private void setLevelAt(int level) {
            setLevelAt(level, currentRow);
        }

        /** Assigns an object ID to given row. */
        public void setTicketAt(String iid, int row) {
            if (row < 0 || row > (data.size() - 1))
                return;

            ((TableCell)data.get(row)).iid = iid;
        }

        /** Assigns an object ID to current row. */
        private void setTicketAt(String iid) {
            setTicketAt(iid, currentRow);
        }

        /** Returns the minimum selection index. */
        private int getMinSelectionIndex() {
            if (data.size() == 0)
                return -1;

            return 0;
        }

        /** Returns the maximum selection index. */
        private int getMaxSelectionIndex() {
            return data.size() - 1;
        }

        /** Writes the object as an XML output. */
        private void xmlOut(WriterXML xml, int col, Object value) {
            if (value == null || value.toString().trim().length() == 0)
                return;

            String outputValue = "";

            if ((columnType[col] == TYPE_BOOLEAN) && 
                ((Boolean)value).booleanValue()) {
                outputValue = "y";
            } else if ((columnType[col] == TYPE_ACTION) && 
                ((Boolean)value).booleanValue()) {
                outputValue = "y";
            } else if (combo[col] != null) {
                outputValue = combo[col].getItemCode(value.toString());
            } else if (columnType[col] == TYPE_DATE) {
                outputValue = XMLWrapper.wrapDate(value.toString().trim());
            } else {
                outputValue = value.toString().trim();
            }

            xml.xmlAttribute(tags[col], outputValue);
        }

        /** Writes the object as an XML output. */
        private void xmlOut(WriterXML xml, String tagElement) {
            for (int row = 0; row < data.size(); row++) {
                TableCell cell = data.get(row);

                xml.xmlStart(tagElement);

                if (isHierarchical() && cell.level > 0)
                    xml.xmlAttribute("level", cell.level);

                if (cell.iid != null)
                    xml.xmlAttribute("iid", cell.iid);

                for (int col = isHierarchical() ? 1 : 0; col < nbcols; col++)
                    xmlOut(xml, col, cell.columns.get(col));

                xml.xmlEnd();

                if (cell.hidden != null) {
                    TableCell secondcell;

                    for (int i = 0; i < cell.hidden.size(); i++) {
                        secondcell = cell.hidden.get(i);

                        xml.xmlStart(tagElement);

                        if (isHierarchical() && secondcell.level > 0)
                            xml.xmlAttribute("level", secondcell.level);

                        if (secondcell.iid != null)
                            xml.xmlAttribute("iid", secondcell.iid);

                        for (int col = isHierarchical() ? 1 : 0; col < nbcols; 
                             col++)
                            xmlOut(xml, col, secondcell.columns.get(col));

                        xml.xmlEnd();
                    }
                }
            }
        }

        /** Converts the cell at given row and column into HTML. */
        private String toHtml(int row, int column, 
                              Object value) throws IOException {
            if (value == null)
                return "<td>&nbsp;</td>";

            String stringvalue = 
                (value == null) ? "" : value.toString().trim();

            if (stringvalue.length() == 0)
                return "<td>&nbsp;</td>";

            String color = "";
            boolean repeated = false;

            if (row > 0 && stringvalue.length() > 0 && 
                tableModel.getColumnType(column) == TYPE_STRING) {
                Object previous = getValueAt(row - 1, column);
                String previousValue = 
                    previous != null ? previous.toString() : "";
                if (previousValue != null) {
                    if (previousValue.trim().equals(stringvalue))
                        repeated = true;
                }
            }

            if (tableModel.getHighlightAt(row, column) || 
                ((tableModel.highlightColumn >= 0) && 
                 tableModel.getValueAt(row, 
                                       tableModel.highlightColumn).equals(Boolean.TRUE)))
                color = "color: rgb(255, 0, 0);";
            else if (repeated)
                color = "color: rgb(127, 127, 127);";

            if (tableModel.isHierarchical() && (column == 1)) {
                for (int i = 0; i < tableModel.getLevelAt(row); i++)
                    stringvalue = 
                            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + stringvalue;
            }

            if (columnType[column] == TYPE_BOOLEAN)
                return "<td style=\"text-align: center;" + color + "\">" + 
                    (((Boolean)value).booleanValue() ? "label:yes" : 
                     "&nbsp;") + "</td>";

            if (columnType[column] == TYPE_ACTION)
                return "<td style=\"text-align: center;" + color + "\">" + 
                    (((Boolean)value).booleanValue() ? "label:yes" : 
                     "&nbsp;") + "</td>";

            if (columnType[column] == TYPE_INTEGER)
                return "<td style=\"text-align: right;" + color + "\">" + 
                    (stringvalue.equals("0") ? "&nbsp;" : stringvalue) + 
                    "</td>";

            if (columnType[column] == TYPE_PERCENT) {
                int fillPercent = ((Integer)value).intValue();
                String icon = "";

                if (fillPercent == 100)
                    icon = "factory.report.complete100.gif";
                else if (fillPercent >= 75)
                    icon = "factory.report.complete75.gif";
                else if (fillPercent >= 50)
                    icon = "factory.report.complete50.gif";
                else if (fillPercent >= 25)
                    icon = "factory.report.complete25.gif";
                else if (fillPercent >= 1)
                    icon = "factory.report.complete0.gif";

                return "<td style=\"text-align: left;" + color + "\">" + 
                    (icon.length() > 0 ? 
                     "<img src=\"" + icon + "\" align=\"bottom\"/>&nbsp;" : 
                     "") + 
                    (stringvalue.equals("0") ? "&nbsp;" : (stringvalue + "%")) + 
                    "</td>";
            }

            if (columnType[column] == TYPE_DATE)
                return "<td style=\"text-align: center;" + color + "\">" + 
                    stringvalue + "</td>";

            if (tableModel.centered[column])
                return "<td style=\"text-align: center;" + color + "\">" + 
                    stringvalue + "</td>";

            if (combo[column] != null) {
                String icon = combo[column].getIconName(stringvalue);
                return "<td style=\"text-align: left;" + color + "\">" + 
                    (icon != null ? 
                     "<img src=\"factory.report." + icon + "\" align=\"bottom\"/>&nbsp;" : 
                     "") + stringvalue + "</td>";

            }
            return "<td style=\"text-align: left;" + color + "\">" + 
                stringvalue + "</td>";
        }

        /** Generates am XML list of items, based on column index and search string. */
        public void generateList(WriterXML output, String classname, 
                                 String search) throws IOException {
            List<String> list = new ArrayList();

            for (int row = 0; row < data.size(); row++) {
                String value = data.get(row).columns.get(isHierarchical ? 1 : 0).toString();

                if (search.length() > 0) {
                    if (value.toLowerCase().indexOf(search) < 0) {
                        continue;
                    }
                }

                if (!list.contains(value)) 
                    list.add(value);
            }

            if (list.size() == 0) 
                output.xmlOut("message", "message:nodata");

            for (String value: list) {
                output.xmlStart(classname);
                output.xmlAttribute("name", value);
                output.xmlAttribute("summary", value);
                output.xmlEnd();
            }

            list.clear();
            list = null;
        }

        /** Reads the object from an XML input. */
        private void xmlIn(String tag, String value) {
            if (tag.equals("level") && isHierarchical()) {
                try {
                    setLevelAt(Integer.parseInt(value));
                } catch (java.lang.NumberFormatException ex) {
                    setLevelAt(1);
                }
                return;
            }

            if (tag.equals("iid") && isHierarchical()) {
                try {
                    setTicketAt(value);
                } catch (java.lang.NumberFormatException ex) {
                    setTicketAt("");
                }
                return;
            }

            for (int col = 0; col < nbcols; col++) {
                if (tags[col].equals(tag)) {
                    setValueAt(columnType[col] == TYPE_DATE ? 
                               XMLWrapper.unwrapDate(value) : 
                               value, currentRow, col);
                    return;
                }
            }
        }

        /** Converts the table content into HTML. */
        private String toHtml() throws IOException {
            int row;
            int col;
            StringWriter out = new StringWriter();
            out.write("<table border=\"1\">");
            out.write("<tr>");
            for (col = isHierarchical() ? 1 : 0; col < nbcols; col++)
                out.write("<td><strong>" + getColumnName(col) + 
                          "</strong></td>");
            out.write("</tr>");
            for (row = 0; row < data.size(); row++) {
                out.write("<tr>");
                for (col = isHierarchical() ? 1 : 0; col < nbcols; col++)
                    out.write(toHtml(row, col, data.get(row).columns.get(col)));
                out.write("</tr>");
            }
            out.write("</table>");
            return out.toString();
        }

        /** Returns direction of sort for given column. */
        private int getDirective(int column) {
            return columnSort[column];
        }

        /** Returns sort status for given column. */
        private int getSortingStatus(int column) {
            return getDirective(column);
        }

        /** Assigns a sort status to given column. */
        private void setSortingStatus(int column, int status) {
            columnSort[column] = status;
            sortingStatusChanged();
        }

        /** Indicates if the table is sorted. */
        private boolean isSorting() {
            for (int col = 0; col < nbcols; col++)
                if (columnSort[col] != NOT_SORTED) {
                    return true;
                }

            return false;
        }

        /** Returns the column used for sorting. */
        private int getSortingColumn() {
            for (int col = 0; col < nbcols; col++)
                if (columnSort[col] != NOT_SORTED) {
                    return col;
                }

            return -1;
        }

        /** Sorts data in the table. */
        private void sort() {
            if (!isSorting() || noSort)
                return;

            Collections.sort(data);
            fireTableDataChanged();
        }

        /** Tells the sorting status has been changed. */
        private void sortingStatusChanged() {
            fireTableDataChanged();
            header.repaint();
        }

        /** Resets sort indicators. */
        private void cancelSorting() {
            for (int col = 0; col < nbcols; col++)
                columnSort[col] = NOT_SORTED;

            sortingStatusChanged();
        }

        /** Defines the table as hierarchical. */
        private void setHierarchical() {
            isHierarchical = true;

            for (int i = nbcols - 1; i >= 0; i--) {
                columnName[i + 1] = new String(columnName[i]);
                columnType[i + 1] = columnType[i];
                columnSort[i + 1] = columnSort[i];
                tags[i + 1] = new String(tags[i]);
                readOnly[i + 1] = readOnly[i];
            }

            ++nbcols;

            columnName[0] = new String(" ");
            columnType[0] = TYPE_BOOLEAN;
            columnSort[0] = NOT_SORTED;
            tags[0] = new String("");
            readOnly[0] = true;

            fireTableStructureChanged();
            setTableHeader(getTableHeader());
            setWidth(20, 0);
        }

        /** Indicates if the table is hierarchical. */
        private boolean isHierarchical() {
            return isHierarchical;
        }

        /** Adds a hierarchical level to given row. */
        private void addLevel(int row) {
            if (row == 0)
                return;

            setLevelAt(1, row);
        }

        /** Subtracts a hierarchical level to given row. */
        private void removeLevel(int row) {
            if (row == 0)
                return;

            setLevelAt(-1, row);
        }

        /** Defines if the table can be enabled. */
        private void setSoftEnabled(boolean enabled) {
            this.softEnabled = enabled;
            fireTableDataChanged();
        }

        /** Moves the given row up in the list. */
        private void up(int row) {
            Collections.swap(data, row - 1, row);
            fireTableRowsUpdated(0, row);
        }

        /** Moves the given row down in the list. */
        private void down(int row) {
            Collections.swap(data, row, row + 1);
            fireTableRowsUpdated(0, row + 1);
        }

        /** Indicates if the given column is read only. */
        private boolean isReadOnly(int col) {
            return readOnly[col];
        }

        /**
		  * Represents the mouse listener used for sorting.
		  */
        private class MouseHandler extends MouseAdapter {

            /** Manages mouse clicks. */
            public void mouseClicked(MouseEvent e) {
                if (isHierarchical() || noSort)
                    return;

                JTableHeader h = (JTableHeader)e.getSource();
                TableColumnModel columnModel = h.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int column = columnModel.getColumn(viewColumn).getModelIndex();

                if (column != -1) {
                    int status = getSortingStatus(column);
                    cancelSorting();
                    status = (status == ASCENDING) ? DESCENDING : ASCENDING;
                    status = ((status + 4) % 3) - 1;
                    setSortingStatus(column, status);
                }

                sort();
            }
        }

        /**
		 * Represents data values stored in the table.
		 */
        private class TableCell implements Comparable {

            /** Table model. */
            private PanelTableModel model;

            /** Data stored in columns. */
            private List<Object> columns = new ArrayList(10);

            /** Hierarchical level. */
            private int level;

            /** Hide indicators stored for rows of data. */
            private List<TableCell> hidden = null;

            /** Internal object ID. */
            private String iid;

            /** Hightlight indicators stored for rows of data. */
            private List<Boolean> highlight = new ArrayList(10);

            /** Constructor. */
            TableCell(PanelTableModel model) {
                this.model = model;
            }

            /** Compares this object with the specified object for order. */
            public int compareTo(Object o) {
                int col = model.getSortingColumn();

                if (col < 0)
                    return 0;

                int direction = model.getDirective(col);
                int type = model.getColumnType(col);

                if (type == TYPE_DATE) {
                    Date d1;
                    Date d2;

                    try {
                        d1 = XMLWrapper.dsLocal.parse(columns.get(col).toString());
                    } catch (java.text.ParseException ex) {
                        d1 = null;
                    }

                    try {
                        d2 = 
 XMLWrapper.dsLocal.parse(((TableCell)o).columns.get(col).toString());
                    } catch (java.text.ParseException ex) {
                        d2 = null;
                    }

                    if ((d1 == null) && (d2 == null)) {
                        return 0;
                    } else if ((d1 == null) && (d2 != null)) {
                        return -direction;
                    } else if ((d1 != null) && (d2 == null)) {
                        return direction;
                    } else {
                        return direction * d1.compareTo(d2);
                    }
                }

                if (type == TYPE_INTEGER || type == TYPE_PERCENT) {
                    Integer s1 = (Integer)columns.get(col);
                    Integer s2 = (Integer)((TableCell)o).columns.get(col);

                    return direction * s1.compareTo(s2);
                }

                String s1 = columns.get(col).toString();
                String s2 = ((TableCell)o).columns.get(col).toString();

                return direction * s1.compareTo(s2);
            }

            /** Returns hierarchical level. */
            public int getLevel() {
                return level;
            }

            /** Assigns a hierarchical level. */
            public void setLevel(int level) {
                this.level = level;
            }

            /** Returns hierarchical level based on a delta number. */
            public void setDeltaLevel(int delta) {
                this.level += delta;

                if (this.level < 0) {
                    this.level = 0;

                    return;
                }

                if (hidden != null) {
                    for (int i = 0; i < hidden.size(); i++)
                        hidden.get(i).setDeltaLevel(delta);
                }
            }
        }
    }

    /**
	 * Represents a renderer of table header.
	 */
    private class SortableHeaderRenderer implements TableCellRenderer {

        /** Cell rendered. */
        private TableCellRenderer tableCellRenderer;

        /** Constructor. */
        public SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
            this.tableCellRenderer = tableCellRenderer;
        }

        /** Returns the component to be displayed. */
        public Component getTableCellRendererComponent(JTable table, 
                                                       Object value, 
                                                       boolean isSelected, 
                                                       boolean hasFocus, 
                                                       int row, int column) {
            Component c = 
                tableCellRenderer.getTableCellRendererComponent(table, value, 
                                                                isSelected, 
                                                                hasFocus, row, 
                                                                column);

            if (!noSort && c instanceof JLabel) {
                JLabel l = (JLabel)c;
                l.setHorizontalTextPosition(JLabel.RIGHT);

                int modelColumn = table.convertColumnIndexToModel(column);
                l.setIcon(getHeaderRendererIcon(modelColumn));
            }

            return c;
        }
    }

    /**
	 * Represents a renderer of table cell when the cell is not a boolean.
	 */
    private class LabelRenderer extends JLabel implements TableCellRenderer {

        /** Returns the component to be displayed. */
        public Component getTableCellRendererComponent(JTable table, 
                                                       Object value, 
                                                       boolean isSelected, 
                                                       boolean hasFocus, 
                                                       int row, int column) {
            int fillPercent;
            boolean repeated = false;
            String stringvalue = 
                (value == null) ? "" : value.toString().trim();
            fillPercent = 0;

            if (tableModel.isHierarchical()) {
                int level = tableModel.getLevelAt(row);
                int nextLevel = tableModel.getLevelAt(row + 1);
                if (column == 1) {
                    for (int i = 0; i < tableModel.getLevelAt(row); i++)
                        stringvalue = "   " + stringvalue;
                }
                else if ((nextLevel > 0 && level < nextLevel) || 
                    tableModel.hasHidden(row)) {
                    return null;
                }
            }

            // Display in gray if the same label is displayed a row above
            if (row > 0 && stringvalue.length() > 0 && 
                tableModel.getColumnType(column) == TYPE_STRING) {
                Object previous = getValueAt(row - 1, column);
                String previousValue = 
                    previous != null ? previous.toString() : "";
                if (previousValue != null) {
                    if (previousValue.trim().equals(stringvalue))
                        repeated = true;
                }
            }

            if ((tableModel.getColumnType(column) == TYPE_INTEGER) && 
                stringvalue.equals("0")) {
                stringvalue = "";
                setIcon(nullArrow);
            } else if (tableModel.getColumnType(column) == TYPE_PERCENT) {
                if (stringvalue.equals("0")) {
                    stringvalue = "";
                    setIcon(nullArrow);
                } else if (stringvalue.length() > 0) {
                    fillPercent = ((Integer)value).intValue();
                    stringvalue += "%";
                    if (fillPercent == 100)
                        setIcon(LocalIcon.get("complete100.gif"));
                    else if (fillPercent >= 75)
                        setIcon(LocalIcon.get("complete75.gif"));
                    else if (fillPercent >= 50)
                        setIcon(LocalIcon.get("complete50.gif"));
                    else if (fillPercent >= 25)
                        setIcon(LocalIcon.get("complete25.gif"));
                    else if (fillPercent >= 1)
                        setIcon(LocalIcon.get("complete0.gif"));
                } else
                    setIcon(nullArrow);
            } else {
                if (tableModel.combo[column] != null) {
                    Icon icon = 
                        tableModel.combo[column].getIconLabel(stringvalue);
                    setIcon(icon == null ? nullArrow : icon);
                } else
                    setIcon(nullArrow);
            }

            setText(stringvalue);
            setBorder((hasFocus && tableModel.softEnabled) ? selectedBorder : 
                      unselectedBorder);
            setForeground((tableModel.getHighlightAt(row, column) || 
                           ((tableModel.highlightColumn >= 0) && 
                            tableModel.getValueAt(row, 
                                                  tableModel.highlightColumn).equals(Boolean.TRUE))) ? 
                          Color.RED : (repeated ? Color.GRAY : Color.BLACK));
            setOpaque(true);
            if (row % 2 == 1)
                setBackground((tableModel.softEnabled && 
                               !tableModel.isReadOnly(column)) ? 
                              XMLWrapper.editColorDark : XMLWrapper.lightGray);
            else
                setBackground((tableModel.softEnabled && 
                               !tableModel.isReadOnly(column)) ? 
                              XMLWrapper.editColor : Color.WHITE);

            if (tableModel.centered[column]) 
                setHorizontalAlignment(JLabel.CENTER);
            else if (tableModel.getColumnType(column) == TYPE_INTEGER) 
                setHorizontalAlignment(JLabel.RIGHT);
            else if (tableModel.getColumnType(column) == TYPE_DATE) 
                setHorizontalAlignment(JLabel.CENTER);
            else 
                setHorizontalAlignment(LocalMessage.isRightToLeft() ? 
                                       JLabel.RIGHT : JLabel.LEFT);
            return this;
        }
    }

    /**
	 * Represents a renderer of table cell when the cell is a boolean.
	 */
    private class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

        /** Returns the component to be displayed. */
        public Component getTableCellRendererComponent(JTable table, 
                                                       Object value, 
                                                       boolean isSelected, 
                                                       boolean hasFocus, 
                                                       int row, int column) {
            setSelected(value != null && ((Boolean)value).booleanValue());
            setForeground(Color.BLACK);
            setOpaque(true);
            if (row % 2 == 1)
                setBackground((tableModel.softEnabled && 
                               !tableModel.isReadOnly(column)) ? 
                              XMLWrapper.editColorDark : XMLWrapper.lightGray);
            else
                setBackground((tableModel.softEnabled && 
                               !tableModel.isReadOnly(column)) ? 
                              XMLWrapper.editColor : Color.WHITE);
            setEnabled(tableModel.softEnabled && 
                       !tableModel.isReadOnly(column));
            setBorderPaintedFlat(true);
            setBorder((hasFocus && tableModel.softEnabled) ? selectedBorder : 
                      unselectedBorder);

            if (tableModel.isHierarchical()) {
                int level = tableModel.getLevelAt(row);
                int nextLevel = tableModel.getLevelAt(row + 1);
                if (column == 0) {
                    setEnabled(true);
                    if ((nextLevel > 0 && level < nextLevel) || 
                        tableModel.hasHidden(row)) 
                        setIcon(null);
                    else 
                        setIcon(nullArrow);
                } else {
                    if ((nextLevel > 0 && level < nextLevel) || 
                        tableModel.hasHidden(row)) {
                        return null;
                    }
                    else setIcon(null);
                }
            } else 
                setIcon(null);

            if(tableModel.getColumnType(column) == TYPE_ACTION) {
                setEnabled(!tableModel.softEnabled);
                setIcon(LocalIcon.get(
                    ((Boolean)value).booleanValue() ? "action.gif" : "plus.gif"));
            }

            setHorizontalAlignment(JLabel.CENTER);
            return this;
        }
    }
}
