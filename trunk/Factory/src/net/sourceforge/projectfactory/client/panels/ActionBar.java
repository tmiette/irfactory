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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/ActionBar.java,v $
$Revision: 1.2 $
$Date: 2007/03/04 21:03:37 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Defines an action used to be available in the action bar.
  * @author David Lambert
  */
public class ActionBar extends PanelData {
    public ComboBoxCodeApplication application = new ComboBoxCodeApplication(this);
    public EditBox labelButton = new EditBox();
    public EditBox icon = new EditBox();
    public CheckBox administratorOnly = new CheckBox("label:administratoronly");
    public CheckBox localOnly = new CheckBox("label:localonly");
    public EditBox orderNumber = new EditBox();
    public TableBoxLookup item = new TableBoxLookup(
                                "button","label:button",50,
                                "panel","label:panel",50);

    /** Constructor. */
    public ActionBar(FrameMain frame) {
        super(frame);
        item.setNoSort();
        item.attachLookup(0,this,"dictionary");
        item.attachLookup(1,this,"panel");
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        add("label:application", application);
        add("label:labelbutton", labelButton);
        add("label:icon", icon);
        add(administratorOnly);
        add(localOnly);
        add("label:ordernumber", orderNumber);
        add("label:panels", item);
        addComments();
        addAudit();
        addPanel("label:actionbar");
        nextPanel();
    }
}
