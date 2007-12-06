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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Plan.java,v $
$Revision: 1.2 $
$Date: 2007/02/12 09:41:11 $
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
  * Panel used for the definitions of plans.
  * @author David Lambert
  */
public class Plan extends PanelData {
    protected EditBoxLookup project = new EditBoxLookup(this, "project");
    protected EditBox version = new EditBox();
    protected EditBox forecastDate = new EditBox();
    protected EditBox hoursPerDay = new EditBox();
    protected CheckBox generateForecast = new CheckBox("label:generate:forecast");
    protected CheckBox useRemaining = new CheckBox("label:generate:remaining");
    protected TableBoxLookup member = new TableBoxLookup(
                                "actor","label:actor",100,
                                "from","label:from",10,
                                "to","label:to",10,
                                "role","label:role",50,
                                "location","label:location",100,
                                "phonenumber","label:phone",50,
                                "interim","label:interim",20);
    protected TableBoxLookup anonymous = new TableBoxLookup(
                                "name","label:name",100,
                                "description","label:description",100,
                                "location","label:location",20);
    protected TableBoxLookup absence = new TableBoxLookup(
                                "absence","label:date",10,
                                "actor","label:actor",100,
                                "duration","label:duration",10,
                                "durationtype","label:unit",10,
                                "purpose","label:purpose",100);
    protected TableBoxLookup event = new TableBoxLookup(
                                "event","label:date",10,
                                "duration","label:duration",5,
                                "durationtype","label:unit",5,
                                "actor","label:actor",100,
                                "purpose","label:purpose",200);
    protected TableBox phase = new TableBox(
                                "phase","label:phase",10,
                                "description","label:description",100);
    protected TableBoxLookup constraint = new TableBoxLookup(
                                "task","label:name",100,
                                "type","label:type",10,
                                "constraint","label:targetdate",10,
                                "predecessor","label:predecessor",100,
                                "priority","label:priority",10,
                                "phase","label:phase",20);
    protected TableBoxLookup risk = new TableBoxLookup(
                                "risk","label:risk",100,
                                "task","label:task",50,
                                "probability","label:probability",5,
                                "duration","label:impact",5,
                                "durationtype","label:unit",5,
                                "exposure","label:exposure",5,
                                "owner","label:owner",30,
                                "actionstatus","label:status",10);
    protected LabelBox totalExposure = new LabelBox();
    protected TableBox workload = new TableBox(
                                "task","label:name",200,
                                "type","label:type",10,
                                "duration","label:duration",5,
                                "durationtype","label:unit",5,
                                "adjustment","label:adjustment",10,
                                "adjustmentduration","label:duration",5,
                                "adjustmentdurationtype","label:unit",5,
                                "risk","label:risk",5);
    protected LabelBox totalWorkload = new LabelBox();
    protected TableBox adjustment = new TableBox(
                                "task","label:name",200,
                                "type","label:type",10,
                                "duration","label:scheduled",5,
                                "durationtype","label:unit",5,
                                "adjustment","label:adjustment",10,
                                "adjustmentduration","label:duration",5,
                                "adjustmentdurationtype","label:unit",5,
                                "remaining","label:remaining",5,
                                "remainingdurationtype","label:unit",5,
                                "risk","label:risk",5);
    protected LabelBox totalCompleted = new LabelBox();
    protected LabelBox totalRemaining = new LabelBox();
    protected TableBoxLookup assignment = new TableBoxLookup(
                                "task","label:name",100,
                                "type","label:type",10,
                                "actor","label:actor",100,
                                "anonymous","label:anonym",100,
                                "fte","label:fte",10,
                                "free","label:free",10);
    protected ButtonToggle button1 = new ButtonToggle("button:project:newversionplan","button:project:newversionplan:tip","plan.gif");
    protected ButtonToggle button2 = new ButtonToggle("button:project:newstatus","button:project:newstatus:tip","status.gif");
    protected ButtonToggle button3 = new ButtonToggle("button:project:newtracking","button:project:newtracking:tip","tracking.gif");

