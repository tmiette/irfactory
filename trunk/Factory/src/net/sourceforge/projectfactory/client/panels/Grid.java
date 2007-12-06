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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Grid.java,v $
$Revision: 1.2 $
$Date: 2007/02/12 09:41:01 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.components.ComboBoxes.ComboBoxCode;
import net.sourceforge.projectfactory.client.components.ComboBoxes.ComboBoxCodeApplication;
import net.sourceforge.projectfactory.client.components.EditBoxes.EditBoxLookup;
import net.sourceforge.projectfactory.client.components.TableBoxes.TableBoxLookup;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used in order to define grids that are used in panels.
  * @author David Lambert
  */
public class Grid extends PanelData {
    protected ComboBoxCodeApplication application = new ComboBoxCodeApplication(this);
    protected EditBoxLookup packageReference = new EditBoxLookup(this, "package");
    protected CheckBox hierarchical = new CheckBox("label:hierarchical");
    protected CheckBox noSort = new CheckBox("label:nosort");
    protected CheckBox noButton = new CheckBox("label:nobutton");
    protected TableBoxLookup content = new TableBoxLookup(
                                "xmltag","label:xmltag",100,
                                "label","label:label",100,
                                "type","label:type",50,
                                "length","label:length",10,
                                "readonly","label:readonly",10,
                                "duplicate","label:autoduplicate",5,
                                "defaultvalue","label:default",10,
                                "combobox","label:combobox",20,
                                "lookupclass","label:lookupclass",20,
                                "lookupgrid","label:lookupgrid",20);

    /** Constructor. */
    public Grid(MainFrame frame) {
        super(frame);
        content.setNoSort();
        content.attachLookup(1,this,"dictionary");
        content.setCombo(2, new ComboBoxCode("grid:item:type"));
        content.setColumnDefault("string", 2);
        content.setIntegerType(3);
        content.setBooleanType(4);
        content.setBooleanType(5);
        content.attachLookup(7,this,"combobox");
        content.attachLookup(8,this,"classdefinition");
        content.attachLookup(9,this,"grid");
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        addHeaderName();
        add("label:application", application);
        add("label:package", packageReference);
        add(hierarchical);
        add(noSort);
        add(noButton);
        addComments();
        addAudit();
        addPanel("label:grid");
        nextPanel();
        add("", content);
        addPanel("label:content");
        nextPanel();
    }
}
