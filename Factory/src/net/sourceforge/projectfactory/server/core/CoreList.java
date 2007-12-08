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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/core/FactoryCore.java,v $
$Revision: 1.15 $
$Date: 2007/02/27 22:12:14 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.core;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.projectfactory.server.ApplicationServerBase;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.SubEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Aggregates all the core objects.
 * @author David Lambert
 */
public class CoreList extends ApplicationServerBase {

    /** List of packages. */
    public List<Package> packages = new ArrayList(500);

    /** List of grids. */
    public List<Grid> grids = new ArrayList(40);

    /** List of panels. */
    public List<Panel> panels = new ArrayList(40);

    /** List of classes. */
    public List<ClassDefinition> classes = new ArrayList(40);

    /** List of panel codes. */
    public List<PanelCode> panelCodes = new ArrayList(40);

    /** List of class codes. */
    public List<ClassCode> classCodes = new ArrayList(40);

    /** Constructor. */
    public CoreList(ApplicationServerBase serverBase) {
        super(serverBase);
    }

    /** Clears objects. */
    public void clear() {
		packages.clear();
		grids.clear();
		panels.clear();
		classes.clear();
        panelCodes.clear();
        classCodes.clear();
    }

    /** Sorts objects. */
    public void sort() {
        Collections.sort(packages);
        Collections.sort(grids);
        Collections.sort(panels);
        Collections.sort(classes);
        Collections.sort(panelCodes);
        Collections.sort(classCodes);
    }

    /** Saves all entities. */
    public void saveAll(WriterXML xml, TransactionXML transaction, 
                        boolean demo, String iid) {
        for (String extension: 
                transaction.getServer().getApplicationExtensions()) {
            savePackages(xml, transaction, extension, iid);
        }
    }

    /** Saves all packages. */
    private void savePackages(WriterXML xml, TransactionXML transaction, 
                              String application, String iid) {
        for (Package pack: packages)
            if (pack.getApplication().equals(application))
				saveEntity(xml, transaction, pack, false, iid);
        for (Grid grid: grids)
            if (grid.getApplication().equals(application))
				saveEntity(xml, transaction, grid, false, iid);
        for (Panel panel: panels)
            if (panel.getApplication().equals(application))
				saveEntity(xml, transaction, panel, false, iid);
        for (ClassDefinition classDefinition: classes)
            if (classDefinition.getApplication().equals(application))
				saveEntity(xml, transaction, classDefinition, false, iid);
        for (PanelCode panelCode: panelCodes)
            if (panelCode.getApplication().equals(application))
				saveEntity(xml, transaction, panelCode, false, iid);
        for (ClassCode classCode: classCodes)
            if (classCode.getApplication().equals(application))
				saveEntity(xml, transaction, classCode, false, iid);
    }

    /** Saves all objects in file(s). */
    public void saveAll(WriterXML xml, TransactionXML transaction) {
        for (String extension: 
				transaction.getServer().getApplicationExtensions()) {

			String filename = transaction.getServer().getPath() + 
								XMLWrapper.SLASH + "system" + 
								extension + ".xml";
			try {
				WriterXML outputXml = 
					new WriterXML(filename, "factory");
				savePackages(outputXml, transaction, extension, null);
				outputXml.end();
				traceSaving(xml, filename);
			} catch (IOException ex) {
				traceError(xml, filename);
			}
		}
    }

    /** Adds files to be opened. */
    public void addFileList(List<String> files, String prefix) {
        files.add(prefix + "system.xml");
    }

    /** Adds classes used for replication. */
    public void addClassList(List classes) {
        classes.add("package");
        classes.add("grid");
        classes.add("panel");
        classes.add("classdefinition");
        classes.add("panelcode");
        classes.add("classcode");
    }

    /** Sends the object names to the client based on a list. */
    public boolean isAvailable(TransactionXML transaction, String category, 
                               String filter, Entity entity) {
        if (category.equals("system")) {
            if (filter.equals("filter:system:drafts")) {
                if(!((CoreEntity)entity).draft)
                    return false;
            }
        }
        return true;
    }

    /** Creates a list based on category. */
    public void listByCategory(TransactionXML transaction, List lists, 
                               String category, String filter) {
        if (category.equals("system")) {
            if (filter.equals("filter:system:packages"))
                lists.add(packages);
            if (filter.equals("filter:system:grids"))
                lists.add(grids);
            if (filter.equals("filter:system:panels"))
                lists.add(panels);
            if (filter.equals("filter:system:classes"))
                lists.add(classes);
            if (filter.equals("filter:system:drafts")) {
                lists.add(panels);
                lists.add(classes);
            }
        }
    }

    /** Creates the list based on class. */
    public void listByClass(TransactionXML transaction, List lists, 
                            String classname) {
        if (classname.equals("grid"))
            lists.add(grids);
        if (classname.equals("classdefinition"))
            lists.add(classes);
        if (classname.equals("panel"))
            lists.add(panels);
        if (classname.equals("package"))
            lists.add(packages);
        if (classname.equals("panelcode"))
            lists.add(panelCodes);
        if (classname.equals("classcode"))
            lists.add(classCodes);
    }

    /** Returns the appropriate parser in order to read an element. */
    public SubEntityServerXML getParser(TransactionXML transaction, String tag) {
        if (tag.equals("package")) 
            return new SubEntityServerXML(this, transaction, new Package(), packages);
        else if (tag.equals("grid")) 
            return new SubEntityServerXML(this, transaction, new Grid(), grids);
        else if (tag.equals("panel")) 
            return new SubEntityServerXML(this, transaction, new Panel(), panels);
        else if (tag.equals("classdefinition")) 
            return new SubEntityServerXML(this, transaction, new ClassDefinition(), classes);
        else if (tag.equals("panelcode")) 
            return new SubEntityServerXML(this, transaction, new PanelCode(), panelCodes);
        else if (tag.equals("classcode")) 
            return new SubEntityServerXML(this, transaction, new ClassCode(), classCodes);
        return null;
    }
}