    /** Constructor. */
    public Plan(MainFrame frame) {
        super(frame);
        name.setEnabler(false);
        name.setMustSave(true);
        project.setEnabler(false);
        project.setMustSave(true);
        version.setEnabler(false);
        version.setMustSave(true);
        member.attachLookup(0,this,"actor");
        member.setDateType(1,this);
        member.setDateType(2,this);
        member.setReadOnly(4);
        member.setReadOnly(5);
        member.setBooleanType(6);
        member.setEnabler(false);
        anonymous.attachLookup(2,this,"location");
        absence.setDateType(0,this);
        absence.setIntegerType(2);
        absence.setCombo(3, new ComboBoxCode("duration"));
        absence.setEnabler(false);
        event.setDateType(0,this);
        event.setIntegerType(1);
        event.setColumnDefault("1", 1);
        event.setColumnDefault("1", 2);
        event.attachLookup(3,this,"actor",member);
        constraint.setNoSort();
        constraint.setReadOnly(0);
        constraint.setDupMode(0);
        constraint.setCombo(1, new ComboBoxCode("itemtype"));
        constraint.setReadOnly(1);
        constraint.setDupMode(1);
        constraint.setDateType(2,this);
        constraint.attachLookup(3,this,"task",constraint);
        constraint.setIntegerType(4);
        constraint.setDupMode(4);
        constraint.attachLookup(5,this,"phase",phase);
        constraint.setDupMode(5);
        risk.attachLookup(1,this,"task",workload);
        risk.setPercentType(2);
        risk.setIntegerType(3);
        risk.setCombo(4, new ComboBoxCode("duration"));
        risk.setIntegerType(5);
        risk.setReadOnly(5);
        risk.attachLookup(6,this,"actor",member);
        risk.setCombo(7, new ComboBoxCode("status"));
        risk.setColumnDefault("1", 7);
        workload.hideButtons();
        workload.setReadOnly(0);
        workload.setCombo(1, new ComboBoxCode("itemtype"));
        workload.setReadOnly(1);
        workload.setIntegerType(2);
        workload.setCombo(3, new ComboBoxCode("duration"));
        workload.setCombo(4, new ComboBoxCode("adjustment"));
        workload.setIntegerType(5);
        workload.setCombo(6, new ComboBoxCode("duration"));
        workload.setHighlight(7);
        workload.setReadOnly(7);
        adjustment.hideButtons();
        adjustment.setReadOnly(0);
        adjustment.setCombo(1, new ComboBoxCode("itemtype"));
        adjustment.setReadOnly(1);
        adjustment.setIntegerType(2);
        adjustment.setReadOnly(2);
        adjustment.setCombo(3, new ComboBoxCode("duration"));
        adjustment.setReadOnly(3);
        adjustment.setCombo(4, new ComboBoxCode("adjustment"));
        adjustment.setIntegerType(5);
        adjustment.setCombo(6, new ComboBoxCode("duration"));
        adjustment.setIntegerType(7);
        adjustment.setReadOnly(7);
        adjustment.setCombo(8, new ComboBoxCode("duration"));
        adjustment.setReadOnly(8);
        adjustment.setHighlight(9);
        adjustment.setReadOnly(9);
        assignment.setReadOnly(0);
        assignment.setDupMode(0);
        assignment.setCombo(1, new ComboBoxCode("itemtype"));
        assignment.setReadOnly(1);
        assignment.setDupMode(1);
        assignment.attachLookup(2,this,"actor",member);
        assignment.attachLookup(3,this,"anonymous",anonymous);
        assignment.setPercentType(4);
        assignment.setBooleanType(5);
        button1.addActionListener(this);
        button2.addActionListener(this);
        button3.addActionListener(this);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        add("label:project", project);
        add("label:version", version);
        add("label:begincalendar", forecastDate);
        add("label:hoursperday", hoursPerDay);
        add(generateForecast);
        add(useRemaining);
        addComments();
        addAudit();
        addPanel("label:plan");
        nextPanel();
        add("label:actors", member);
        add("label:anonymous", anonymous);
        addPanel("label:actors");
        nextPanel();
        add("label:absences", absence);
        add("label:events", event);
        addPanel("label:events");
        nextPanel();
        add("", phase);
        addPanel("label:phases");
        nextPanel();
        add("", constraint);
        addInstruction("instruction:constraint");
        addPanel("label:constraints");
        nextPanel();
        add("", risk);
        add("", totalExposure);
        addPanel("label:risks");
        nextPanel();
        add("", workload);
        addInstruction("instruction:workload");
        add("", totalWorkload);
        addPanel("label:workload");
        nextPanel();
        add("", adjustment);
        addInstruction("instruction:completed");
        add("", totalCompleted);
        add("", totalRemaining);
        addPanel("label:completed");
        nextPanel();
        add("", assignment);
        addInstruction("instruction:assignment");
        addPanel("label:assignments");
        nextPanel();
        addActionBarButton(button3);
        addActionBarButton(button2);
        addActionBarButton(button1);
    }

    /** Refreshs extra menu items. */
    public void refreshMenus() {
        refreshMenu(getFrame().menuNewExtra1,button1);
        refreshMenu(getFrame().menuNewExtra2,button2);
        refreshMenu(getFrame().menuNewExtra3,button3);
    }

    /** Manages buttons and menus. */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1 || e.getSource() == getFrame().menuNewExtra1) {
            addDefaultValue("project",project);
            addDefaultValue("plan",name);
            actionNew("plan");
        }
        if (e.getSource() == button2 || e.getSource() == getFrame().menuNewExtra2) {
            addDefaultValue("project",project);
            addDefaultValue("plan",name);
            actionNew("status");
        }
        if (e.getSource() == button3 || e.getSource() == getFrame().menuNewExtra3) {
            addDefaultValue("project",project);
            addDefaultValue("plan",name);
            actionNew("tracking");
        }
    }

    /** Manages panel content after it's populated from XML. */
    public void exitXmlIn() {
        super.exitXmlIn();
        totalWorkload.addLabel("label:total");
        totalCompleted.addLabel("label:totalcompleted");
        totalRemaining.addLabel("label:totalremaining");
        totalExposure.addLabel("label:totalexposure");
        // Hightlight the "Risk" flag in workload and remaining tables
        // based on risk table
        risk.copyHighlightColumns(workload, 0, 7, 1);
        risk.copyHighlightColumns(adjustment, 0, 9, 1);
    }
}
