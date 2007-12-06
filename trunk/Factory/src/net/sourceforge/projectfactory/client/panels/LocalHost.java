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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/LocalHost.java,v $
$Revision: 1.4 $
$Date: 2007/02/19 17:37:37 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.components.comboBoxes.ComboBoxCode;
import net.sourceforge.projectfactory.client.components.editBoxes.EditBox;
import net.sourceforge.projectfactory.client.components.editBoxes.EditBoxPassword;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used for the definitions of the local host or server.
  * @author David Lambert
  */
public class LocalHost extends PanelData {
    public EditBox port = new EditBox();
    public EditBoxPassword encryptKey = new EditBoxPassword();
    public CheckBox allowReplication = new CheckBox("label:allowreplication");
    public CheckBox activeInMail = new CheckBox("label:active");
    public ComboBoxCode inMailType = new ComboBoxCode("option:mail:protocol:receive");
    public EditBox inMailServer = new EditBox();
    public EditBox inMailPort = new EditBox();
    public EditBox inMailUserName = new EditBox();
    public EditBoxPassword inMailPassword = new EditBoxPassword();
    public CheckBox activeOutMail = new CheckBox("label:active");
    public ComboBoxCode outMailType = new ComboBoxCode("option:mail:protocol:send");
    public EditBox outMailServer = new EditBox();
    public EditBox outMailPort = new EditBox();
    public ComboBoxCode outMailAuth = new ComboBoxCode("option:mail:auth");
    public EditBox outMailUserName = new EditBox();
    public EditBoxPassword outMailPassword = new EditBoxPassword();
    public EditBox path = new EditBox();

    /** Constructor. */
    public LocalHost(MainFrame frame) {
        super(frame);
        name.setMustSave(true);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeader();
        add("label:port", port);
        addInstruction("instruction:port");
        add("label:encryptkey", encryptKey);
        add(allowReplication);
        addInstruction("instruction:localserver");
        addComments();
        addAudit();
        addPanel("label:server");
        nextPanel();
        add(activeInMail);
        add("label:type", inMailType);
        add("label:server", inMailServer);
        add("label:mailport", inMailPort);
        add("label:username", inMailUserName);
        add("label:password", inMailPassword);
        addInstruction("instruction:localinformation");
        addPanel("label:inmailbox");
        nextPanel();
        add(activeOutMail);
        add("label:type", outMailType);
        add("label:server", outMailServer);
        add("label:mailport", outMailPort);
        add("label:auth", outMailAuth);
        add("label:username", outMailUserName);
        add("label:password", outMailPassword);
        addInstruction("instruction:localinformation");
        addPanel("label:outmailbox");
        nextPanel();
        add("label:path", path);
        addPanel("label:pathcode");
        nextPanel();
    }

    /** Indicates if the panel can be deleted. */
    public boolean canDelete() {
        return false;
    }
}
