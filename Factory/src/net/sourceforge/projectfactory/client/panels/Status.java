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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Status.java,v $
$Revision: 1.4 $
$Date: 2007/02/26 17:22:05 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import java.awt.event.ActionEvent;
import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used for project statuses.
  * @author David Lambert
  */
public class Status extends PanelData {
    public EditBox plan = new EditBox();
    public EditBoxDate dateStatus = new EditBoxDate(this);
    public ComboBoxCode confidence = new ComboBoxCode("confidence");
    public TableBox statusItem = new TableBox(
                                "task","label:task",0,
                                "type","label:type",0,
                                "scheduled","label:scheduled",0,
                                "complete","label:complete",0,
                                "confidence","label:confidence",0,
                                "actionstatus","label:status",0,
                                "nothit","label:issue?",0,
                                "action","label:action",0);
    public LabelBox average = new LabelBox();
    public LabelBox averageScheduled = new LabelBox();
    public TableBox workload = new TableBox(
                                "task","label:name",200,
                                "type","label:type",10,
                                "duration","label:duration",5,
                                "durationtype","label:unit",5,
                                "adjustment","label:adjustment",10,
                                "adjustmentduration","label:duration",5,
                                "adjustmentdurationtype","label:unit",5,
                                "nothit","label:issue?",5);
    public LabelBox totalWorkload = new LabelBox();
    public TableBox adjustment = new TableBox(
                                "task","label:name",200,
                                "type","label:type",10,
                                "duration","label:scheduled",5,
                                "durationtype","label:unit",5,
                                "adjustment","label:adjustment",10,
                                "adjustmentduration","label:duration",5,
                                "adjustmentdurationtype","label:unit",5,
                                "remaining","label:remaining",5,
                                "remainingdurationtype","label:unit",5,
                                "nothit","label:issue?",5);
    public LabelBox totalCompleted = new LabelBox();
    public LabelBox totalRemaining = new LabelBox();
    public TableBox phase = new TableBox(
                                "phase","label:phase",10,
                                "description","label:description",100,
                                "complete","label:complete",10);
    public TableBoxLookup risk = new TableBoxLookup(
                                "risk","label:risk",100,
                                "task","label:task",50,
                                "probability","label:probability",5,
                                "duration","label:impact",5,
                                "durationtype","label:unit",5,
                                "exposure","label:exposure",5,
                                "owner","label:owner",30,
                                "actionstatus","label:status",10);
    public LabelBox totalExposure = new LabelBox();
    protected ButtonToggle button1 = new ButtonToggle("button:project:newstatus","button:project:newstatus:tip","status.gif");

    /** Constructor. */
    public Status(MainFrame frame) {
        super(frame);
        name.setEnabler(false);
        name.setMustSave(true);
        plan.setEnabler(false);
        plan.setMustSave(true);
        dateStatus.setEnabler(false);
        dateStatus.setMustSave(true);
        statusItem.hideButtons();
        statusItem.setReadOnly(0);
        statusItem.setCombo(1, new ComboBoxCode("itemtype"));
        statusItem.setReadOnly(1);
        statusItem.setPercentType(2);
        statusItem.setReadOnly(2);
        statusItem.setPercentType(3);
        statusItem.setCombo(4, new ComboBoxCode("confidence"));
        statusItem.setCombo(5, new ComboBoxCode("status"));
        statusItem.setHighlight(6);
        statusItem.setReadOnly(6);
        statusItem.setActionType(7, this);
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
        phase.hideButtons();
        phase.setPercentType(2);
        phase.setEnabler(false);
        risk.hideButtons();
        risk.attachLookup(1,this,"task");
        risk.setPercentType(2);
        risk.setIntegerType(3);
        risk.setCombo(4, new ComboBoxCode("duration"));
        risk.setIntegerType(5);
        risk.setReadOnly(5);
        risk.setCombo(7, new ComboBoxCode("status"));
        risk.setColumnDefault("1", 7);
        button1.addActionListener(this);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeader();
        add("label:plan", plan);
        add("label:date", dateStatus);
        addComments();
        addAudit();
        addPanel("label:status");
        nextPanel();
        add("", confidence);
        add("", statusItem);
        add("", average);
        add("", averageScheduled);
        addPanel("label:complete");
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
        add("", phase);
        addPanel("label:phases");
        nextPanel();
        add("", risk);
        add("", totalExposure);
        addPanel("label:risks");
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
            addDefaultValue("plan",plan);
            actionNew("status");
        }
    }

    /** Creates an action based on the provided string. */
    public void createAction(String value) {
        addDefaultValue("plan",plan);
        addDefaultValue("task",value);
        actionNew("action");
    }

    /** Manages panel content after it's populated from XML. */
    public void exitXmlIn() {
        super.exitXmlIn();
        average.addLabelPercent("label:average");
        averageScheduled.addLabelPercent("label:totalscheduled");
        totalWorkload.addLabel("label:total");
        totalCompleted.addLabel("label:totalcompleted");
        totalRemaining.addLabel("label:totalremaining");
        totalExposure.addLabel("label:totalexposure");
        // Copy the flag "Delay?" calculated in status items
        // in the remaining/adjustment and workload tables.
        statusItem.copyColumnsValue(adjustment, 6, 0, 9, 0);
        statusItem.copyColumnsValue(workload, 6, 0, 7, 0);
    }
}
