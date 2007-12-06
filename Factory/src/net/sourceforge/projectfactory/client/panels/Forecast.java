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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Forecast.java,v $
$Revision: 1.3 $
$Date: 2007/02/16 10:39:59 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.components.ComboBoxes.ComboBoxCode;
import net.sourceforge.projectfactory.client.components.EditBoxes.EditBox;
import net.sourceforge.projectfactory.client.components.EditBoxes.EditBoxDate;
import net.sourceforge.projectfactory.client.components.TableBoxes.TableBox;
import net.sourceforge.projectfactory.client.components.TableBoxes.TableBoxLookup;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used for calendar forecasts.
  * @author David Lambert
  */
public class Forecast extends PanelData {
    public EditBox plan = new EditBox();
    public EditBoxDate forecastDate = new EditBoxDate(this);
    public EditBoxDate endDate = new EditBoxDate(this);
    public TableBoxLookup sumTask = new TableBoxLookup(
                                "name","label:name",150,
                                "type","label:type",10,
                                "duration","label:duration",5,
                                "durationtype","label:unit",5,
                                "from","label:from",10,
                                "to","label:to",10,
                                "phase","label:phase",20,
                                "endphase","label:endphase",10,
                                "nothit","label:nothit",10,
                                "action","label:action",10);
    public LabelBox totalRemaining = new LabelBox();
    public LabelBox totalScheduled = new LabelBox();
    public TableBoxLookup phase = new TableBoxLookup(
                                "phase","label:phase",10,
                                "from","label:from",10,
                                "to","label:to",10);
    public TableBoxLookup weeklyTask = new TableBoxLookup(
                                "week","label:week",10,
                                "dateitem","label:date",20,
                                "purpose","label:description",150,
                                "type","label:type",10,
                                "assigned","label:assigned",50,
                                "target","label:constraint:target",10,
                                "completion","label:scheduled",10,
                                "phase","label:phase",10,
                                "nothit","label:nothit",10);
    public TableBoxLookup weeklyAssignment = new TableBoxLookup(
                                "week","label:week",10,
                                "dateitem","label:date",20,
                                "actor","label:actor",100,
                                "anonymous","label:anonym",100,
                                "ontask","label:ontask",10,
                                "onabsence","label:absence",10,
                                "noassignment","label:noassignment",10,
                                "badassignment","label:issue?",10);
    public TableBox completed = new TableBox(
                                "purpose","label:description",150,
                                "duration","label:duration",5,
                                "durationtype","label:unit",5);
    public LabelBox totalCompleted = new LabelBox();

    /** Constructor. */
    public Forecast(MainFrame frame) {
        super(frame);
        name.setEnabler(false);
        name.setMustSave(true);
        plan.setEnabler(false);
        plan.setMustSave(true);
        forecastDate.setEnabler(false);
        forecastDate.setMustSave(true);
        endDate.setEnabler(false);
        endDate.setMustSave(true);
        sumTask.setCombo(1, new ComboBoxCode("itemtype"));
        sumTask.setIntegerType(2);
        sumTask.setCombo(3, new ComboBoxCode("duration"));
        sumTask.setDateType(4,this);
        sumTask.setDateType(5,this);
        sumTask.setDateType(7,this);
        sumTask.setHighlight(8);
        sumTask.setActionType(9, this);
        sumTask.setEnabler(false);
        phase.setDateType(1,this);
        phase.setDateType(2,this);
        phase.setEnabler(false);
        weeklyTask.setDateType(1,this);
        weeklyTask.setCombo(3, new ComboBoxCode("itemtype"));
        weeklyTask.setDateType(5,this);
        weeklyTask.setPercentType(6);
        weeklyTask.setHighlight(8);
        weeklyTask.setEnabler(false);
        weeklyAssignment.setDateType(1,this);
        weeklyAssignment.setPercentType(4);
        weeklyAssignment.setPercentType(5);
        weeklyAssignment.setPercentType(6);
        weeklyAssignment.setHighlight(7);
        weeklyAssignment.setEnabler(false);
        completed.setIntegerType(1);
        completed.setCombo(2, new ComboBoxCode("duration"));
        completed.setEnabler(false);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeader();
        add("label:plan", plan);
        add("label:begincalendar", forecastDate);
        add("label:enddate", endDate);
        addComments();
        addAudit();
        addPanel("label:forecast");
        nextPanel();
        add("", sumTask);
        add("", totalRemaining);
        add("", totalScheduled);
        addPanel("label:summary");
        nextPanel();
        add("", phase);
        addPanel("label:phases");
        nextPanel();
        add("", weeklyTask);
        addPanel("label:weeklytask");
        nextPanel();
        add("", weeklyAssignment);
        addPanel("label:weeklyassignment");
        nextPanel();
        add("", completed);
        add("", totalCompleted);
        addPanel("label:completed");
        nextPanel();
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
        totalScheduled.addLabel("label:totalduration");
        totalRemaining.addLabel("label:totalremaining");
        totalCompleted.addLabel("label:total");
    }
}
