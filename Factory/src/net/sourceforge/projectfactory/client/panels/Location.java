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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Location.java,v $
$Revision: 1.2 $
$Date: 2007/02/12 09:41:01 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.components.comboBoxes.ComboBoxCodeTimeZone;
import net.sourceforge.projectfactory.client.components.tableBoxes.TableBox;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used for location definition, including timezone and workdays.
  * @author David Lambert
  */
public class Location extends PanelData {
    protected CheckBox defaultLocation = new CheckBox("label:default");
    protected ComboBoxCodeTimeZone timezone = new ComboBoxCodeTimeZone();
    protected CheckBox monday = new CheckBox("label:monday");
    protected CheckBox tuesday = new CheckBox("label:tuesday");
    protected CheckBox wednesday = new CheckBox("label:wednesday");
    protected CheckBox thursday = new CheckBox("label:thursday");
    protected CheckBox friday = new CheckBox("label:friday");
    protected CheckBox saturday = new CheckBox("label:saturday");
    protected CheckBox sunday = new CheckBox("label:sunday");
    protected TableBox actor = new TableBox(
                                "name","label:actor",100,
                                "position","label:position",100,
                                "phonenumber","label:phone",20);

    /** Constructor. */
    public Location(MainFrame frame) {
        super(frame);
        actor.setEnabler(false);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        add(defaultLocation);
        add("label:timezone", timezone);
        addComments();
        addAudit();
        addPanel("label:location");
        nextPanel();
        add(monday);
        add(tuesday);
        add(wednesday);
        add(thursday);
        add(friday);
        add(saturday);
        add(sunday);
        addPanel("label:workdays");
        nextPanel();
        add("", actor);
        addPanel("label:actors");
        nextPanel();
    }
}
