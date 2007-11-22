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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/FrameMain.java,v $
$Revision: 1.65 $
$Date: 2007/03/17 20:00:56 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import java.io.File;

import java.lang.StackTraceElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.projectfactory.FactoryBuild;
import net.sourceforge.projectfactory.client.components.Arrow;
import net.sourceforge.projectfactory.client.components.ButtonToggleFactory;
import net.sourceforge.projectfactory.client.components.ComboBoxCode;
import net.sourceforge.projectfactory.client.components.LabelBox;
import net.sourceforge.projectfactory.client.components.LocalIcon;
import net.sourceforge.projectfactory.client.components.LocalMessage;
import net.sourceforge.projectfactory.client.components.LocalSplash;
import net.sourceforge.projectfactory.client.components.PanelCalendar;
import net.sourceforge.projectfactory.client.components.PanelDataLookup;
import net.sourceforge.projectfactory.client.components.PanelDiagram;
import net.sourceforge.projectfactory.client.components.ToggleButtonAction;
import net.sourceforge.projectfactory.client.components.ToggleButtonCategory;
import net.sourceforge.projectfactory.client.panels.PanelData;
import net.sourceforge.projectfactory.client.xml.ImportDataXML;
import net.sourceforge.projectfactory.client.xml.ImportErrXML;
import net.sourceforge.projectfactory.client.xml.ImportListXML;
import net.sourceforge.projectfactory.client.xml.ImportPreferenceXML;
import net.sourceforge.projectfactory.client.xml.ImportSecurityXML;
import net.sourceforge.projectfactory.middleware.FactoryConnection;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Main window used for the whole application.
 * @author David Lambert
 */
public class FrameMain extends JFrame implements ActionListener {

    /** Maximum value for the progress bar : equals to the number of statements
	 *  LocalSplash.addProgressValue(). */
    public static final int PROGRESS_MAX = 7;

    /** Session to the server. */
    protected FactoryConnection connection;

    /** System user (provided by the operating system). */
    private static final String user = 
        System.getProperty("user.name").toLowerCase();

    /** Splitter size. */
    private static final int SPLITSIZE = 10;

    /** Size for separators. */
    private static final int SEPARATOR_SIZE = 10;

    /** Counts the number of opened windows. */
    protected static int windows;

    /** Indicates if the latest requests returned an error or not. */
    private boolean inError;

    /** Category selected by the user. */
    public String selectionCategory = "actors";

    /** Main panel. */
    private JPanel panelContent;

    /** Search panel. */
    public PanelDataLookup panelSearch = new PanelDataLookup(this);

    /** Workbench. */
    private JPanel panelWorkbench = new JPanel();

    /** Message and help lowerTabs. */
    private JTabbedPane lowerTabs = new JTabbedPane();

    /** Calendar panel. */
    private PanelCalendar calendarPanel = new PanelCalendar();

    /** Horizontal split (from main data panel and message panel. */
    public JSplitPane splitHorizontal;

    /** Vertical split (from lookup panel and main data panel. */
    public JSplitPane splitVertical;

    /** Scroll pane used in order to display messages. */
    private JScrollPane scrollListMessages = new JScrollPane();

    /** Tree node used for the messages. */
    private DefaultMutableTreeNode treeNodeMessages = 
        new DefaultMutableTreeNode();

    /** Data model used for the messages. */
    private DefaultTreeModel treeModelMessages = 
        new DefaultTreeModel(treeNodeMessages);

    /** Internal storage for messages. */
    private List<String> messages = new ArrayList(40);

    /** Tree used for the messages. */
    private JTree treeMessages = new JTree(treeModelMessages);

    /** Panel used in order to 'store' data panels in a stack. */
    private JPanel stackPanels = new JPanel();

    /** Card layout used in order to display data panels in a stack. */
    private CardLayout layoutData = new CardLayout();

    /** List of the panels. */
    private HashMap<String,PanelData> listPanels = new HashMap();

    /** Current 'displayed' panel. */
    private PanelData currentPanel;

    /** 'Blank' panel, splash, preferences, help... */
    public PanelData panelBlank;

    /** Action bar. */
    protected JToolBar actionBar = new JToolBar();

    /** Command bar. */
    private JToolBar commandBar = new JToolBar();

    /** Button 1 for new object. */
    protected ButtonToggleFactory buttonNew1 = 
        new ButtonToggleFactory("", "", "plus.gif");

    /** Button 2 for new object. */
    protected ButtonToggleFactory buttonNew2 = 
        new ButtonToggleFactory("", "", "plus.gif");

    /** Button 3 for new object. */
    protected ButtonToggleFactory buttonNew3 = 
        new ButtonToggleFactory("", "", "plus.gif");

    /** Button separator before edit buttons. */
    private JToolBar.Separator separatorEdit = 
        new JToolBar.Separator(new Dimension(SEPARATOR_SIZE, 0));

    /** Close button. */
    private ButtonToggleFactory buttonClose = 
        new ButtonToggleFactory("button:close", "button:close:tip", "close.gif");

    /** Edit button. */
    private ButtonToggleFactory buttonEdit = 
        new ButtonToggleFactory("button:edit", "button:edit:tip", "edit.gif");

    /** Print button. */
    private ButtonToggleFactory buttonPrint = 
        new ButtonToggleFactory("", "button:print:tip", "print.gif");

    /** Print diagram button. */
    private ButtonToggleFactory buttonPrintDiagram = 
        new ButtonToggleFactory("", "", "print.gif");

    /** Delete button. */
    private ButtonToggleFactory buttonDelete = 
        new ButtonToggleFactory("button:delete", "button:delete:tip", "minus.gif");

    /** Button separator before save buttons. */
    private JToolBar.Separator separatorSave = 
        new JToolBar.Separator(new Dimension(SEPARATOR_SIZE, 0));

    /** Save button. */
    private ButtonToggleFactory buttonSave = 
        new ButtonToggleFactory("button:save", "button:save:tip", "save.gif");

    /** Button separator before new window button. */
    private JToolBar.Separator separatorNewWindow = 
        new JToolBar.Separator(new Dimension(SEPARATOR_SIZE, 0));

    /** New window button. */
    private ButtonToggleFactory buttonNewWindow = 
        new ButtonToggleFactory("", "button:newwindow:tip", "window.gif");

    /** Zoom button. */
    private ButtonToggleFactory buttonZoom = 
        new ButtonToggleFactory("", "button:zoom:tip", "zoom.gif");

    /** Bottom Zoom button. */
    private ButtonToggleFactory buttonBottomZoom = 
        new ButtonToggleFactory("", "button:bottomzoom:tip", "zoominv.gif");

    /** Position of horizontal and vertical spliters. */
    private int posSplitX;

    /** Position of horizontal and vertical spliters. */
    private int posSplitY;

    /** Menu bar. */
    private JMenuBar menuBar = new JMenuBar();

    /** Menu file. */
    private JMenu menuFile;

    /** Menu go. */
    private JMenu menuGo;

    /** Menu window. */
    private JMenu menuWindow;

    /** Menu server configuration. */
    private JMenuItem menuConfigureServer;

    /** Menu connect to server. */
    private JMenuItem menuConnectServer;

    /** Menu disconnect to server. */
    private JMenuItem menuDisconnectServer;

    /** Menu connexions. */
    private JMenuItem menuShowConnexions;

    /** Menu zoom. */
    private JMenuItem menuZoom;

    /** Menu zoom of window bottom. */
    private JMenuItem menuBottomZoom;

    /** Menu new element 1. */
    protected JMenuItem menuNew1;

    /** Menu new element 2. */
    protected JMenuItem menuNew2;

    /** Menu new element 3. */
    protected JMenuItem menuNew3;

    /** Context menu new element 1. */
    public JMenuItem menuNewExtra1;

    /** Context menu new element 2. */
    public JMenuItem menuNewExtra2;

    /** Context menu new element 3. */
    public JMenuItem menuNewExtra3;

    /** Menu close. */
    protected JMenuItem menuClose;

    /** Menu edit. */
    protected JMenuItem menuEdit;

    /** Menu delete. */
    protected JMenuItem menuDelete;

    /** Menu save. */
    protected JMenuItem menuSave;

    /** Menu backup all. */
    protected JMenuItem menuBackupAll;

    /** Menu backup item. */
    protected JMenuItem menuBackupItem;

    /** Menu backup demo data. */
    protected JMenuItem menuBackupDemo;

    /** Menu restore. */
    protected JMenuItem menuRestore;

    /** Menu import. */
    protected JMenuItem menuImport;

    /** Menu exit. */
    protected JMenuItem menuExit;

    /** Print menu. */
    protected JMenuItem menuPrint;

