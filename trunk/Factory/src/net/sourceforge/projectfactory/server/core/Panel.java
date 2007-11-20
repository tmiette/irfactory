/*

Copyright (c) 2006, 2007 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/core/Panel.java,v $
$Revision: 1.21 $
$Date: 2007/02/27 22:12:14 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.core;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.List;

import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Represents a panel definition.
 * @author David Lambert
 */
public class Panel extends PanelBase {

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "panel");

        super.xmlOut(xml, transaction, false);

        if (transaction.isDetail() || transaction.isSave()) {
            for (PanelItem item: items)
                item.xmlOut(xml, transaction, true);

            for (Button item: buttons)
                item.xmlOut(xml, transaction, true);

            for (ButtonParameter item: buttonParameters)
                item.xmlOut(xml, transaction, true);

            if (transaction.isDetail()) {
                generateCode(transaction);
                xmlOut(xml, "generatedcode", generatedcode);
            }
        }

        if (transaction.isExpand() || transaction.isSummary())
            xmlOutExpandPanelCode(xml, transaction);

        if (tags)
            xmlEnd(xml);
    }

    /** Writes associated objects as an XML output. */
    private void xmlOutExpandPanelCode(FactoryWriterXML xml, 
                                       TransactionXML transaction) {
        for (PanelCode panelCode: transaction.getServer().core.panelCodes)
            if (panelCode.packageReference != null && 
                panelCode.packageReference.equals(packageReference) && 
                panelCode.panelName != null && 
                panelCode.panelName.equals(this))
                panelCode.xmlOutSummary(xml, "panelcode");
    }

    /** Validates the object before any save or update. */
    public boolean xmlValidate(FactoryWriterXML xml, 
                               TransactionXML transaction, List list) {
        if (super.xmlValidate(xml, transaction, list))
            return true;


        if (!isActive() || draft || packageReference == null)
            return false;

        if (transaction.getSession().isRemote()) {
            draft = true;
            return false;
        }

        generateCode(transaction);

        /** Write the output into a file. */
        String path = transaction.getServer().getLocalhost().getPath();
        if (path == null || path.length() == 0) {
            draft = true;
            return false;
        }

        String filename = path;
        if (!filename.endsWith(XMLWrapper.SLASH))
            filename = filename + XMLWrapper.SLASH;
        filename = 
                filename + XMLWrapper.replaceAll(packageReference.getName(), ".", 
                                                 XMLWrapper.SLASH) + 
                XMLWrapper.SLASH + getName() + ".java";
        try {
            OutputStreamWriter fileWriter = 
                new OutputStreamWriter(new FileOutputStream(filename), 
                                       "UTF-8");
            BufferedWriter outputFile = new BufferedWriter(fileWriter);
            outputFile.write(generatedcode);
            outputFile.close();
            xmlMessage(xml, "message:savingcomplete", filename);
        } catch (IOException e) {
            xmlError(xml, "error:filenotsaved", filename);
            xml.xmlException(e);
            return true;
        }

        return false;
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);
        canEdit = true;
        canDelete = true;
        canSave = true;
    }

    /** Provides a summary when the object is displayed in a list. */
    public String getSummary() {
        String indicator = "";
        if (draft)
            indicator = "@label:inactive";

        return super.getSummary() + indicator;
    }

    /** Generates java code. */
    private void generateCode(TransactionXML transaction) {
        generatedcode = "";
        if (packageReference == null)
            return;

        out(packageReference.headerFile);
        out("");

        out("package " + packageReference.getName() + ";");
        out("");
        if (buttons.size() > 0)
            out("import java.awt.event.ActionEvent;");
        out("import net.sourceforge.projectfactory.client.FrameMain;");
        out("import net.sourceforge.projectfactory.client.components.*;");
        out("import net.sourceforge.projectfactory.client.panels.PanelData;");
        out("");
        out("/** ");
        if (comment != null) {
            String[] code = comment.split("\n");
            for (String line: code)
                out("  * " + line);
        }
        out("  * @author " + transaction.getSession().getOperatorName());
        out("  */");
        out("public class " + getName() + " extends PanelData {");

        // Declarations
        for (PanelItem item: items) {
            if (item.type.equals("string") && item.xmlTag != null) {
                if (item.comboBox != null) {
                    out("    public ComboBoxCode " + item.xmlTag + 
                        " = new ComboBoxCode(\"" + item.comboBox + "\");");
                } else if (item.lookupClass != null) {
                    out("    public EditBoxLookup " + item.xmlTag + 
                        " = new EditBoxLookup(this, \"" + 
                        item.lookupClass.toLowerCase() + "\");");
                } else {
                    out("    public EditBox " + item.xmlTag + 
                        " = new EditBox();");
                }
            } else if (item.type.equals("boolean") && item.xmlTag != null) {
                out("    public CheckBox " + item.xmlTag + 
                    " = new CheckBox(\"" + item.getLabel() + "\");");
            } else if (item.type.equals("date") && item.xmlTag != null) {
                out("    public EditBoxDate " + item.xmlTag + 
                    " = new EditBoxDate(this);");
            } else if (item.type.equals("text") && item.xmlTag != null) {
                out("    public TextBox " + item.xmlTag + 
                    " = new TextBox();");
            } else if (item.type.equals("label") && item.xmlTag != null) {
                out("    public LabelBox " + item.xmlTag + 
                    " = new LabelBox();");
            } else if (item.type.equals("password") && item.xmlTag != null) {
                out("    public EditBoxPassword " + item.xmlTag + 
                    " = new EditBoxPassword();");
            } else if (item.type.equals("grid") && item.xmlTag != null) {
                Grid grid = item.getGrid(transaction);
                if (grid != null) {
                    if (grid.gotLookup())
                        out("    public TableBoxLookup " + item.xmlTag + 
                            " = new TableBoxLookup(" + grid.getDefinition() + 
                            ");");
                    else
                        out("    public TableBox " + item.xmlTag + 
                            " = new TableBox(" + grid.getDefinition() + ");");
                }
            } else if (item.type.equals("timezone") && item.xmlTag != null) {
                out("    public ComboBoxCodeTimeZone " + item.xmlTag + 
                    " = new ComboBoxCodeTimeZone();");
            } else if (item.type.equals("application") && 
                       item.xmlTag != null) {
                out("    public ComboBoxCodeApplication " + item.xmlTag + 
                    " = new ComboBoxCodeApplication(this);");
            }
        }

        // Buttons declarations
        int i = 0;
        for (Button item: buttons) {
            ++i;
            out("    protected ButtonToggleFactory button" + i + 
                " = new ButtonToggleFactory(" + "\"" + item.label + "\"," + 
                "\"" + item.label + ":tip\"," + "\"" + item.icon + "\"" + ");");
        }


        // Constructor      
        out("");
        out("    /** Constructor. */");
        out("    public " + getName() + "(FrameMain frame) {");
        out("        super(frame);");
        for (PanelItem item: items) {
            if (item.type.equals("string") && item.xmlTag != null) {
                if (item.readOnly)
                    out("        " + item.xmlTag + ".setEnabler(false);");
                if (item.mustSave)
                    out("        " + item.xmlTag + ".setMustSave(true);");
            } else if (item.type.equals("boolean") && item.xmlTag != null) {
                if (item.readOnly)
                    out("        " + item.xmlTag + ".setEnabler(false);");
                if (item.mustSave)
                    out("        " + item.xmlTag + ".setMustSave(true);");
            } else if (item.type.equals("date") && item.xmlTag != null) {
                if (item.readOnly)
                    out("        " + item.xmlTag + ".setEnabler(false);");
                if (item.mustSave)
                    out("        " + item.xmlTag + ".setMustSave(true);");
            } else if (item.type.equals("grid") && item.xmlTag != null) {
                Grid grid = item.getGrid(transaction);
                if (grid != null) {
                    String initialisation = 
                        grid.getInitialisation(this, item.xmlTag);
                    if (initialisation.length() > 0)
                        out(initialisation);
                }
                if (item.readOnly)
                    out("        " + item.xmlTag + ".setEnabler(false);");
                if (item.mustSave)
                    out("        " + item.xmlTag + ".setMustSave(true);");
            } else if (item.type.equals("text") && item.xmlTag != null) {
                if (item.readOnly)
                    out("        " + item.xmlTag + ".setEnabler(false);");
                if (item.mustSave)
                    out("        " + item.xmlTag + ".setMustSave(true);");
            } else if (item.type.equals("timezone") && item.xmlTag != null) {
                if (item.readOnly)
                    out("        " + item.xmlTag + ".setEnabler(false);");
                if (item.mustSave)
                    out("        " + item.xmlTag + ".setMustSave(true);");
            } else if (item.type.equals("application") && 
                       item.xmlTag != null) {
                if (item.readOnly)
                    out("        " + item.xmlTag + ".setEnabler(false);");
                if (item.mustSave)
                    out("        " + item.xmlTag + ".setMustSave(true);");
            } else if (item.type.equals("headername")) {
                if (item.readOnly)
                    out("        name.setEnabler(false);");
                if (item.mustSave)
                    out("        name.setMustSave(true);");
            } else if (item.type.equals("header")) {
                if (item.readOnly)
                    out("        name.setEnabler(false);");
                if (item.mustSave)
                    out("        name.setMustSave(true);");
            }
        }
        for (i = 1; i <= buttons.size(); i++)
            out("        button" + i + ".addActionListener(this);");
        out("    }");

        // Initialization
        out("");
        out("    /** Initialization (UI). */");
        out("    public void init(String tagpanel) throws Exception {");
        out("        super.init(tagpanel);");
        for (PanelItem item: items) {
            if (!item.invisible) {
                if (item.type.equals("string") && item.xmlTag != null) {
                    out("        add(\"" + item.getLabel() + "\", " + 
                        item.xmlTag + ");");
                } else if (item.type.equals("boolean") && 
                           item.xmlTag != null) {
                    out("        add(" + item.xmlTag + ");");
                } else if (item.type.equals("header")) {
                    out("        addHeader();");
                } else if (item.type.equals("headername")) {
                    out("        addHeaderName();");
                } else if (item.type.equals("comment")) {
                    out("        addComments();");
                } else if (item.type.equals("audit")) {
                    out("        addAudit();");
                } else if (item.type.equals("date") && item.xmlTag != null) {
                    out("        add(\"" + item.getLabel() + "\", " + 
                        item.xmlTag + ");");
                } else if (item.type.equals("text") && item.xmlTag != null) {
                    out("        add(\"" + item.getLabel() + "\", " + 
                        item.xmlTag + ");");
                } else if (item.type.equals("label") && item.xmlTag != null) {
                    out("        add(\"" + item.getLabel() + "\", " + 
                        item.xmlTag + ");");
                } else if (item.type.equals("password") && 
                           item.xmlTag != null) {
                    out("        add(\"" + item.getLabel() + "\", " + 
                        item.xmlTag + ");");
                } else if (item.type.equals("grid") && item.xmlTag != null) {
                    out("        add(\"" + item.getLabel() + "\", " + 
                        item.xmlTag + ");");
                } else if (item.type.equals("timezone") && 
                           item.xmlTag != null) {
                    out("        add(\"" + item.getLabel() + "\", " + 
                        item.xmlTag + ");");
                } else if (item.type.equals("application") && 
                           item.xmlTag != null) {
                    out("        add(\"" + item.getLabel() + "\", " + 
                        item.xmlTag + ");");
                } else if (item.type.equals("title")) {
                    out("        addPanel(\"" + item.getLabel() + "\");");
                    out("        nextPanel();");
                } else if (item.type.equals("instruction")) {
                    out("        addInstruction(\"" + item.getLabel() + 
                        "\");");
                }
            }
        }
        for (i = buttons.size(); i >= 1; i--)
            out("        addActionBarButton(button" + i + ");");

        out("    }");

        if (buttons.size() > 0) {
            out("");
            out("    /** Refreshs extra menu items. */");
            out("    public void refreshMenus() {");
            for (i = 1; i <= buttons.size(); i++) {
                out("        refreshMenu(getFrame().menuNewExtra" + i + 
                    ",button" + i + ");");
            }
            out("    }");
        }

        if (!canEdit) {
            out("");
            out("    /** Indicates if the panel can be edited. */");
            out("    public boolean canEdit() {");
            out("        return false;");
            out("    }");
        }

        if (!canDelete) {
            out("");
            out("    /** Indicates if the panel can be deleted. */");
            out("    public boolean canDelete() {");
            out("        return false;");
            out("    }");
        }

        if (!canSave) {
            out("");
            out("    /** Indicates if the panel can be saved. */");
            out("    public boolean canSave() {");
            out("        return false;");
            out("    }");
        }

        // Predefined actions
        if (buttons.size() > 0) {
            out("");
            out("    /** Manages buttons and menus. */");
            out("    public void actionPerformed(ActionEvent e) {");
            for (i = 1; i <= buttons.size(); i++) {
                out("        if (e.getSource() == button" + i + 
                    " || e.getSource() == getFrame().menuNewExtra" + i + 
                    ") {");
                for (ButtonParameter parameter: buttonParameters) {
                    out("            addDefaultValue(\"" + 
                        parameter.xmlTagParameter.toLowerCase() + "\"," + 
                        parameter.xmlTagValue + ");");
                }
                out("            actionNew(\"" + 
                    buttons.get(i - 1).xmlTag.toLowerCase() + "\");");
                out("        }");
            }
            out("    }");
        }

        // Management of actions
        if (xmlTagAction != null) {
            out("");
            out("    /** Creates an action based on the provided string. */");
            out("    public void createAction(String value) {");
            for (ButtonParameter parameter: buttonParameters) {
                out("        addDefaultValue(\"" + 
                    parameter.xmlTagParameter.toLowerCase() + "\"," + 
                    parameter.xmlTagValue + ");");
            }
            out("        addDefaultValue(\"" + xmlTagAction.toLowerCase() + 
                "\",value);");
            out("        actionNew(\"action\");");
            out("    }");
        }

        if (exitXml != null) {
            out("");
            out("    /** Manages panel content after it's populated from XML. */");
            out("    public void exitXmlIn() {");
            out("        super.exitXmlIn();");
            String[] code = exitXml.split("\n");
            for (String line: code)
                out("        " + line);
            out("    }");
        }

        // Add additional methods attached to the panel
        for (PanelCode panelCode: transaction.getServer().core.panelCodes) {
            if (panelCode.isActive() && panelCode.panelName != null && 
                panelCode.panelName.equals(this)) {
                out("");
                out("    /** " + panelCode.smallComments + " */");
                out("    " + panelCode.getName() + " {");
                if (panelCode.code != null) {
                    String[] code = panelCode.code.split("\n");
                    for (String line: code)
                        out("        " + line);
                }
                out("    }");
            }
        }

        out("}");
    }
}
