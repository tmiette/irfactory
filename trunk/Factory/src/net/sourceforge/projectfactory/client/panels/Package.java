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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Package.java,v $
$Revision: 1.3 $
$Date: 2007/02/12 11:17:34 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import java.awt.event.ActionEvent;
import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.components.ComboBoxes.ComboBoxCodeApplication;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used for the definitions of packages.
  * @author David Lambert
  */
public class Package extends PanelData {
    protected ComboBoxCodeApplication application = new ComboBoxCodeApplication(this);
    protected TextBox headerFile = new TextBox();
    protected TextBox generatedCode = new TextBox();
    protected ButtonToggle button1 = new ButtonToggle("button:package:newgrid","button:package:newgrid:tip","grid.gif");
    protected ButtonToggle button2 = new ButtonToggle("button:package:newpanel","button:package:newpanel:tip","panel.gif");
    protected ButtonToggle button3 = new ButtonToggle("button:package:newclass","button:package:newclass:tip","classdefinition.gif");

    /** Constructor. */
    public Package(MainFrame frame) {
        super(frame);
        generatedCode.setEnabler(false);
        button1.addActionListener(this);
        button2.addActionListener(this);
        button3.addActionListener(this);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        add("label:application", application);
        addComments();
        addAudit();
        addPanel("label:package");
        nextPanel();
        add("", headerFile);
        addPanel("label:fileheader");
        nextPanel();
        add("", generatedCode);
        addPanel("label:generatedcode");
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
            addDefaultValue("application",application);
            addDefaultValue("packagereference",name);
            actionNew("grid");
        }
        if (e.getSource() == button2 || e.getSource() == getFrame().menuNewExtra2) {
            addDefaultValue("application",application);
            addDefaultValue("packagereference",name);
            actionNew("panel");
        }
        if (e.getSource() == button3 || e.getSource() == getFrame().menuNewExtra3) {
            addDefaultValue("application",application);
            addDefaultValue("packagereference",name);
            actionNew("classdefinition");
        }
    }
}