    /** Print diagram menu. */
    protected JMenuItem menuPrintDiagram;

    /** New window menu. */
    protected JMenuItem menuNewWindow;

    /** Product home page menu. */
    protected JMenuItem menuWeb;

    /** Developer home page menu. */
    protected JMenuItem menuDevWeb;

    /** License menu. */
    protected JMenuItem menuLicense;

    /** Bug database. */
    protected JMenuItem menuBug;

    /** Main panel is zoomed. */
    private boolean zoomed;

    /** Message panel is zoomed. */
    private boolean bottomZoomed;

    /** Default values used in order to send request information to the server
     *  during the default phase (new element). */
    private List<DefaultValue> defaultValues = new ArrayList(2);

    /** Any lookup activity is suspended when blockLooup is true. */
    public boolean blockLookup;

    /** Thread used in order to load all the panels in the background at the
     *  begining of the frame construction. */
    protected ThreadLoadPanels threadLoadPanels;

    /** Diagram options. */
    public ComboBoxCode comboDisplayOptions = new ComboBoxCode("diagram:details");

    /** Diagram periods. */
    public ComboBoxCode comboDisplayPeriod = new ComboBoxCode("diagram:period");

    /** Diagram actors. */
    public ComboBoxCode comboDisplayActors = new ComboBoxCode("diagram:actors");

    /** Counter. */
    public LabelBox count = new LabelBox("1");

    /** Diagram panel. */
    private PanelDiagram diagramPanel = new PanelDiagram();

    /** Connected server name. */
    private String serverName = "";
    
    /** Processing message. */
    private boolean processing;

    /** Count string. */
    private String countString = "";

	/** Indicates the thread is initialized. */
	private volatile boolean initialized;

    /** Lists of buttons available in the command bar. */
    private List<ToggleButtonCategory> buttons = new ArrayList(10);
    
    /** Short cut used for menu items. */
    private static int shortCutKey = 0;
    
