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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Panel.java,v $
$Revision: 1.5 $
$Date: 2007/02/27 22:11:44 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import java.awt.event.ActionEvent;
import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used in order to define panels, then generate code.
  * @author David Lambert
  */
public class Panel extends PanelData {
    public ComboBoxCodeApplication application = new ComboBoxCodeApplication(this);
    public EditBoxLookup packageReference = new EditBoxLookup(this, "package");
    public CheckBox draft = new CheckBox("label:draft");
    public CheckBox canSave = new CheckBox("label:cansave");
    public CheckBox canEdit = new CheckBox("label:canedit");
    public CheckBox canDelete = new CheckBox("label:candelete");
    public TableBoxLookup content = new TableBoxLookup(
                                "xmltag","label:xmltag",100,
                                "label","label:label",100,
                                "type","label:type",50,
                                "readonly","label:readonly",5,
                                "invisible","label:invisible",5,
                                "mustsave","label:mustsave",5,
                                "combobox","label:combobox",20,
                                "grid","label:grid",20,
                                "lookupclass","label:lookupclass",20);
    public TableBoxLookup button = new TableBoxLookup(
                                "label","label:label",100,
                                "icon","label:icon",50,
                                "xmltag","label:action:tag",20);
    public EditBox xmlTagAction = new EditBox();
    public TableBox buttonParameter = new TableBox(
                                "xmltagparameter","label:xmltag:parameter",50,
                                "xmltagvalue","label:xmltag:value",50);
    public TextBox exitXML = new TextBox();
    public TextBox generatedCode = new TextBox();
    protected ButtonToggle button1 = new ButtonToggle("button:panel:newcode","button:panel:newcode:tip","plus.gif");

    /** Constructor. */
    public Panel(MainFrame frame) {
        super(frame);
        content.setNoSort();
        content.attachLookup(1,this,"dictionary");
        content.setCombo(2, new ComboBoxCode("panel:item:type"));
        content.setColumnDefault("string", 2);
        content.setBooleanType(3);
        content.setBooleanType(4);
        content.setBooleanType(5);
        content.attachLookup(6,this,"combobox");
        content.attachLookup(7,this,"grid");
        button.setNoSort();
        button.attachLookup(0,this,"dictionary");
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
        add(canSave);
        add(canEdit);
        add(canDelete);
        addComments();
        addAudit();
        addPanel("label:panel");
        nextPanel();
        add("", content);
        addPanel("label:content");
        nextPanel();
        add("label:buttons", button);
        add("label:xmltag:action", xmlTagAction);
        add("label:button:parameters", buttonParameter);
        addPanel("label:buttons");
        nextPanel();
        add("label:exitxml", exitXML);
        addPanel("label:predefinedactions");
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
            addDefaultValue("panelname",name);
            actionNew("panelcode");
        }
    }
}
