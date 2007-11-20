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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Action.java,v $
$Revision: 1.2 $
$Date: 2007/02/12 09:41:18 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import java.awt.event.ActionEvent;
import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used in order to manage actions.
  * @author David Lambert
  */
public class Action extends PanelData {
    protected ComboBoxCode status = new ComboBoxCode("status");
    protected TableBoxLookup recipient = new TableBoxLookup(
                                "actor","label:actor",200,
                                "role","label:role",20,
                                "status","label:status",10);
    protected TableBox step = new TableBox(
                                "name","label:step",200,
                                "complexity","label:complexity",10,
                                "actionstatus","label:status",10);
    protected TextBox details = new TextBox();
    protected EditBox project = new EditBox();
    protected EditBox task = new EditBox();
    protected CheckBox attachProject = new CheckBox("label:attachproject");
    protected CheckBox attachForecast = new CheckBox("label:attachforecast");
    protected EditBoxDate begin = new EditBoxDate(this);
    protected EditBoxDate target = new EditBoxDate(this);
    protected TableBoxLookup item = new TableBoxLookup(
                                "dateitem","label:date",20,
                                "duration","label:duration",10,
                                "durationtype","label:unit",10,
                                "assigned","label:assigned",50,
                                "completion","label:scheduled",10);
    protected ButtonToggleFactory button1 = new ButtonToggleFactory("button:action:answer","button:action:answer:tip","action.gif");

    /** Constructor. */
    public Action(FrameMain frame) {
        super(frame);
        recipient.attachLookup(0,this,"actor");
        recipient.setCombo(1, new ComboBoxCode("role"));
        recipient.setColumnDefault("1", 1);
        recipient.setCombo(2, new ComboBoxCode("status:recipient"));
        recipient.setColumnDefault("1", 2);
        step.setHierarchical();
        step.setCombo(1, new ComboBoxCode("highlow"));
        step.setColumnDefault("2", 1);
        step.setCombo(2, new ComboBoxCode("status"));
        step.setColumnDefault("1", 2);
        project.setEnabler(false);
        project.setMustSave(true);
        task.setEnabler(false);
        task.setMustSave(true);
        begin.setEnabler(false);
        begin.setMustSave(true);
        target.setEnabler(false);
        target.setMustSave(true);
        item.setDateType(0,this);
        item.setIntegerType(1);
        item.setCombo(2, new ComboBoxCode("duration"));
        item.setPercentType(4);
        item.setEnabler(false);
        item.setMustSave(true);
        button1.addActionListener(this);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        add("label:status", status);
        add("label:recipients", recipient);
        addAudit();
        addPanel("label:action");
        nextPanel();
        add("", step);
        addInstruction("instruction:steps");
        add("", details);
        addPanel("label:details");
        nextPanel();
        add("label:project", project);
        add("label:task", task);
        add(attachProject);
        add(attachForecast);
        add("label:begindate", begin);
        add("label:enddate", target);
        add("", item);
        addPanel("label:forecast");
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
            addDefaultValue("action",name);
            addDefaultValue("parentiid",iid);
            actionNew("action");
        }
    }

    /** Manages panel content after it's populated from XML. */
    public void exitXmlIn() {
        super.exitXmlIn();
        status.setEnabler(parentIid.getText().length() == 0);
        step.setEnabler(parentIid.getText().length() == 0);
    }
}
