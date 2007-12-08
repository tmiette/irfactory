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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/panels/About.java,v $
$Revision: 1.2 $
$Date: 2007/02/12 09:41:11 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.client.panels;

import net.sourceforge.projectfactory.client.MainFrame;
import net.sourceforge.projectfactory.client.components.*;
import net.sourceforge.projectfactory.client.panels.PanelData;

/** 
  * Displays information about the system.
  * @author David Lambert
  */
public class About extends PanelData {
    protected LabelBox title = new LabelBox();
    protected LabelBox copyright = new LabelBox();
    protected LabelBox license = new LabelBox();
    protected LabelBox homepage = new LabelBox();
    protected LabelBox devpage = new LabelBox();
    protected LabelBox bugpage = new LabelBox();
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
    protected TextBox licenseText = new TextBox();

    /** Constructor. */
    public About(MainFrame frame) {
        super(frame);
        licenseText.setEnabler(false);
    }

    /** Initialization (UI). */
    public void init(String tagpanel) throws Exception {
        super.init(tagpanel);
        add("label:title", title);
        add("label:copyright", copyright);
        add("label:license", license);
        add("label:homepage", homepage);
        add("label:devpage", devpage);
        add("label:bugpage", bugpage);
        addPanel("label:about");
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
        add("", licenseText);
        addPanel("label:license");
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

    /** Manages panel content after it's populated from XML. */
    public void exitXmlIn() {
        super.exitXmlIn();
        licenseText.setText(LocalMessage.get("text:license"));
        title.setText(getFrame().getBuild());
        copyright.setText(getFrame().getCopyright());
        license.setText(getFrame().getLicense());
        homepage.setText(getFrame().getHomePage());
        devpage.setText(getFrame().getDevPage());
        bugpage.setText(getFrame().getBugPage());
        osname.setText(System.getProperty("os.name"));
        osarch.setText(System.getProperty("os.arch"));
        osversion.setText(System.getProperty("os.version"));
        javavmname.setText(System.getProperty("java.vm.name"));
        javavmversion.setText(System.getProperty("java.vm.version"));
        javavmvendor.setText(System.getProperty("java.vm.vendor"));
        javahome.setText(System.getProperty("java.home"));
        javaclasspath.setText(System.getProperty("java.class.path"));
        userhome.setText(System.getProperty("user.home"));
        userdir.setText(System.getProperty("user.dir"));
    }
}
