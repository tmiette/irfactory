/*

Copyright (c) 2007 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/ClassDefinition.java,v $
$Revision: 1.5 $
$Date: 2007/03/04 21:03:37 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import java.awt.event.ActionEvent;
import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.components.comboBoxes.ComboBoxCode;
import net.sourceforge.projectfactory.client.components.comboBoxes.ComboBoxCodeApplication;
import net.sourceforge.projectfactory.client.components.editBoxes.EditBox;
import net.sourceforge.projectfactory.client.components.editBoxes.EditBoxLookup;
import net.sourceforge.projectfactory.client.components.tableBoxes.TableBox;
import net.sourceforge.projectfactory.client.components.tableBoxes.TableBoxLookup;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used in order to define classes and generate code.
  * @author David Lambert
  */
public class ClassDefinition extends PanelData {
    public ComboBoxCodeApplication application = new ComboBoxCodeApplication(this);
    public EditBoxLookup packageReference = new EditBoxLookup(this, "package");
    public CheckBox draft = new CheckBox("label:draft");
    public ComboBoxCode classType = new ComboBoxCode("class:type");
    public EditBoxLookup superClass = new EditBoxLookup(this, "classdefinition");
    public CheckBox createDerived = new CheckBox("label:createderived");
    public EditBox xmlTag = new EditBox();
    public CheckBox uniqueName = new CheckBox("label:uniquename");
    public TableBoxLookup content = new TableBoxLookup(
                                "xmltag","label:xmltag",100,
                                "type","label:type",20,
                                "fromparent","label:fromparent",5,
                                "classreference","label:class",20,
                                "listname","label:listname",50,
                                "errormessage","label:errormessage",50);
    public TableBox classValue = new TableBox(
                                "label","label:label",100,
                                "staticvalue","label:value",20);
    public TextBox generatedCode = new TextBox();
    protected ButtonToggle button1 = new ButtonToggle("button:class:newcode","button:class:newcode:tip","plus.gif");

    /** Constructor. */
    public ClassDefinition(MainFrame frame) {
        super(frame);
        content.setNoSort();
        content.setCombo(1, new ComboBoxCode("class:item:type"));
        content.setColumnDefault("string", 1);
        content.setBooleanType(2);
        content.attachLookup(3,this,"classdefinition");
        generatedCode.setEnabler(false);
        button1.addActionListener(this);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        add("label:application", application);
        add("label:package", packageReference);
        add(draft);
        add("label:classtype", classType);
        add("label:superclass", superClass);
        add(createDerived);
        add("label:xmltag", xmlTag);
        add(uniqueName);
        addComments();
        addAudit();
        addPanel("label:class");
        nextPanel();
        add("", content);
        addPanel("label:content");
        nextPanel();
        add("", classValue);
        addPanel("label:staticvalues");
        nextPanel();
        add("", generatedCode);
        addPanel("label:generatedcode");
        nextPanel();
        addActionBarButton(button1);
    }

    /** Refreshs extra menu items. */
    public void refreshMenus() {
        refreshMenu(getFrame().menuNewExtra1,button1);
    }

    /** Manages buttons and menus. */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1 || e.getSource() == getFrame().menuNewExtra1) {
            addDefaultValue("packagereference",packageReference);
            addDefaultValue("application",application);
            addDefaultValue("classname",name);
            actionNew("classcode");
        }
    }
}
