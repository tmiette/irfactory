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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/Connexion.java,v $
$Revision: 1.2 $
$Date: 2007/02/12 09:41:01 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.components.TableBoxes.TableBox;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Panel used in order to display active connexions.
  * @author David Lambert
  */
public class Connexion extends PanelData {
    protected TableBox connexion = new TableBox(
                                "username","label:username",50,
                                "networkid","label:networkid",30,
                                "os","label:os",40,
                                "build","label:build",40,
                                "host","label:host",80,
                                "requests","label:requests",10);
    protected LabelBox serverBuild = new LabelBox();
    protected LabelBox osname = new LabelBox();
    protected LabelBox osarch = new LabelBox();
    protected LabelBox osversion = new LabelBox();
    protected LabelBox javavmname = new LabelBox();
    protected LabelBox javavmversion = new LabelBox();
    protected LabelBox javavmvendor = new LabelBox();
    protected LabelBox javahome = new LabelBox();
    protected LabelBox javaclasspath = new LabelBox();
    protected LabelBox userhome = new LabelBox();
    protected LabelBox userdir = new LabelBox();

    /** Constructor. */
    public Connexion(MainFrame frame) {
        super(frame);
        connexion.setIntegerType(5);
        connexion.setEnabler(false);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        add("", connexion);
        add("label:serverbuild", serverBuild);
        addPanel("label:connexion");
        nextPanel();
        add("label:osname", osname);
        add("label:osarch", osarch);
        add("label:osversion", osversion);
        add("label:javavmname", javavmname);
        add("label:javavmversion", javavmversion);
        add("label:javavmvendor", javavmvendor);
        add("label:javahome", javahome);
        add("label:javaclasspath", javaclasspath);
        add("label:userhome", userhome);
        add("label:userdir", userdir);
        addPanel("label:system");
        nextPanel();
    }

    /** Indicates if the panel can be edited. */
    public boolean canEdit() {
        return false;
    }

    /** Indicates if the panel can be deleted. */
    public boolean canDelete() {
        return false;
    }

    /** Indicates if the panel can be saved. */
    public boolean canSave() {
        return false;
    }
}
