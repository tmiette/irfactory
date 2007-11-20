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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/HolidaySchedule.java,v $
$Revision: 1.2 $
$Date: 2007/02/12 09:41:18 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used for holiday schedules.
  * @author David Lambert
  */
public class HolidaySchedule extends PanelData {
    protected CheckBox defaultHolidaySchedule = new CheckBox("label:default");
    protected TableBoxLookup holiday = new TableBoxLookup(
                                "holiday","label:date",10,
                                "duration","label:duration",10,
                                "durationtype","label:unit",10,
                                "purpose","label:purpose",200);
    protected TableBox actor = new TableBox(
                                "name","label:actor",100,
                                "position","label:position",100,
                                "phonenumber","label:phone",20);

    /** Constructor. */
    public HolidaySchedule(FrameMain frame) {
        super(frame);
        holiday.setDateType(0,this);
        holiday.setIntegerType(1);
        holiday.setColumnDefault("1", 1);
        holiday.setCombo(2, new ComboBoxCode("duration"));
        holiday.setColumnDefault("1", 2);
        actor.setEnabler(false);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        add(defaultHolidaySchedule);
        addComments();
        addAudit();
        addPanel("label:holiday");
        nextPanel();
        add("", holiday);
        addPanel("label:calendar");
        nextPanel();
        add("", actor);
        addPanel("label:actors");
        nextPanel();
    }
}
