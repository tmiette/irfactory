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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/PanelLoader.java,v $
$Revision: 1.3 $
$Date: 2007/02/27 22:11:44 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import net.sourceforge.projectfactory.client.MainFrame;

/** 
  * Client side of Factory core (panels).
  * @author David Lambert
  */
public class PanelLoader {

    /** Construct data panels and attach them to the main frame. */
    public void load(MainFrame frame) throws Exception {
        frame.addPanel(new About(frame), "about");
        frame.addPanel(new Action(frame), "action");
        frame.addPanel(new ActionBar(frame), "actionbar");
        frame.addPanel(new Actor(frame), "actor");
        frame.addPanel(new Blank(frame), "blank");
        frame.addPanel(new BusinessProcess(frame), "businessprocess");
        frame.addPanel(new ClassCode(frame), "classcode");
        frame.addPanel(new ClassDefinition(frame), "classdefinition");
        frame.addPanel(new ComboBox(frame), "combobox");
        frame.addPanel(new Connexion(frame), "connexion");
        frame.addPanel(new Dictionary(frame), "dictionary");
        frame.addPanel(new Forecast(frame), "forecast");
        frame.addPanel(new Grid(frame), "grid");
        frame.addPanel(new HolidaySchedule(frame), "holidayschedule");
        frame.addPanel(new LocalHost(frame), "localhost");
        frame.addPanel(new Location(frame), "location");
        frame.addPanel(new Package(frame), "package");
        frame.addPanel(new Panel(frame), "panel");
        frame.addPanel(new PanelCode(frame), "panelcode");
        frame.addPanel(new Plan(frame), "plan");
        frame.addPanel(new Project(frame), "project");
        frame.addPanel(new Server(frame), "server");
        frame.addPanel(new Status(frame), "status");
        frame.addPanel(new Team(frame), "team");
        frame.addPanel(new Tracking(frame), "tracking");
    }
}