    /** Constructor, the window with all its decorations is initialized. */
    public FrameMain(FactoryConnection connection) {
        LocalSplash.addProgressValue("initialization");
        this.connection = connection;
        connection.attach(this);
        blockLookup = true;

        // Message panels must be created before anything else.
        treeMessages.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeMessages.setAutoscrolls(true);
        treeMessages.setRootVisible(false);
        treeMessages.setFocusable(false);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(new Arrow(true, 0));
        treeMessages.setCellRenderer(renderer);
        scrollListMessages.setViewportView(treeMessages);
        scrollListMessages.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollListMessages.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollListMessages.setRowHeader(null);
        scrollListMessages.setAutoscrolls(true);
        scrollListMessages.setFocusable(false);
        lowerTabs.setFocusable(false);
        lowerTabs.setTabPlacement(JTabbedPane.BOTTOM);
        lowerTabs.add(scrollListMessages, LocalMessage.get("tab:messages"));
        lowerTabs.add(calendarPanel, LocalMessage.get("tab:calendar"));
        lowerTabs.add(diagramPanel, LocalMessage.get("tab:diagram"));

        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        LocalSplash.addProgressValue("setmainwindow");
        setTitle(getFrameTitle());

        ImageIcon icon = LocalIcon.get("factory_icon.jpg");
        if (icon != null)
            setIconImage(icon.getImage());

        Runtime.getRuntime().addShutdownHook(new ThreadShutDown());
        try {
            setSize(new Dimension(820, 570));
            splitHorizontal = 
                    new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, panelWorkbench, 
                                   lowerTabs);
            splitVertical = 
                    LocalMessage.isRightToLeft() ? new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                                                                  true, 
                                                                  splitHorizontal, 
                                                                  panelSearch) : 
                    new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, 
                                   panelSearch, splitHorizontal);
            panelContent = (JPanel)this.getContentPane();
            panelContent.setLayout(new BorderLayout());
            splitHorizontal.setDividerLocation(330);
            splitHorizontal.setResizeWeight(1);
            splitVertical.setDividerLocation(LocalMessage.isRightToLeft() ? 
                                             820 - 160 : 160);
            stackPanels.setLayout(layoutData);
            stackPanels.setVisible(false);
            panelWorkbench.setLayout(new BorderLayout());
            LocalSplash.addProgressValue("createmenus");

            if (LocalMessage.isRightToLeft()) {
                treeMessages.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                actionBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                commandBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                buttonNew1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                buttonNew2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                buttonNew3.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                buttonSave.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                buttonClose.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                buttonEdit.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                buttonPrint.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                buttonDelete.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                buttonNewWindow.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                buttonZoom.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                buttonBottomZoom.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            }

            buttonNew1.addActionListener(this);
            buttonNew2.addActionListener(this);
            buttonNew3.addActionListener(this);
            buttonSave.addActionListener(this);
            buttonClose.addActionListener(this);
            buttonEdit.addActionListener(this);
            buttonPrint.addActionListener(this);
            buttonDelete.addActionListener(this);
            buttonNewWindow.addActionListener(this);
            buttonZoom.addActionListener(this);
            buttonBottomZoom.addActionListener(this);

            commandBar.setFloatable(false);
            commandBar.add(buttonNew1, null);
            commandBar.add(buttonNew2, null);
            commandBar.add(buttonNew3, null);
            separatorEdit.setOrientation(JSeparator.VERTICAL);
            commandBar.add(separatorEdit, null);
            commandBar.add(buttonClose, null);
            commandBar.add(buttonEdit, null);
            commandBar.add(buttonDelete, null);
            separatorSave.setOrientation(JSeparator.VERTICAL);
            commandBar.add(separatorSave, null);
            commandBar.add(buttonSave, null);
            separatorNewWindow.setOrientation(JSeparator.VERTICAL);
            commandBar.add(separatorNewWindow, null);
            commandBar.add(buttonPrint, null);
            commandBar.add(buttonZoom, null);
            commandBar.add(buttonBottomZoom, null);
            commandBar.add(buttonNewWindow, null);
            count.setVisible(false);
            actionBar.add(count, null);

            JPanel actionBars = new JPanel();
            actionBars.setLayout(new BorderLayout());
            actionBars.add(actionBar, BorderLayout.NORTH);
            actionBars.add(commandBar, BorderLayout.SOUTH);
            panelContent.add(actionBars, BorderLayout.NORTH);

            LocalSplash.addProgressValue("createcalendarpanel");
            calendarPanel.setFocusable(false);
            calendarPanel.init();
            LocalSplash.addProgressValue("creatediagrampanel");
            diagramPanel.setFocusable(false);
            diagramPanel.init(comboDisplayOptions, comboDisplayPeriod, 
                              comboDisplayActors);
            LocalSplash.addProgressValue("createsearchpanel");
            panelSearch.init();

            LocalSplash.addProgressValue("createdecorations");
            splitVertical.setBorder(null);
            splitHorizontal.setBorder(null);
            splitVertical.setDividerSize(SPLITSIZE);
            splitHorizontal.setDividerSize(SPLITSIZE);
            panelContent.add(splitVertical, BorderLayout.CENTER);
            panelWorkbench.add(stackPanels, BorderLayout.CENTER);

            separatorEdit.setVisible(false);
            buttonClose.setVisible(false);
            buttonEdit.setVisible(false);
            buttonDelete.setVisible(false);
            buttonSave.setVisible(false);
            buttonPrint.setVisible(false);

            createmenus();
            loadPanels();
            
            LocalSplash.addProgressValue("startdatabase");
            if (++windows == 1) {
                addMessage("TRC", 
                           LocalMessage.get("message:jre", 
                                            System.getProperty("java.version"), 
                                            System.getProperty("java.vendor")));
                addMessage("TRC", "os.name:" + System.getProperty("os.name"));
                addMessage("TRC", "os.arch:" + System.getProperty("os.arch"));
                addMessage("TRC", 
                           "os.version:" + System.getProperty("os.version"));
                addMessage("TRC", "user.home:" + System.getProperty("user.home"));
                addMessage("TRC", 
                           "java.vm.name:" + System.getProperty("java.vm.name"));
                addMessage("TRC", 
                           "java.vm.version:" + System.getProperty("java.vm.version"));
                addMessage("TRC", 
                           "java.vm.vendor:" + System.getProperty("java.vm.vendor"));
                addMessage("TRC", "java.home:" + System.getProperty("java.home"));
                addMessage("TRC", 
                           "java.class.path:" + System.getProperty("java.class.path"));
                addMessage("TRC", "user.dir:" + System.getProperty("user.dir"));
                addMessage("MSG", LocalMessage.get("message:welcome", user));
                new ImportDataXML(this).xmlIn(connection.getQueue(), null, false);
                new ImportErrXML(this).xmlErrorIn(connection.getErrorQueue());
                LocalSplash.hide();
                setCursor(false);
                panelSearch.setFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(e);
        }

		showPanel("blank");
		panelBlank = currentPanel;
        stackPanels.setVisible(true);
		initialized = true;

        initializeCommands();
        readPreferences();
        activateNewButtons();
        getLookupParams();
        resetCategories();
        refreshMenus();
        blockLookup = false;
        panelSearch.runLookup();
        setVisible(true);
        
        /*My Test*/
        /*
        FactoryWriterXML query;
    	FactoryWriterXML answer;
        query = new FactoryWriterXML("query:new");
		query.xmlStart("team");
		query.xmlOut("iid","doak11955005096444");
		query.xmlOut("name","MyTeam");
		query.xmlOut("revision","1");
		query.xmlOut("active","y");
		query.xmlOut("administrator","n");
		query.xmlEnd();
		answer = new FactoryWriterXML();
		connection.queryLocal(query, answer);
		*/
    }

    /** Initialize commands. */
    private void initializeCommands() {
        try {
            setCursor(true);
            shortCutKey = 0;
            menuGo.removeAll();
            actionBar.removeAll();
            FactoryWriterXML query = new FactoryWriterXML("query:connect");
            query.xmlOut("menulist", "y");
            FactoryWriterXML answer = new FactoryWriterXML();
            querySession(query, answer);
            new ImportSecurityXML(this).xmlIn(answer, null, false);
        } catch (Exception ex) {
            addMessage(ex);
            return;
        } finally {
            actionBar.repaint();
            setCursor(false);
        }
    }

	/** Indicates the thread is initialized. */
	public boolean isInitialized() {
		return initialized;
	}

    /** Sets the title for this frame to the specified string. */
    public void setTitleDocument(String name) {
        if (XMLWrapper.isMac)
            setTitle(XMLWrapper.unwrapEmbeddedDate(name) + " (" + 
                     LocalMessage.get("label:" + getCurrent().getTagpanel()) + 
                     ") - " + getFrameTitle());
        else
            setTitle(getFrameTitle() + " - " + 
                     XMLWrapper.unwrapEmbeddedDate(name) + " (" + 
                     LocalMessage.get("label:" + getCurrent().getTagpanel()) + 
                     ")");
    }

    /** Returns title to be used for the frame. */
    public String getFrameTitle() {
        return FactoryBuild.getShortTitle();
    }

    /** Returns title to be used as build number. */
    public String getBuild() {
        return FactoryBuild.getBuild();
    }

    /** Returns the path to be used in order to store data. */
    public String getPath() {
        return FactoryBuild.getPath();
    }

    /** Returns home page URL. */
    public String getHomePage() {
        return FactoryBuild.getHomePage();
    }

    /** Returns developer page URL. */
    public String getDevPage() {
        return FactoryBuild.getDevPage();
    }

    /** Returns bug page URL. */
    public String getBugPage() {
        return FactoryBuild.getBugPage();
    }

    /** Copyright. */
    public String getCopyright() {
        return FactoryBuild.getCopyright();
    }

    /** Licence. */
    public String getLicense() {
        return FactoryBuild.getLicense();
    }

    /** List of extensions for managed applications. */	
    public String[] getApplicationExtensions() {
        return FactoryBuild.getApplicationExtensions();
    }

    /** Load panels using a thread. */
    protected void loadPanels() {
        threadLoadPanels = new ThreadLoadPanels(this);
    }

    /** Adds buttons to action bar. */
    public void addActionBar(ToggleButtonCategory button) {
        actionBar.setFloatable(false);
        if (LocalMessage.isRightToLeft()) 
            button.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        actionBar.add(button, null);
        buttons.add(button);
        button.addButtonMenu(menuGo, ++shortCutKey);
    }

    /** Create menus items. */
    protected void createmenus() {
        setJMenuBar(menuBar);
        menuFile = menuBar.add(new JMenu(LocalMessage.get("menu:file")));
        menuNew1 = 
                menuFile.add(new JMenuItem("+", LocalIcon.get("plus.gif")));
        menuNew1.addActionListener(this);
        menuNew1.setAccelerator(KeyStroke.getKeyStroke('N', XMLWrapper.ControlKey));
        menuNew2 = 
                menuFile.add(new JMenuItem("+", LocalIcon.get("plus.gif")));
        menuNew2.addActionListener(this);
        menuNew3 = 
                menuFile.add(new JMenuItem("+", LocalIcon.get("plus.gif")));
        menuNew3.addActionListener(this);
        menuNewExtra1 = 
                menuFile.add(new JMenuItem("+", LocalIcon.get("plus.gif")));
        menuNewExtra2 = 
                menuFile.add(new JMenuItem("+", LocalIcon.get("plus.gif")));
        menuNewExtra3 = 
                menuFile.add(new JMenuItem("+", LocalIcon.get("plus.gif")));
        menuFile.add(new JSeparator());
        menuClose = 
                menuFile.add(new JMenuItem(LocalMessage.get("button:close"), 
                                           LocalIcon.get("close.gif")));
        menuClose.addActionListener(this);
        menuClose.setAccelerator(KeyStroke.getKeyStroke('W', XMLWrapper.ControlKey));
        menuEdit = 
                menuFile.add(new JMenuItem(LocalMessage.get("button:edit"), LocalIcon.get("edit.gif")));
        menuEdit.addActionListener(this);
        menuEdit.setAccelerator(KeyStroke.getKeyStroke('E', XMLWrapper.ControlKey));
        menuDelete = 
                menuFile.add(new JMenuItem(LocalMessage.get("button:delete"), 
                                           LocalIcon.get("minus.gif")));
        menuDelete.addActionListener(this);
        menuDelete.setAccelerator(KeyStroke.getKeyStroke('D', XMLWrapper.ControlKey));
        menuSave = 
                menuFile.add(new JMenuItem(LocalMessage.get("button:save"), LocalIcon.get("save.gif")));
        menuSave.addActionListener(this);
        menuSave.setAccelerator(KeyStroke.getKeyStroke('S', XMLWrapper.ControlKey));
        menuFile.add(new JSeparator());
        menuBackupItem = 
                menuFile.add(new JMenuItem(LocalMessage.get("label:backup:item") + 
                                           "..."));
        menuBackupItem.addActionListener(this);
        menuBackupAll = 
                menuFile.add(new JMenuItem(LocalMessage.get("label:backup:all") + 
                                           "..."));
        menuBackupAll.addActionListener(this);
        menuBackupDemo = 
                menuFile.add(new JMenuItem(LocalMessage.get("label:backupdemo")));
        menuBackupDemo.addActionListener(this);
        menuFile.add(new JSeparator());
        menuRestore = 
                menuFile.add(new JMenuItem(LocalMessage.get("label:restore") + 
                                           "..."));
        menuRestore.addActionListener(this);
        menuImport = 
                menuFile.add(new JMenuItem(LocalMessage.get("label:import") + 
                                           "..."));
        menuImport.addActionListener(this);
        menuFile.add(new JSeparator());
        menuPrint = 
                menuFile.add(new JMenuItem(LocalMessage.get("button:print"), 
                                           LocalIcon.get("print.gif")));
        menuPrint.addActionListener(this);
        menuPrint.setAccelerator(KeyStroke.getKeyStroke('P', XMLWrapper.ControlKey));
        menuPrintDiagram = 
                menuFile.add(new JMenuItem(LocalMessage.get("button:printdiagram"), 
                                           LocalIcon.get("print.gif")));
        menuPrintDiagram.addActionListener(this);
        menuPrintDiagram.setAccelerator(KeyStroke.getKeyStroke('P', 
                                                               Event.SHIFT_MASK | 
                                                               XMLWrapper.ControlKey));

        menuFile.add(new JSeparator());
        menuConfigureServer = 
                menuFile.add(new JMenuItem(LocalMessage.get("button:configureserver") + 
                                         "..."));
        menuConfigureServer.addActionListener(this);

        menuFile.add(new JSeparator());
        menuConnectServer = 
                menuFile.add(new JMenuItem(LocalMessage.get("button:connectserver") + 
                                         "..."));
        menuConnectServer.addActionListener(this);
        menuConnectServer.setAccelerator(KeyStroke.getKeyStroke('K', 
                                                                XMLWrapper.ControlKey));
        menuDisconnectServer = 
                menuFile.add(new JMenuItem(LocalMessage.get("button:disconnectserver")));
        menuDisconnectServer.addActionListener(this);
        menuDisconnectServer.setAccelerator(KeyStroke.getKeyStroke('K', 
                                                                   Event.SHIFT_MASK | 
                                                                   XMLWrapper.ControlKey));

        if (!XMLWrapper.isMac) {
            menuFile.add(new JSeparator());
            menuExit = 
                    menuFile.add(new JMenuItem(LocalMessage.get("label:exit")));
            menuExit.addActionListener(this);
            menuExit.setAccelerator(KeyStroke.getKeyStroke('Q', 
                                                           Event.CTRL_MASK));
        }

        menuGo = menuBar.add(new JMenu(LocalMessage.get("menu:show")));
        
        menuWindow = menuBar.add(new JMenu(LocalMessage.get("menu:window")));
        menuZoom = 
                menuWindow.add(new JMenuItem(LocalMessage.get("button:zoom"), 
                                             LocalIcon.get("zoom.gif")));
        menuZoom.addActionListener(this);
        menuZoom.setAccelerator(KeyStroke.getKeyStroke('M', XMLWrapper.ControlKey));
        menuBottomZoom = 
                menuWindow.add(new JMenuItem(LocalMessage.get("button:bottomzoom"), 
                                             LocalIcon.get("zoominv.gif")));
        menuBottomZoom.addActionListener(this);
        menuBottomZoom.setAccelerator(KeyStroke.getKeyStroke('M', 
                                                             Event.SHIFT_MASK | 
                                                             XMLWrapper.ControlKey));
        menuNewWindow = 
                menuWindow.add(new JMenuItem(LocalMessage.get("button:newwindow"), 
                                             LocalIcon.get("window.gif")));
        menuNewWindow.addActionListener(this);
        menuNewWindow.setAccelerator(KeyStroke.getKeyStroke('N', 
                                                            Event.SHIFT_MASK | 
                                                            XMLWrapper.ControlKey));

        menuWindow.add(new JSeparator());
        menuShowConnexions = 
                menuWindow.add(new JMenuItem(LocalMessage.get("button:showconnexions")));
        menuShowConnexions.addActionListener(this);

        JMenu menuHelp = menuBar.add(new JMenu(LocalMessage.get("menu:help")));
        menuWeb = menuHelp.add(new JMenuItem(LocalMessage.get("menu:web")));
        menuWeb.addActionListener(this);
        menuDevWeb = 
                menuHelp.add(new JMenuItem(LocalMessage.get("menu:devweb")));
        menuDevWeb.addActionListener(this);
        menuBug = menuHelp.add(new JMenuItem(LocalMessage.get("menu:bug")));
        menuBug.addActionListener(this);
        menuHelp.add(new JSeparator());
        menuLicense = 
                menuHelp.add(new JMenuItem(LocalMessage.get("menu:license")));
        menuLicense.addActionListener(this);
    }

    /** Manages actions performed by buttons and menu items. */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof ButtonToggleFactory) {
            fireButton((ButtonToggleFactory)e.getSource());
        } else if (e.getSource() instanceof JMenuItem) {
            JMenuItem source = (JMenuItem)e.getSource();

            if (source == menuNew1) {
                fireButton(buttonNew1);
            } else if (source == menuNew2) {
                fireButton(buttonNew2);
            } else if (source == menuNew3) {
                fireButton(buttonNew3);
            } else if (source == menuClose) {
                fireButton(buttonClose);
            } else if (source == menuEdit) {
                fireButton(buttonEdit);
            } else if (source == menuDelete) {
                fireButton(buttonDelete);
            } else if (source == menuSave) {
                fireButton(buttonSave);
            } else if (source == menuPrint) {
                fireButton(buttonPrint);
            } else if (source == menuPrintDiagram) {
                fireButton(buttonPrintDiagram);
            } else if (source == menuNewWindow) {
                fireButton(buttonNewWindow);
            } else if (source == menuWeb) {
                web();
            } else if (source == menuDevWeb) {
                devWeb();
            } else if (source == menuLicense) {
                showLicense();
            } else if (source == menuBug) {
                bug();
            } else if (source == menuExit) {
                saveSession();
                disconnectServer();
                connection.detach(this);
                if (--windows == 0)
                    System.exit(0);
            } else if (source == menuZoom) {
                zoom();
            } else if (source == menuBottomZoom) {
                bottomZoom();
            } else if (source == menuBackupItem) {
                backupFile(false, false);
            } else if (source == menuBackupAll) {
                backupFile(true, false);
            } else if (source == menuBackupDemo) {
                backupFile(true, true);
            } else if (source == menuRestore) {
                restoreFile();
            } else if (source == menuImport) {
                importFile();
            } else if (source == menuConfigureServer) {
                configureServer();
            } else if (source == menuConnectServer) {
                connectServer();
            } else if (source == menuShowConnexions) {
                showConnexions();
            } else if (source == menuDisconnectServer) {
                disconnectServer();
            }
        }
    }

    /** Refreshs menu items based on action bar. */
    protected void refreshMenus() {
        menuNew1.setText(LocalMessage.get("menu:new") + " " + 
                         buttonNew1.getText());
        menuNew1.setVisible(buttonNew1.isVisible());
        menuNew1.setEnabled(buttonNew1.isEnabled());
        menuNew2.setText(LocalMessage.get("menu:new") + " " + 
                         buttonNew2.getText());
        menuNew2.setVisible(buttonNew2.isVisible());
        menuNew2.setEnabled(buttonNew1.isEnabled());
        menuNew3.setText(LocalMessage.get("menu:new") + " " + 
                         buttonNew3.getText());
        menuNew3.setVisible(buttonNew3.isVisible());
        menuNew3.setEnabled(buttonNew1.isEnabled());
        menuNewExtra1.setEnabled(buttonNew1.isEnabled());
        menuNewExtra2.setEnabled(buttonNew1.isEnabled());
        menuNewExtra3.setEnabled(buttonNew1.isEnabled());

        menuRestore.setEnabled(buttonNew1.isEnabled() && 
                               !connection.isConnected());
        menuImport.setEnabled(buttonNew1.isEnabled() && 
                              !connection.isConnected());
        menuBackupItem.setEnabled(buttonNew1.isEnabled() && 
                              !connection.isConnected() && 
                              buttonEdit.isVisible() && 
                              buttonEdit.isEnabled());
        menuBackupAll.setEnabled(buttonNew1.isEnabled() && 
                              !connection.isConnected());
        menuBackupDemo.setEnabled(buttonNew1.isEnabled() && 
                                  !connection.isConnected());

        for(ToggleButtonCategory button: buttons) 
            button.refreshMenu();

        menuConnectServer.setEnabled(!connection.isConnected());
        menuDisconnectServer.setEnabled(connection.isConnected());
        menuConfigureServer.setEnabled(!connection.isConnected());

        menuClose.setText(buttonClose.getText());
        menuClose.setEnabled(buttonClose.isVisible() && 
                             buttonClose.isEnabled());
        menuEdit.setEnabled(buttonEdit.isVisible() && buttonEdit.isEnabled());
        menuDelete.setEnabled(buttonDelete.isVisible() && 
                              buttonDelete.isEnabled());
        menuSave.setEnabled(buttonSave.isVisible() && buttonSave.isEnabled());
        menuPrint.setEnabled(buttonPrint.isVisible() && 
                             buttonPrint.isEnabled());
        menuPrintDiagram.setEnabled(buttonPrint.isVisible() && 
                                    buttonPrint.isEnabled());

        removeListeners(menuNewExtra1);
        removeListeners(menuNewExtra2);
        removeListeners(menuNewExtra3);
        menuNewExtra1.setVisible(false);
        menuNewExtra2.setVisible(false);
        menuNewExtra3.setVisible(false);

        if (currentPanel != null)
            currentPanel.refreshMenus();
    }

    /** Opens a new window. */
    protected void newWindow() {
        FrameMain frame = null;

        try {
            frame = new FrameMain(new FactoryConnection());
            frame.setLocationRelativeTo(null);
            frame.setLocation(getX() + (16 * windows), 
                              getY() + (16 * windows));
            frame.setSize(getSize());
            connection.attach(frame);
        } catch (Exception ex) {
            addMessage(ex);
        } finally {
            if (frame != null)
                frame.setVisible(true);
        }
    }

    /** Removes listeners from extra menu items in order to
	  * prevent inapropriate actions when labels are changed. */
    private void removeListeners(JMenuItem item) {
        ActionListener[] listeners = item.getActionListeners();

        for (int i = 0; i < listeners.length; i++)
            item.removeActionListener(listeners[i]);
    }

    /** Manages menu items. */
    public void fireButton(ButtonToggleFactory source) {
        setCursor(true);
        if (source == buttonNew1 || 
                source == buttonNew2 || 
                source == buttonNew3) {
            actionNew(source);
            setCursor(false);
            return;
        } else if (source == buttonSave) {
            try {
                FactoryWriterXML query;
                if (buttonEdit.isSelected())
                    query = new FactoryWriterXML("query:update");
                else if (buttonDelete.isSelected())
                    query = new FactoryWriterXML("query:delete");
                else
                    query = new FactoryWriterXML("query:new");
                getCurrent().xmlOut(query, true);
                FactoryWriterXML answer = new FactoryWriterXML();
                querySession(query, answer);
                if (!inError) {
                    panelSearch.runLookupAndWait();
                    setSaveRevertVisible(false, true);
                    new ImportListXML(null, panelSearch).xmlIn(answer, null, false);
					getCurrent().setEnabled(false);
                    setActionVisible(getCurrent() != panelBlank);
                    resetAction();
                } else {
                    addMessageDictionary("WAR", "warning:notsaved");
                    source.setSelected(false);
                    setCursor(false);
                    return;
                }
            } catch (Exception ex) {
                addMessage(ex);
            }
        } else if (source == buttonEdit) {
            getCurrent().setEnabled(true);
            setSaveRevertVisible(true, false);
            getCurrent().setFocus();
        } else if (source == buttonDelete) {
            getCurrent().setEnabled(false);
            setSaveRevertVisible(true, false);
            clearCalendarMessages();
            reloadCalendar();
            setTitle(getFrameTitle());
        } else if (source == buttonClose) {
            if (buttonNew1.isSelected() || buttonNew2.isSelected() || 
                buttonNew3.isSelected() || buttonEdit.isSelected() || 
                buttonDelete.isSelected()) {
                clearCalendarMessages();
                reloadCalendar();
                setTitle(getFrameTitle());

                if (buttonEdit.isSelected() || buttonDelete.isSelected()) {
                    panelSearch.reselectTree();
                    setSaveRevertVisible(false, true);
                } else {
                    setActionVisible(false);
                    resetSaveRevert();
                }
                getCurrent().setEnabled(false);
                buttonClose.requestFocusInWindow();
                resetAction();
                clearNew();
                setCursor(false);
                return;
            } else {
                panelSearch.unselectTree();
                setActionVisible(false);
                resetSaveRevert();
                source.setSelected(false);
                clearCalendarMessages();
                reloadCalendar();
                setTitle(getFrameTitle());
                setCursor(false);
                return;
            }
        } else if (source == buttonPrint || source == buttonPrintDiagram) {
            try {
                FactoryWriterXML err = new FactoryWriterXML();
                getCurrent().print(err, source == buttonPrintDiagram, false);
                new ImportErrXML(this).xmlErrorIn(err);
            } catch (Exception ex) {
                addMessage(ex);
            } finally {
                source.setSelected(false);
                setCursor(false);
            }
            return;
        } else if (source == buttonNewWindow) {
            saveSession();
            newWindow();
            source.setSelected(false);
            setCursor(false);
            return;
        } else if (source == buttonZoom) {
            zoom();
            setCursor(false);
            return;
        } else if (source == buttonBottomZoom) {
            bottomZoom();
            setCursor(false);
            return;
        }
        clearNew();
        source.setSelected(true);
        setCursor(false);
    }

    /** Reads preferences (from server). */
    protected void readPreferences() {
        try {
            FactoryWriterXML query = new FactoryWriterXML("query:get");
            query.xmlStart("preference");
            query.xmlEnd();
            FactoryWriterXML answer = new FactoryWriterXML();
            querySession(query, answer, true);
            new ImportPreferenceXML(FrameMain.this).xmlIn(answer, null, false);
        } catch (Exception e) {
            addMessage(e);
        }
        // Preference defaults
        if (count.getText().equals("1")) {
            setLocation(64, 64);
			comboDisplayOptions.setSelectedCode("1");
            comboDisplayPeriod.setSelectedCode("1");
        }
        comboDisplayActors.setSelectedCode("1");
    }

    /** Activates with appropriate labels the 'new' buttons on the action bar. */
    private void activateNewButtons() {
        for(ToggleButtonCategory button: buttons) {
            if(button.getCategory().equals(selectionCategory)) {
                resetButtons(button.getAssociatedActions());
                break;
            }
        }
        refreshMenus();
    }

    /** Adds a new data panel in the workbench. */
    public void addPanel(PanelData panel, String title) throws Exception {
		synchronized (listPanels) {
			if (listPanels.get(title) == null) {
				panel.init(title);
				stackPanels.add(panel, title);
				listPanels.put(title, panel);
			}
		}
    }

    /** Activates the data panel to be displayed in the workbench. */
    public void showPanel(String title) {
        clearCalendarMessages();
        reloadCalendar();

        if (currentPanel != null)
            currentPanel.setButtonsVisible(false);

        layoutData.show(stackPanels, title);
        currentPanel = listPanels.get(title);

        if (currentPanel == null || 
            !currentPanel.getTagpanel().equals(title)) {
            try {
                while (!threadLoadPanels.isTerminated()) {
                    currentPanel = listPanels.get(title);
                    if (currentPanel != null)
                        break;

                    Thread.sleep(40);
                }
            } catch (InterruptedException ex) {
                return;
            }

            layoutData.show(stackPanels, title);
            currentPanel = listPanels.get(title);

            if (currentPanel == null || 
                !currentPanel.getTagpanel().equals(title)) {
                addMessage("FAT", "Incorrect panel : " + title);
                return;
            }
        } else
            currentPanel.clean();

        if (currentPanel != null)
            currentPanel.setButtonsVisible(true);

        return;
    }

    /** Responses to a window manager event.
     *  The session is closed when all the windows are closed. */
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            disconnectServer();
            connection.detach(this);
            if (--windows == 0)
                System.exit(0);
        }
    }

    /** Resets all the buttons in the tool bar in a standard mode. */
    private void resetButtons() {
        buttonNew1.setSelected(false);
        buttonNew2.setSelected(false);
        buttonNew3.setSelected(false);
        buttonClose.setSelected(false);
        buttonEdit.setSelected(false);
        buttonPrint.setSelected(false);
        buttonDelete.setSelected(false);
        buttonSave.setSelected(false);
        buttonNew1.setVisible(false);
        buttonNew2.setVisible(false);
        buttonNew3.setVisible(false);
        refreshMenus();
    }

    /** Activates three 'new' buttons on the tool bar. */
    private void resetButtons(List<ToggleButtonAction> actions) {
        resetButtons();
        int no = 1;
        for(ToggleButtonAction action: actions) {
            ButtonToggleFactory button;
            switch(no) {
                case 1: button = buttonNew1; break;
                case 2: button = buttonNew2; break;
                default: button = buttonNew3; break;
            }
            button.setText(LocalMessage.get(action.button));
            button.setToolTipText(LocalMessage.get(action.button + ":tip"));
            button.setVisible(true);
            button.setAssociatedPanel(action.panel);
            ++no;
        }
    }

    /** De-selects all the 'new' buttons. */
    private void clearNew() {
        buttonNew1.setSelected(false);
        buttonNew2.setSelected(false);
        buttonNew3.setSelected(false);
        refreshMenus();
    }

    /** Adds an object to the action bar. */
    public void addActionBarButton(ButtonToggleFactory button) {
        commandBar.add(button, 3);
    }

    /** Locks or unlocks the workbench during editing. */
    private void lockScreen(boolean lock) {
        panelSearch.lock(lock);

        for (int i = 0; i < actionBar.getComponentCount(); i++) {
            Object component = actionBar.getComponentAtIndex(i);
            if (component.getClass().toString().indexOf("ToggleButtonCategory") >= 
                0) {
                ((ToggleButtonCategory)component).setEnabled(lock);
            }
        }
        for (int i = 0; i < commandBar.getComponentCount(); i++) {
            Object component = commandBar.getComponentAtIndex(i);
            if (component.getClass().toString().indexOf("ButtonToggleFactory") >= 
                0) {
                ButtonToggleFactory button = (ButtonToggleFactory)component;
                if (button != buttonSave && button != buttonZoom && 
                    button != buttonBottomZoom && button != buttonClose && 
                    button != buttonNewWindow) {
                    button.setEnabled(lock);
                }
            }
        }
        refreshMenus();
    }

    /** Shows or hides the save and revert buttons. */
    private void setSaveRevertVisible(boolean visible, boolean lock) {
        lockScreen(lock);
        buttonSave.setSelected(false);
        buttonClose.setSelected(false);
        separatorSave.setVisible(visible && 
                                getCurrent() != panelBlank);
        buttonSave.setVisible(visible && 
                                getCurrent() != panelBlank && 
                                getCurrent().canSave());
        buttonClose.setText(LocalMessage.get(
									visible && getCurrent() != panelBlank ? 
									"button:cancel" : 
									"button:close"));
        refreshMenus();
    }

    /** Hides the save and revert buttons and hides the current data panel. */
    private void resetSaveRevert() {
        setSaveRevertVisible(false, true);
        showPanel("blank");
    }

    /** De-selects the close, edit and delete buttons. */
    private void resetAction() {
        buttonClose.setSelected(false);
        buttonEdit.setSelected(false);
        buttonPrint.setSelected(false);
        buttonDelete.setSelected(false);
        refreshMenus();
    }

    /** Shows or hides the close, edit and delete buttons. */
    public void setActionVisible(boolean visible) {
        separatorEdit.setVisible(visible && getCurrent() != panelBlank);
        buttonClose.setVisible(visible && getCurrent() != panelBlank);
        buttonEdit.setVisible(visible && getCurrent() != panelBlank && getCurrent().canEdit());
        buttonDelete.setVisible(visible && getCurrent() != panelBlank && getCurrent().canDelete());
        buttonPrint.setVisible(visible && getCurrent() != panelBlank);
        refreshMenus();
    }

    /** Sends a request to the server, and interprets the answer. */
    public void querySession(FactoryWriterXML query, FactoryWriterXML answer, 
                             boolean forceLocal) {
        if (query == null)
            return;

        try {
            addMessage("TRC", "==> " + query.toString());
            if (forceLocal)
                connection.queryLocal(query, answer);
            else
                connection.query(query, answer);
            addMessage("TRC", "<== " + answer.getOutWriter().toString());
            inError = addMessage(answer);
        } catch (Exception ex) {
            addMessage(ex);
            inError = true;
        } finally {
            setServerName();
            refreshMenus();
        }
    }

    /** Sends a request to the server, and interprets the answer. */
    public void querySession(FactoryWriterXML query, FactoryWriterXML answer) {
        querySession(query, answer, false);
    }

    /** Interprets the message stream and display messages. */
    public boolean addMessage(FactoryWriterXML answer) {
        try {
            ImportErrXML xml = new ImportErrXML(this);
            xml.xmlErrorIn(answer);
            return xml.isInError();
        } catch (Exception ex) {
            addMessage(ex);
            return true;
        }
    }

    /** Saves session parameters in preference file. */
    public void saveSession() {
        saveLookupParams();
        FactoryWriterXML query = new FactoryWriterXML("query:update");

        query.xmlStart("preference");
        query.xmlOut("posx", getX());
        query.xmlOut("posy", getY());
        query.xmlOut("lenx", getWidth());
        query.xmlOut("leny", getHeight());
        query.xmlOut("poslookup", 
                     (zoomed || bottomZoomed ? posSplitX : splitVertical.getDividerLocation()));
        query.xmlOut("posmessages", 
                     (zoomed || bottomZoomed ? posSplitY : splitHorizontal.getDividerLocation()));
        query.xmlOut("selectioncategory", selectionCategory);
        query.xmlOut("displayoptions", comboDisplayOptions.getSelectedCode());
        query.xmlOut("displayperiod", comboDisplayPeriod.getSelectedCode());
        query.xmlOut("count", count.getText());

        for (int i = 0; i < actionBar.getComponentCount(); i++) {
            Object component = actionBar.getComponentAtIndex(i);
            if (component.getClass().toString().indexOf("ToggleButtonCategory") >= 
                0)
                ((ToggleButtonCategory)component).xmlOut(query);
        }
        query.xmlEnd();
        FactoryWriterXML answer = new FactoryWriterXML();

        try {
            querySession(query, answer, true);
        } catch (Exception e) {
            addMessage(e);
        }

        query = new FactoryWriterXML("query:saveall");
        answer = new FactoryWriterXML();
        try {
            querySession(query, answer, true);
        } catch (Exception e) {
            addMessage(e);
        }
    }

    /** Returns the current activated data panel in the workbench. */
    public PanelData getCurrent() {
        return currentPanel == null ? panelBlank : currentPanel;
    }

    /** Selects or de-select the buttons in the tools bar
	 *  based on the selected category. */
    public void resetCategories() {
        if(selectionCategory != null) {
            for (int i = 0; i < actionBar.getComponentCount(); i++) {
                Object component = actionBar.getComponentAtIndex(i);
                if (component.getClass().toString().indexOf("ToggleButtonCategory") >= 
                    0)
                    ((ToggleButtonCategory)component).reset();
            }
        }
    }

    /** Saves the properties attached to the buttons in the tools bar. */
    public void saveLookupParams() {
        for (int i = 0; i < actionBar.getComponentCount(); i++) {
            Object component = actionBar.getComponentAtIndex(i);
            if (component.getClass().toString().indexOf("ToggleButtonCategory") >= 
                0)
                ((ToggleButtonCategory)component).saveLookupParams();
        }
    }

    /** Retrieves the properties attached to the buttons in the tools bar. */
    public void getLookupParams() {
        for (int i = 0; i < actionBar.getComponentCount(); i++) {
            Object component = actionBar.getComponentAtIndex(i);
            if (component.getClass().toString().indexOf("ToggleButtonCategory") >= 
                0)
                ((ToggleButtonCategory)component).getLookupParams();
        }
    }

    /** Sets the properties to the right button in the tools bar
	 *  based on the category. */
    public void setLookupParams(String category, String filter, 
                                String search) {
        for (int i = 0; i < actionBar.getComponentCount(); i++) {
            Object component = actionBar.getComponentAtIndex(i);
            if (component.getClass().toString().indexOf("ToggleButtonCategory") >= 
                0)
                ((ToggleButtonCategory)component).setLookupParams(category, 
                                                                  filter, 
                                                                  search);
        }
    }

    /** Sends an exception message to the message log/panel. */
    public final void addMessage(Exception ex) {
        addMessage("FAT", ex.toString());
        StackTraceElement[] stack = ex.getStackTrace();
        for (int i = 0; i < stack.length; i++)
            addMessage("FAT", stack[i].toString() + "-" + getBuild());
        addMessageDictionary("WAR", "instruction:exception1");
        addMessageDictionary("WAR", "instruction:exception2");
    }

    /** Sends a message based on dictionary to the message log/panel. */
    public final void addMessageDictionary(String category, String label, 
                                           String... args) {
        addMessage(category, LocalMessage.get(label, args));
    }

    /** Sends a message to the message log/panel. */
    public final void addMessage(String category, String text) {
        String nText = XMLWrapper.wrapHTML(
						XMLWrapper.unwrapEmbeddedDate(
							XMLWrapper.replaceAll(text, "\n", "")));

        if (category.equals("ERR") || category.equals("FAT") || 
            category.equals("WAR"))
            nText = "<b>" + nText + "</b>";

        if (category.equals("ERR") || category.equals("FAT"))
            nText = "<font color=red>" + nText + "</font>";

        if (category.equals("TRC"))
            nText = "<font color=gray>" + nText + "</font>";

        if (category.equals("SRV"))
            nText = "<font color=blue>" + nText + "</font>";

        if (category.equals("MSG"))
            nText = "<font color=black>" + nText + "</font>";

        synchronized (treeMessages) {
            if (messages.size() > 99)
                messages.remove(0);

            messages.add(nText);

            if (!category.equals("TRC")) {
                if (treeMessages.getRowCount() > 59)
                    treeNodeMessages.remove(0);

                DefaultMutableTreeNode selectionNode = 
                    new DefaultMutableTreeNode(new MessageTreeNode(nText));
                treeNodeMessages.add(selectionNode);
                treeModelMessages.reload();
                TreePath selectionPath = 
                    treeMessages.getPathForRow(treeMessages.getRowCount() - 1);
                treeMessages.scrollPathToVisible(selectionPath);
            }

            if (category.equals("ERR") || category.equals("FAT") || 
                category.equals("WAR")) {
                if (zoomed)
                    zoom();
                lowerTabs.setSelectedIndex(0);
            }
        }
    }

    /** Returns the list of messages as a long string. */
    public String getMessages() {
        String messageString = "";
        for (int i = 0; i < messages.size(); i++) {
            if (i > 0)
                messageString += "\n";
            messageString += messages.get(i);
        }
        return messageString;
    }

    /** Zooms the main data panel (hide search panel and messages). */
    private void zoom() {
        if (bottomZoomed)
            bottomZoom();
        if (!zoomed) {
            posSplitX = splitVertical.getDividerLocation();
            posSplitY = splitHorizontal.getDividerLocation();
            panelSearch.setVisible(false);
            lowerTabs.setVisible(false);
            splitVertical.setDividerLocation(0);
            splitHorizontal.setDividerLocation(splitHorizontal.getHeight());
            splitVertical.setDividerSize(0);
            splitHorizontal.setDividerSize(0);
        } else {
            panelSearch.setVisible(true);
            lowerTabs.setVisible(true);
            splitVertical.setDividerLocation(posSplitX);
            splitHorizontal.setDividerLocation(posSplitY);
            splitVertical.setDividerSize(SPLITSIZE);
            splitHorizontal.setDividerSize(SPLITSIZE);
        }

        zoomed = !zoomed;
        buttonZoom.setSelected(zoomed);
    }

    /** Zooms the bottom panel (hide search panel and data). */
    private void bottomZoom() {
        if (zoomed)
            zoom();
        if (!bottomZoomed) {
            posSplitX = splitVertical.getDividerLocation();
            posSplitY = splitHorizontal.getDividerLocation();
            panelSearch.setVisible(false);
            panelWorkbench.setVisible(false);
            splitVertical.setDividerLocation(0);
            splitHorizontal.setDividerLocation(splitHorizontal.getHeight());
            splitVertical.setDividerSize(0);
            splitHorizontal.setDividerSize(0);
        } else {
            panelSearch.setVisible(true);
            panelWorkbench.setVisible(true);
            splitVertical.setDividerLocation(posSplitX);
            splitHorizontal.setDividerLocation(posSplitY);
            splitVertical.setDividerSize(SPLITSIZE);
            splitHorizontal.setDividerSize(SPLITSIZE);
        }

        bottomZoomed = !bottomZoomed;
        buttonBottomZoom.setSelected(bottomZoomed);
    }

    /** Changes category : reponse to a click on a category button. */
    public void actionChangeCategory(ToggleButtonCategory button) {
        setTitle(getFrameTitle());
        if (zoomed)
            zoom();
        if (bottomZoomed)
            bottomZoom();

        blockLookup = true;
        saveLookupParams();
        selectionCategory = button != null ? button.getCategory() : "";
        resetCategories();
        if (button != null) {
            panelSearch.setFilter(selectionCategory, button.getFilter());
            panelSearch.setTextSearch(button.getSearch());
        }
        activateNewButtons();
        blockLookup = false;
        panelSearch.runLookup();
        clearCalendarMessages();
        reloadCalendar();
        panelSearch.setFocus();
    }

    /** Assigns a request string to be used when the 'default' query is sent to
     *  the server. */
    public void addDefaultValue(String tag, String value) {
        defaultValues.add(new DefaultValue(tag, value));
    }

    /** Appends the fault values to the output stream. */
    public void xmlOutDefaultValues(FactoryWriterXML xml) {
        for (DefaultValue value: defaultValues)
            value.xmlOut(xml);
        defaultValues.clear();
    }

    /** Action on a 'new' button when pressed from the tool bar. */
    private void actionNew(ButtonToggleFactory source) {
        actionNew(source, "");
    }

    /** Action on a 'new' button when pressed from a data panel. */
    public void actionNew(String panelName) {
        actionNew(null, panelName);
    }

    /** Triggers the window to a 'new' or 'create' mode
	 *  for a new element to be keyed. */
    protected void actionNew(ButtonToggleFactory source, String panelName) {
    
        if (source == buttonNew1 || 
                source == buttonNew2 || 
                source == buttonNew3) {
            showPanel(source.getAssociatedPanel().toLowerCase());
        } else if (panelName.length() > 0)
            showPanel(panelName);

        if (getCurrent() != panelBlank) {
            try {
                FactoryWriterXML query = new FactoryWriterXML("query:default");
                getCurrent().xmlOut(query, false);
                FactoryWriterXML answer = new FactoryWriterXML();
                querySession(query, answer);
                new ImportDataXML(this).xmlIn(answer, null, false);
                query = null;
            } catch (Exception ex) {
                addMessage(ex);
            }

            getCurrent().setEnabled(true);
            setSaveRevertVisible(true, false);
            panelSearch.unselectTree();
            buttonClose.setSelected(false);
            buttonEdit.setSelected(false);
            buttonPrint.setSelected(false);
            buttonDelete.setSelected(false);
            separatorEdit.setVisible(getCurrent() != panelBlank);
            buttonClose.setVisible(getCurrent() != panelBlank);
            separatorSave.setVisible(getCurrent() != panelBlank);
            getCurrent().setFocus();
        }

        clearNew();
    }

    /** Adds a message to the calendar. */
    public void addCalendarMessage(Date dateMessage, String message, 
                                   String label, String who, int type, 
                                   int duration, int durationType, 
                                   int complete) {
        if ((type != 7) && (type != 8) && (type != 9)) {
            if (type != 10)
                calendarPanel.addCalendarMessage(dateMessage, message);

            if (label.length() > 0)
                diagramPanel.addCalendarMessage(dateMessage, label, who, type, 
                                                duration, durationType, 
                                                complete);
            else
                diagramPanel.addCalendarMessage(dateMessage, message, "", type, 
                                                duration, durationType, 
                                                complete);
        }
    }

    /** Rebuilds calendar. */
    public void reloadCalendar() {
        calendarPanel.runLookup();
        diagramPanel.runLookup();
    }

    /** Removes messages loaded in calendar. */
    public void clearCalendarMessages() {
        calendarPanel.clearCalendarMessages();
        diagramPanel.clearCalendarMessages();
    }

    /** Changes the cursor. */
    public void setCursor(boolean wait) {
        setCursor(wait ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : 
                  Cursor.getDefaultCursor());
    }

    /** Displays a dialog for open or save files. */
    private String showDialog(int mode, String path) {
        FileFilter gzip = new FactoryFileFilter("gzip", "XML/GZIP");
        FileFilter xml = new FactoryFileFilter("xml", "XML");
        JFileChooser chooser = new JFileChooser(path);
        chooser.setDialogType(mode);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.resetChoosableFileFilters();
        chooser.addChoosableFileFilter(xml);
        chooser.addChoosableFileFilter(gzip);

        if (chooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
            FileFilter filter = chooser.getFileFilter();
            filter.getDescription();
            String filename = chooser.getSelectedFile().getPath();
            if (filter == gzip && !filename.endsWith(".gzip"))
                filename += ".gzip";
            if (filter == xml && !filename.endsWith(".xml"))
                filename += ".xml";
            return filename;
        }
        return "";
    }

    /** Create a full file backup of database. */
    private void backupFile(boolean all, boolean demo) {
        String filename;
        if (demo)
            filename = XMLWrapper.USERHOME + XMLWrapper.SLASH + "demo.xml";
        else
            filename = 
                    showDialog(JFileChooser.SAVE_DIALOG, 
                                    all ? getPath() + 
                                            XMLWrapper.SLASH + "backups" : 
                                        XMLWrapper.USERHOME);

        if (filename.length() > 0) {
            try {
                setCursor(true);
                FactoryWriterXML query = new FactoryWriterXML("query:backup");
                query.xmlOut("file", filename);
                if (!all && getCurrent() != panelBlank) 
                    query.xmlOut("iid", getCurrent().getIid());
                if (demo)
                    query.xmlOut("demo", "y");
                if(filename.endsWith(".gzip"))
                    query.xmlOut("gzip", "y");
                FactoryWriterXML answer = new FactoryWriterXML();
                querySession(query, answer);
            } catch (Exception e) {
                addMessage(e);
            } finally {
                setCursor(false);
            }
        }
    }

    /** Restore database from file. */
    private void restoreFile() {
        JOptionPane.showMessageDialog(this, 
                                      LocalMessage.get("warning:restore"));
        String filename = 
            showDialog(JFileChooser.OPEN_DIALOG, getPath() + XMLWrapper.SLASH + 
                       "backups");
        if (filename.length() > 0) {
            try {
                setCursor(true);
                FactoryWriterXML query = new FactoryWriterXML("query:restore");
                query.xmlOut("file", filename);
                FactoryWriterXML answer = new FactoryWriterXML();
                querySession(query, answer);
                panelSearch.runLookup();
            } catch (Exception e) {
                addMessage(e);
            } finally {
                setCursor(false);
            }
        }
    }

    /** Import data from file to database. */
    private void importFile() {
        JOptionPane.showMessageDialog(this, 
                                      LocalMessage.get("warning:import"));
        String filename = 
            showDialog(JFileChooser.OPEN_DIALOG, getPath() + XMLWrapper.SLASH + 
                       "backups");
        if (filename.length() > 0) {
            try {
                setCursor(true);
                FactoryWriterXML query = new FactoryWriterXML("query:import");
                query.xmlOut("file", filename);
                FactoryWriterXML answer = new FactoryWriterXML();
                querySession(query, answer);
                panelSearch.runLookup();
            } catch (Exception e) {
                addMessage(e);
            } finally {
                setCursor(false);
            }
        }
    }

    /** Hides action bar. */
    public void hideActionBar() {
        actionBar.setVisible(false);
    }

    /** Returns diagram image for the period. */
    public BufferedImage getDiagram(int i) {
        return diagramPanel.getImage(i);
    }

    /** Returns the number of periods defined in the diagram. */
    public int getPeriods() {
        return diagramPanel.getPeriods();
    }

    /** Removes temporary files. */
    private void clearTmp() {
        String path = XMLWrapper.USERHOME;

        if (!path.endsWith(XMLWrapper.SLASH)) 
            path = path + XMLWrapper.SLASH;

        File dir = new File(path);
        String[] files = dir.list();

        for (int i = 0; i < files.length; i++) {
            if (files[i].startsWith("factory.report")) {
                File file = new File(path + files[i]);
                file.deleteOnExit();
            }
        }
    }

    /** Goes to the product web site. */
    private void web() {
        addMessageDictionary("MSG", "message:openurl", getHomePage());

        if (!BrowserControl.displayURL(getHomePage()))
            addMessageDictionary("ERR", "error:badbrowser");
    }

    /** Goes to the development home page. */
    private void devWeb() {
        addMessageDictionary("MSG", "message:openurl", getDevPage());

        if (!BrowserControl.displayURL(getDevPage()))
            addMessageDictionary("ERR", "error:badbrowser");
    }


    /** Shows the license. */
    private void showLicense() {
        try {
            setCursor(true);
            panelSearch.unselectTree();
            FactoryWriterXML query = 
                new FactoryWriterXML("query:about");
            FactoryWriterXML answer = new FactoryWriterXML();
            querySession(query, answer);
            new ImportDataXML(this).xmlIn(answer, null, false);
        } catch (Exception ex) {
            addMessage(ex);
        } finally {
            if (getCurrent() != panelBlank) {
                getCurrent().setEnabled(false);
                setActionVisible(true);
                refreshMenus();
            }
            setCursor(false);
        }
    }

    /** Submits bug information to developers. */
    private void bug() {
        try {
            setCursor(true);
            FactoryWriterXML err = new FactoryWriterXML();
            getCurrent().print(err, false, true);
            new ImportErrXML(this).xmlErrorIn(err);
        } catch (Exception ex) {
            addMessage(ex);
        } finally {
            setCursor(false);
        }
    }

    /** Server configuration. */
    void configureServer() {
        try {
            setCursor(true);
            panelSearch.unselectTree();
            FactoryWriterXML query = new FactoryWriterXML("query:get");
            query.xmlStart("server").xmlAttribute("name", "localhost").xmlEnd();
            FactoryWriterXML answer = new FactoryWriterXML();
            querySession(query, answer);
            new ImportDataXML(this).xmlIn(answer, null, false);
        } catch (Exception ex) {
            addMessage(ex);
            return;
        } finally {
            if (getCurrent() != panelBlank) {
                getCurrent().setEnabled(false);
                setActionVisible(true);
                refreshMenus();
            }
            setCursor(false);
        }
    }

    /** Proposes a connection to a server. */
    void connectServer() {
        saveSession();
        ImportListXML importList = null;
        showPanel("blank");
        try {
            setCursor(true);
            FactoryWriterXML query = new FactoryWriterXML("query:list");
            query.xmlOut("class", "server");
            query.xmlOut("getoperatorid", "y");
            query.xmlOut("getoperatorname", "y");
            FactoryWriterXML answer = new FactoryWriterXML();
            querySession(query, answer);
            importList = new ImportListXML(null, null);
            importList.xmlIn(answer, null, false);
        } catch (Exception ex) {
            addMessage(ex);
            return;
        } finally {
            setCursor(false);
        }

        if (importList != null) {
            int size = importList.getNamesCount();
            if (size == 0) {
                addMessageDictionary("MSG", "message:noserver");
                return;
            }
            String[] servers = new String[size];
            for (int i = 0; i < size; i++)
                servers[i] = importList.getName(i);

            serverName = 
                    (String)JOptionPane.showInputDialog(this, LocalMessage.get("instruction:chooseserver") + 
                                                        "\n\n", 
                                                        getFrameTitle(), 
                                                        JOptionPane.QUESTION_MESSAGE, 
                                                        null, servers, 
                                                        servers[0]);

            if (serverName != null && serverName.length() > 0) {
                setCursor(true);
                saveLookupParams();
                connection.connect(this, serverName);
                try {
                    if (connection.isConnected()) {
                        FactoryWriterXML query = new FactoryWriterXML("query:connect");
                        query.xmlOut("networkid", user);
                        query.xmlOut("build", getBuild());
                        query.xmlOut("os", 
                                     System.getProperty("os.name") + " (" + 
                                     System.getProperty("os.arch") + ")");
                        query.xmlOut("operatorid", importList.getOperatorId());
                        query.xmlOut("operatorname", importList.getOperatorName());
                        query.xmlOut("localdate", XMLWrapper.dfUS.format(new Date()).toString());
                        FactoryWriterXML answer = new FactoryWriterXML();
                        querySession(query, answer);
                    }
                } catch (Exception ex) {
                    addMessage(ex);
                    return;
                } finally {
                    setServerName();
                    initializeCommands();
                    readPreferences();
                    resetCategories();
                    panelSearch.runLookup();
                    refreshMenus();
                    setCursor(false);
                }
            } else
                serverName = "";
                
        }
    }

    /** Assigns a server name. */
    private void setServerName() {
        if (!connection.isConnected())
            serverName = "";

        panelSearch.setServerName((processing ? 
                                    LocalMessage.get("label:processing") : 
                                    serverName) +
                                    (serverName.length() > 0 && countString.length() > 0 ?
                                    " - " : "") + 
                                    countString 
                                    );

        panelSearch.setServerColor(processing ? 
                                    Color.RED : 
                                    serverName.length() > 0 ? 
                                        Color.BLUE : 
                                        Color.BLACK);

        refreshMenus();
    }

    /** Assigns a server name. */
    public void setServerNameProcessing() {
        processing = true;
        countString = "";
        setServerName();
    }

    /** Assigns a server name. */
    public void setServerNameNoProcessing(String countString) {
        processing = false;
        this.countString = countString;
        setServerName();
    }

    /** Disconnect to a server. */
    void disconnectServer() {
        showPanel("blank");
        saveSession();
        if (connection.isConnected()) {
            try {
                setCursor(true);
                saveLookupParams();
                FactoryWriterXML query = new FactoryWriterXML("query:connect");
                query.xmlOut("disconnect", "y");
                FactoryWriterXML answer = new FactoryWriterXML();
                querySession(query, answer);
            } catch (Exception ex) {
                addMessage(ex);
                return;
            } finally {
                connection.disconnect(this);
                setServerName();
                initializeCommands();
                readPreferences();
                resetCategories();
                panelSearch.runLookup();
                refreshMenus();
                setCursor(false);
            }
        }
    }

    /** Displays active connexions. */
    void showConnexions() {
        try {
            setCursor(true);
            FactoryWriterXML query = new FactoryWriterXML("query:connect");
            query.xmlOut("list", "y");
            FactoryWriterXML answer = new FactoryWriterXML();
            querySession(query, answer);
            new ImportDataXML(this).xmlIn(answer, null, false);
        } catch (Exception ex) {
            addMessage(ex);
        } finally {
            if (getCurrent() != panelBlank) {
                setActionVisible(true);
                refreshMenus();
            }
            setCursor(false);
        }
    }

    /**
     * Default value to be used for new elements.
     */
    private class DefaultValue {

        /** Tag. */
        String tag;

        /** Value. */
        String value;

        DefaultValue(String tag, String value) {
            this.tag = tag;
            this.value = value;
        }

        void xmlOut(FactoryWriterXML xml) {
            xml.xmlOut(tag, value);
        }
    }

    /**
     * Message to be displayed by the system.
     */
    private class MessageTreeNode {

        /** Category of the message (error, warning, trace,
		  * fatal or regular message). */
        private String category;

        /** Message text */
        private String text;

        /** Constructor. */
        MessageTreeNode(String text) {
            this.text = text;
        }

        /** Converts the message into a string, using html tags. */
        public String toString() {
            return "<html>" + text + "</html>";
        }

        /** Compares this entity to the specified object. */
        public boolean equals(Object object) {
            if (object == this)
                return true;

            MessageTreeNode node = (MessageTreeNode)object;
            return node.category.equals(category) && node.text.equals(text);
        }
    }

    /**
     * Thread used during shutdown.
     */
    private class ThreadShutDown extends Thread {

        /** The session is saved when the application quits. */
        public void run() {
            try {
                saveSession();
                clearTmp();
                addMessageDictionary("MSG", "message:bye", user);
            } catch (Exception e) {
                addMessage(e);
            }
        }
    }

}
