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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Team.java,v $
$Revision: 1.2 $
$Date: 2007/02/12 09:41:11 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.components.comboBoxes.ComboBoxCode;
import net.sourceforge.projectfactory.client.components.editBoxes.EditBoxLookup;
import net.sourceforge.projectfactory.client.components.tableBoxes.TableBox;
import net.sourceforge.projectfactory.client.components.tableBoxes.TableBoxLookup;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used in order to define a team and its members.
  * @author David Lambert
  */
public class Team extends PanelData {
    protected EditBoxLookup lead = new EditBoxLookup(this, "actor");
    protected TableBoxLookup member = new TableBoxLookup(
                                "actor","label:actor",100,
                                "from","label:from",10,
                                "to","label:to",10,
                                "role","label:role",50,
                                "location","label:location",100,
                                "phonenumber","label:phone",50,
                                "interim","label:interim",20);
    protected TableBoxLookup absence = new TableBoxLookup(
                                "absence","label:date",10,
                                "actor","label:actor",100,
                                "duration","label:duration",10,
                                "durationtype","label:unit",10,
                                "purpose","label:purpose",100);
    protected TableBox skill = new TableBox(
                                "actor","label:actor",100,
                                "skill","label:skill",10,
                                "level","label:level",10,
                                "comment","label:comment",100);
    protected TableBoxLookup project = new TableBoxLookup(
                                "name","label:name",200,
                                "begin","label:begindate",10,
                                "target","label:targetdate",10,
                                "lead","label:lead",100);

    /** Constructor. */
    public Team(MainFrame frame) {
        super(frame);
        member.attachLookup(0,this,"actor");
        member.setDateType(1,this);
        member.setDateType(2,this);
        member.setReadOnly(4);
        member.setReadOnly(5);
        member.setBooleanType(6);
        absence.setDateType(0,this);
        absence.setIntegerType(2);
        absence.setCombo(3, new ComboBoxCode("duration"));
        absence.setEnabler(false);
        skill.setCombo(2, new ComboBoxCode("fivestars"));
        skill.setEnabler(false);
        project.setDateType(1,this);
        project.setDateType(2,this);
        project.setEnabler(false);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        add("label:lead", lead);
        addComments();
        addAudit();
        addPanel("label:team");
        nextPanel();
        add("", member);
        addPanel("label:selection");
        nextPanel();
        add("", absence);
        addPanel("label:absences");
        nextPanel();
        add("", skill);
        addPanel("label:skills");
        nextPanel();
        add("", project);
        addPanel("label:projects");
        nextPanel();
    }
}
