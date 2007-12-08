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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/BusinessProcess.java,v $
$Revision: 1.2 $
$Date: 2007/02/12 09:41:18 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import java.awt.event.ActionEvent;
import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used for the definition of the business processes (template).
  * @author David Lambert
  */
public class BusinessProcess extends PanelData {
    protected ComboBoxCode statusTasks = new ComboBoxCode("status");
    protected TableBox task = new TableBox(
                                "name","label:name",200,
                                "type","label:type",10);
    protected TableBoxLookup project = new TableBoxLookup(
                                "name","label:name",200,
                                "begin","label:begindate",10,
                                "target","label:targetdate",10,
                                "lead","label:lead",100);
    protected ButtonToggle button1 = new ButtonToggle("button:businessprocess:newproject","button:businessprocess:newproject:tip","project.gif");

    /** Constructor. */
    public BusinessProcess(MainFrame frame) {
        super(frame);
        task.setHierarchical();
        task.setCombo(1, new ComboBoxCode("itemtype"));
        task.setColumnDefault("1", 1);
        project.setDateType(1,this);
        project.setDateType(2,this);
        project.setEnabler(false);
        button1.addActionListener(this);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        addComments();
        addAudit();
        addPanel("label:businessprocess");
        nextPanel();
        add("label:status", statusTasks);
        add("", task);
        addPanel("label:tasks");
        nextPanel();
        add("", project);
        addPanel("label:projects");
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
            addDefaultValue("businessprocess",name);
            actionNew("project");
        }
    }
}
