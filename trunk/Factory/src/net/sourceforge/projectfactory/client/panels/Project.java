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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Project.java,v $
$Revision: 1.3 $
$Date: 2007/02/15 13:51:36 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import java.awt.event.ActionEvent;
import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.components.ComboBoxes.ComboBoxCode;
import net.sourceforge.projectfactory.client.components.EditBoxes.EditBox;
import net.sourceforge.projectfactory.client.components.EditBoxes.EditBoxLookup;
import net.sourceforge.projectfactory.client.components.TableBoxes.TableBox;
import net.sourceforge.projectfactory.client.components.TableBoxes.TableBoxLookup;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used for the definitions of projects.
  * @author David Lambert
  */
public class Project extends PanelData {
    protected EditBoxLookup businessProcess = new EditBoxLookup(this, "businessprocess");
    protected EditBox begin = new EditBox();
    protected EditBox target = new EditBox();
    protected ComboBoxCode confidence = new ComboBoxCode("confidence");
    protected EditBoxLookup lead = new EditBoxLookup(this, "actor");
    protected TableBoxLookup projectTeam = new TableBoxLookup(
                                "team","label:team",200,
                                "lead","label:lead",100);
    protected TableBoxLookup member = new TableBoxLookup(
                                "actor","label:actor",100,
                                "from","label:from",10,
                                "to","label:to",10,
                                "role","label:role",50,
                                "location","label:location",100,
                                "phonenumber","label:phone",50,
                                "interim","label:interim",20);
    protected TableBoxLookup availability = new TableBoxLookup(
                                "actor","label:actor",100,
                                "from","label:from",10,
                                "to","label:to",10,
                                "monday","label:monday",5,
                                "tuesday","label:tuesday",5,
                                "wednesday","label:wednesday",5,
                                "thursday","label:thursday",5,
                                "friday","label:friday",5,
                                "saturday","label:saturday",5,
                                "sunday","label:sunday",5);
    protected TableBox skill = new TableBox(
                                "actor","label:actor",50,
                                "skill","label:skill",100,
                                "level","label:level",10,
                                "comment","label:comment",100);
    protected ComboBoxCode statusScope = new ComboBoxCode("status");
    protected TableBox scopeItem = new TableBox(
                                "name","label:name",200,
                                "priority","label:priority",10,
                                "complexity","label:complexity",10);
    protected ComboBoxCode statusTasks = new ComboBoxCode("status");
    protected TableBox task = new TableBox(
                                "name","label:name",200,
                                "type","label:type",10,
                                "actionstatus","label:status",10,
                                "action","label:action",5);
    protected ButtonToggle button1 = new ButtonToggle("button:project:newplan","button:project:newplan:tip","plan.gif");

    /** Constructor. */
    public Project(MainFrame frame) {
        super(frame);
        businessProcess.setEnabler(false);
        businessProcess.setMustSave(true);
        projectTeam.attachLookup(0,this,"team");
        projectTeam.setReadOnly(1);
        member.attachLookup(0,this,"actor");
        member.setDateType(1,this);
        member.setDateType(2,this);
        member.setReadOnly(4);
        member.setReadOnly(5);
        member.setBooleanType(6);
        member.setEnabler(false);
        availability.attachLookup(0,this,"actor");
        availability.setDateType(1,this);
        availability.setDateType(2,this);
        availability.setBooleanType(3);
        availability.setBooleanType(4);
        availability.setBooleanType(5);
        availability.setBooleanType(6);
        availability.setBooleanType(7);
        availability.setBooleanType(8);
        availability.setBooleanType(9);
        skill.setCombo(2, new ComboBoxCode("fivestars"));
        scopeItem.setHierarchical();
        scopeItem.setCombo(1, new ComboBoxCode("fivestars"));
        scopeItem.setCombo(2, new ComboBoxCode("highlow"));
        task.setHierarchical();
        task.setCombo(1, new ComboBoxCode("itemtype"));
        task.setColumnDefault("1", 1);
        task.setCombo(2, new ComboBoxCode("status"));
        task.setColumnDefault("1", 2);
        task.setActionType(3, this);
        button1.addActionListener(this);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        add("label:businessprocess", businessProcess);
        add("label:begindate", begin);
        add("label:targetenddate", target);
        add("label:confidence", confidence);
        addComments();
        addAudit();
        addPanel("label:project");
        nextPanel();
        add("label:lead", lead);
        add("label:teams", projectTeam);
        add("label:actors", member);
        addPanel("label:participants");
        nextPanel();
        add("", availability);
        addInstruction("instruction:availability");
        addPanel("label:availability");
        nextPanel();
        add("", skill);
        addPanel("label:skills");
        nextPanel();
        add("", statusScope);
        add("", scopeItem);
        addPanel("label:scope");
        nextPanel();
        add("", statusTasks);
        add("", task);
        addPanel("label:tasks");
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
            addDefaultValue("project",name);
            actionNew("plan");
        }
    }

    /** Creates an action based on the provided string. */
    public void createAction(String value) {
            addDefaultValue("project",name);
            addDefaultValue("task",value);
            actionNew("action");
    }
}
