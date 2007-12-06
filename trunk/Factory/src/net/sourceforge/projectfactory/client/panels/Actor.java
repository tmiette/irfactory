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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Actor.java,v $
$Revision: 1.4 $
$Date: 2007/02/22 15:36:47 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.components.comboBoxes.ComboBoxCode;
import net.sourceforge.projectfactory.client.components.editBoxes.EditBox;
import net.sourceforge.projectfactory.client.components.editBoxes.EditBoxLookup;
import net.sourceforge.projectfactory.client.components.tableBoxes.TableBox;
import net.sourceforge.projectfactory.client.components.tableBoxes.TableBoxLookup;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used for actor definition, including absences, skills and contacts.
  * @author David Lambert
  */
public class Actor extends PanelData {
    public EditBox position = new EditBox();
    public EditBoxLookup reportsTo = new EditBoxLookup(this, "actor");
    public EditBox phoneNumber = new EditBox();
    public EditBox networkID = new EditBox();
    public EditBox alternateNetworkID = new EditBox();
    public CheckBox administrator = new CheckBox("label:administrator");
    public TableBox email = new TableBox(
                                "address","label:email",200,
                                "principal","label:principal",10);
    public EditBoxLookup location = new EditBoxLookup(this, "location");
    public EditBoxLookup holidaySchedule = new EditBoxLookup(this, "holidayschedule");
    public TableBoxLookup absence = new TableBoxLookup(
                                "absence","label:date",10,
                                "duration","label:duration",10,
                                "durationtype","label:unit",10,
                                "purpose","label:purpose",200);
    public TableBox skill = new TableBox(
                                "skill","label:skill",100,
                                "level","label:level",10,
                                "comment","label:comment",10);
    public TableBoxLookup team = new TableBoxLookup(
                                "team","label:team",100,
                                "from","label:from",10,
                                "to","label:to",10,
                                "role","label:role",100,
                                "lead","label:lead",100);
    public TableBox whoReport = new TableBox(
                                "name","label:actor",100,
                                "position","label:position",100,
                                "phonenumber","label:phone",30);
    public TableBoxLookup project = new TableBoxLookup(
                                "name","label:name",200,
                                "begin","label:begindate",10,
                                "target","label:targetdate",10,
                                "lead","label:lead",100);

    /** Constructor. */
    public Actor(MainFrame frame) {
        super(frame);
        email.setBooleanType(1);
        absence.setDateType(0,this);
        absence.setIntegerType(1);
        absence.setColumnDefault("1", 1);
        absence.setCombo(2, new ComboBoxCode("duration"));
        absence.setColumnDefault("1", 2);
        skill.setCombo(1, new ComboBoxCode("fivestars"));
        team.setDateType(1,this);
        team.setDateType(2,this);
        team.setEnabler(false);
        whoReport.setEnabler(false);
        project.setDateType(1,this);
        project.setDateType(2,this);
        project.setEnabler(false);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        add("label:position", position);
        add("label:reportsto", reportsTo);
        addComments();
        addAudit();
        addPanel("label:actor");
        nextPanel();
        add("label:phone", phoneNumber);
        add("label:networkid", networkID);
        add("label:alternatenetworkid", alternateNetworkID);
        add(administrator);
        add("label:emails", email);
        addPanel("label:contact");
        nextPanel();
        add("label:location", location);
        add("label:holiday", holidaySchedule);
        add("label:absences", absence);
        addPanel("label:absences");
        nextPanel();
        add("", skill);
        addPanel("label:skills");
        nextPanel();
        add("label:teams", team);
        add("label:whoreport", whoReport);
        addPanel("label:colleagues");
        nextPanel();
        add("", project);
        addPanel("label:projects");
        nextPanel();
    }
}
