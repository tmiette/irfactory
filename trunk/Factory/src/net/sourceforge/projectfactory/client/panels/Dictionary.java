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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Dictionary.java,v $
$Revision: 1.2 $
$Date: 2007/02/12 09:41:01 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.components.ComboBoxes.ComboBoxCodeApplication;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used for the definitions of dictionaries.
  * @author David Lambert
  */
public class Dictionary extends PanelData {
    protected ComboBoxCodeApplication application = new ComboBoxCodeApplication(this);
    protected TextBox english = new TextBox();
    protected TextBox french = new TextBox();
    protected TextBox german = new TextBox();
    protected TextBox spanish = new TextBox();
    protected TextBox arabic = new TextBox();

    /** Constructor. */
    public Dictionary(MainFrame frame) {
        super(frame);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        add("label:application", application);
        add("label:english", english);
        add("label:french", french);
        add("label:german", german);
        add("label:spanish", spanish);
        add("label:arabic", arabic);
        addAudit();
        addPanel("label:dictionary");
        nextPanel();
    }
}
