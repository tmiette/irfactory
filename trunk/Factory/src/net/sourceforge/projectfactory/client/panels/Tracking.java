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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Tracking.java,v $
$Revision: 1.2 $
$Date: 2007/02/12 09:41:10 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import java.awt.event.ActionEvent;
import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.components.ComboBoxes.ComboBoxCode;
import net.sourceforge.projectfactory.client.components.EditBoxes.EditBox;
import net.sourceforge.projectfactory.client.components.EditBoxes.EditBoxDate;
import net.sourceforge.projectfactory.client.components.TableBoxes.TableBox;
import net.sourceforge.projectfactory.client.components.TableBoxes.TableBoxLookup;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used for project trackings.
  * @author David Lambert
  */
public class Tracking extends PanelData {
    protected EditBox plan = new EditBox();
    protected EditBoxDate dateTrackingFrom = new EditBoxDate(this);
    protected EditBoxDate dateTrackingTo = new EditBoxDate(this);
    protected TableBoxLookup trackingItem = new TableBoxLookup(
                                "dateitem","label:date",20,
                                "day","label:day",5,
                                "task","label:task",100,
                                "type","label:type",10,
                                "actor","label:actor",50,
                                "duration","label:duration",5,
                                "durationtype","label:unit",5,
                                "ontrack","label:ontrack",5,
                                "trackingitemduration","label:duration",5,
                                "trackingitemdurationtype","label:unit",5);
    protected LabelBox totalScheduled = new LabelBox();
    protected TableBoxLookup member = new TableBoxLookup(
                                "actor","label:actor",100,
                                "from","label:from",10,
                                "to","label:to",10,
                                "role","label:role",50,
                                "location","label:location",100,
                                "phonenumber","label:phone",50,
                                "interim","label:interim",20);
    protected TableBox task = new TableBox(
                                "task","",0);
    protected TableBoxLookup unscheduledTrackingItem = new TableBoxLookup(
                                "dateitem","label:date",20,
                                "day","label:day",5,
                                "task","label:task",100,
                                "type","label:type",10,
                                "actor","label:actor",50,
                                "trackingitemduration","label:duration",5,
                                "trackingitemdurationtype","label:unit",5);
    protected LabelBox totalUnscheduled = new LabelBox();
    protected TableBox totalActor = new TableBox(
                                "actor","label:actor",50,
                                "duration","label:duration",5,
                                "durationtype","label:unit",5);
    protected TableBox totalTask = new TableBox(
                                "task","label:task",50,
                                "type","label:type",10,
                                "duration","label:duration",5,
                                "durationtype","label:unit",5);
    protected LabelBox totals = new LabelBox();
    protected TableBox cumulatedActor = new TableBox(
                                "actor","label:actor",50,
                                "duration","label:duration",5,
                                "durationtype","label:unit",5);
    protected TableBox cumulatedTask = new TableBox(
                                "task","label:task",50,
                                "type","label:type",10,
                                "duration","label:duration",5,
                                "durationtype","label:unit",5);
    protected LabelBox cumulated = new LabelBox();
    protected ButtonToggle button1 = new ButtonToggle("button:project:newtracking","button:project:newtracking:tip","tracking.gif");

    /** Constructor. */
    public Tracking(MainFrame frame) {
        super(frame);
        name.setEnabler(false);
        name.setMustSave(true);
        plan.setEnabler(false);
        plan.setMustSave(true);
        trackingItem.hideButtons();
        trackingItem.setDateType(0,this);
        trackingItem.setReadOnly(0);
        trackingItem.setCombo(1, new ComboBoxCode("day"));
        trackingItem.setReadOnly(1);
        trackingItem.setReadOnly(2);
        trackingItem.setCombo(3, new ComboBoxCode("itemtype"));
        trackingItem.setReadOnly(3);
        trackingItem.setReadOnly(4);
        trackingItem.setIntegerType(5);
        trackingItem.setReadOnly(5);
        trackingItem.setCombo(6, new ComboBoxCode("duration"));
        trackingItem.setReadOnly(6);
        trackingItem.setBooleanType(7);
        trackingItem.setIntegerType(8);
        trackingItem.setCombo(9, new ComboBoxCode("duration"));
        member.attachLookup(0,this,"actor");
        member.setDateType(1,this);
        member.setDateType(2,this);
        member.setReadOnly(4);
        member.setReadOnly(5);
        member.setBooleanType(6);
        member.setEnabler(false);
        task.setEnabler(false);
        unscheduledTrackingItem.setDateType(0,this);
        unscheduledTrackingItem.setCombo(1, new ComboBoxCode("day"));
        unscheduledTrackingItem.setReadOnly(1);
        unscheduledTrackingItem.attachLookup(2,this,"task",task);
        unscheduledTrackingItem.setCombo(3, new ComboBoxCode("itemtype"));
        unscheduledTrackingItem.setReadOnly(3);
        unscheduledTrackingItem.attachLookup(4,this,"actor",member);
        unscheduledTrackingItem.setIntegerType(5);
        unscheduledTrackingItem.setColumnDefault("1", 5);
        unscheduledTrackingItem.setCombo(6, new ComboBoxCode("duration"));
        unscheduledTrackingItem.setColumnDefault("1", 6);
        totalActor.setIntegerType(1);
        totalActor.setCombo(2, new ComboBoxCode("duration"));
        totalActor.setEnabler(false);
        totalTask.setCombo(1, new ComboBoxCode("itemtype"));
        totalTask.setIntegerType(2);
        totalTask.setCombo(3, new ComboBoxCode("duration"));
        totalTask.setEnabler(false);
        cumulatedActor.setIntegerType(1);
        cumulatedActor.setCombo(2, new ComboBoxCode("duration"));
        cumulatedActor.setEnabler(false);
        cumulatedTask.setCombo(1, new ComboBoxCode("itemtype"));
        cumulatedTask.setIntegerType(2);
        cumulatedTask.setCombo(3, new ComboBoxCode("duration"));
        cumulatedTask.setEnabler(false);
        button1.addActionListener(this);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeader();
        add("label:plan", plan);
        add("label:from", dateTrackingFrom);
        add("label:to", dateTrackingTo);
        addComments();
        addAudit();
        addPanel("label:tracking");
        nextPanel();
        add("", trackingItem);
        add("", totalScheduled);
        addPanel("label:scheduled");
        nextPanel();
        add("", unscheduledTrackingItem);
        add("", totalUnscheduled);
        addPanel("label:unscheduled");
        nextPanel();
        add("label:peractor", totalActor);
        add("label:pertask", totalTask);
        add("", totals);
        addPanel("label:totals");
        nextPanel();
        add("label:peractor", cumulatedActor);
        add("label:pertask", cumulatedTask);
        add("", cumulated);
        addPanel("label:cumulated");
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
            actionNew("tracking");
        }
    }

    /** Manages panel content after it's populated from XML. */
    public void exitXmlIn() {
        super.exitXmlIn();
        totalScheduled.addLabel("label:total");
        totalUnscheduled.addLabel("label:total");
        totals.addLabel("label:total");
        cumulated.addLabel("label:total");
    }
}
