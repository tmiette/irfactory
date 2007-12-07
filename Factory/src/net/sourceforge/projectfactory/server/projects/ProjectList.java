/*

Copyright (c) 2005, 2006 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/FactoryProject.java,v $
$Revision: 1.28 $
$Date: 2007/02/07 18:12:57 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.projectfactory.server.ApplicationServerBase;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.SubEntityServerXML;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;
import net.sourceforge.projectfactory.xml.server.TransactionXML;


/**
 * Aggregates all the objects which define projects.
 * @author David Lambert
 */
public class ProjectList extends ApplicationServerBase {

    /** List of projects. */
    public List<Project> projects = new ArrayList(10);

    /** List of business processes. */
    public List<BusinessProcess> businessProcesses = new ArrayList(5);

    /** List of plans. */
    public List<Plan> plans = new ArrayList(40);

    /** List of forecasts. */
    public List<Forecast> forecasts = new ArrayList(40);

    /** List of statuses. */
    public List<Status> statuses = new ArrayList(50);

    /** List of trackings. */
    public List<Tracking> trackings = new ArrayList(50);

    /** Constructor. */
    public ProjectList(ApplicationServerBase serverBase) {
        super(serverBase);
    }

    /** Clears objects. */
    public void clear() {
        businessProcesses.clear();
        projects.clear();
        plans.clear();
        forecasts.clear();
        statuses.clear();
    }

    /** Sorts objects. */
    public void sort() {
        Collections.sort(businessProcesses);
        Collections.sort(projects);
        Collections.sort(plans);
        Collections.sort(forecasts);
        Collections.sort(statuses);
        Collections.sort(trackings);
    }

    /** Saves all entities. */
    public void saveAll(WriterXML xml, TransactionXML transaction, 
                        boolean demo, String iid) {
        saveProjects(xml, transaction, demo, iid);
        savePlans(xml, transaction, demo, iid);
        saveForecasts(xml, transaction, demo, iid);
        saveStatuses(xml, transaction, demo, iid);
        saveTrackings(xml, transaction, demo, iid);
    }

    /** Saves all projects. */
    public void saveProjects(WriterXML xml, TransactionXML transaction, 
                             boolean demo, String iid) {
        saveEntity(xml, transaction, businessProcesses, demo, iid);
        saveEntity(xml, transaction, projects, demo, iid);
    }

    /** Saves all plan. */
    public void savePlans(WriterXML xml, TransactionXML transaction, 
                             boolean demo, String iid) {
        saveEntity(xml, transaction, plans, demo, iid);
    }

    /** Saves all forecasts. */
    public void saveForecasts(WriterXML xml, TransactionXML transaction, 
                              boolean demo, String iid) {
        saveEntity(xml, transaction, forecasts, demo, iid);
    }

    /** Saves all statuses. */
    public void saveStatuses(WriterXML xml, TransactionXML transaction, 
                             boolean demo, String iid) {
        saveEntity(xml, transaction, statuses, demo, iid);
    }

    /** Saves all trackings. */
    public void saveTrackings(WriterXML xml, TransactionXML transaction, 
                              boolean demo, String iid) {
        saveEntity(xml, transaction, trackings, demo, iid);
    }

    /** Saves all objects in file(s). */
    public void saveAll(WriterXML xml, TransactionXML transaction) {
        String filename = transaction.getServer().getPath() + 
                            XMLWrapper.SLASH + "projects.xml";
        try {
            WriterXML outputXml = 
                new WriterXML(filename, "factory");
            saveProjects(outputXml, transaction, false, null);
            outputXml.end();
            traceSaving(xml, filename);
        } catch (IOException ex) {
            traceError(xml, filename);
        }

        filename = transaction.getServer().getPath() + 
                        XMLWrapper.SLASH + "plans.xml";
        try {
            WriterXML outputXml = 
                new WriterXML(filename, "factory");
            savePlans(outputXml, transaction, false, null);
            outputXml.end();
            traceSaving(xml, filename);
        } catch (IOException ex) {
            traceError(xml, filename);
        }

        filename = transaction.getServer().getPath() + 
                        XMLWrapper.SLASH + "forecasts.xml";
        try {
            WriterXML outputXml = 
                new WriterXML(filename, "factory");
            saveForecasts(outputXml, transaction, false, null);
            outputXml.end();
            traceSaving(xml, filename);
        } catch (IOException ex) {
            traceError(xml, filename);
        }

        filename = transaction.getServer().getPath() + 
                        XMLWrapper.SLASH + "statuses.xml";
        try {
            WriterXML outputXml = 
                new WriterXML(filename, "factory");
            saveStatuses(outputXml, transaction, false, null);
            outputXml.end();
            traceSaving(xml, filename);
        } catch (IOException ex) {
            traceError(xml, filename);
        }

        filename = transaction.getServer().getPath() + 
                    XMLWrapper.SLASH + "trackings.xml";
        try {
            WriterXML outputXml = 
                new WriterXML(filename, "factory");
            saveTrackings(outputXml, transaction, false, null);
            outputXml.end();
            traceSaving(xml, filename);
        } catch (IOException ex) {
            traceError(xml, filename);
        }
    }

    /** Adds files to be opened. */
    public void addFileList(List<String> files, String prefix) {
        files.add(prefix + "projects.xml");
        files.add(prefix + "plans.xml");
        files.add(prefix + "forecasts.xml");
        files.add(prefix + "statuses.xml");
        files.add(prefix + "trackings.xml");
    }

    /** Adds classes used for replication. */
    public void addClassList(List classes) {
        classes.add("businessprocess");
        classes.add("project");
        classes.add("plan");
        classes.add("forecast");
        classes.add("status");
        classes.add("tracking");
    }

    /** Sends the object names to the client based on a list. */
    public boolean isAvailable(TransactionXML transaction, String category, 
                               String filter, Entity entity) {
        if (category.equals("projects")) {
            if (filter.equals("filter:projects:mine"))
                if (!entity.isActive() || 
                    !((Project)entity).isMember(transaction.getSession().getOperator()))
                    return false;
            if (filter.equals("filter:projects:active"))
                if (!entity.isActive())
                    return false;
            if (filter.equals("filter:projects:inactive"))
                if (entity.isActive())
                    return false;
            if (filter.equals("filter:businessprocess:active"))
                if (!entity.isActive())
                    return false;
            if (filter.equals("filter:businessprocess:inactive"))
                if (entity.isActive())
                    return false;
        }
        return true;
    }

    /** Creates a list based on category. */
    public void listByCategory(TransactionXML transaction, List lists, 
                               String category, String filter) {
        if (category.equals("projects")) {
            if (filter.startsWith("filter:projects"))
                lists.add(projects);
            if (filter.startsWith("filter:businessprocess") || 
                filter.equals("filter:projects:all"))
                lists.add(businessProcesses);
        }
    }

    /** Creates the list based on class. */
    public void listByClass(TransactionXML transaction, List lists, 
                            String classname) {
        if (classname.equals("project"))
            lists.add(projects);
        else if (classname.equals("businessprocess"))
            lists.add(businessProcesses);
        else if (classname.equals("plan"))
            lists.add(plans);
        else if (classname.equals("forecast"))
            lists.add(forecasts);
        else if (classname.equals("tracking"))
            lists.add(trackings);
        else if (classname.equals("status"))
            lists.add(statuses);
        else if (classname.equals("task")) {
            for (Project project: projects) {
                project.setSearchKey();
                lists.add(project.tasks);
            }
        }
    }

    /** Returns the appropriate parser in order to read an element. */
    public SubEntityServerXML getParser(TransactionXML transaction, String tag) {
        if (tag.equals("project")) 
            return new SubEntityServerXML(this, transaction, new Project(), projects);
        else if (tag.equals("plan")) 
            return new SubEntityServerXML(this, transaction, new Plan(), plans);
        else if (tag.equals("forecast")) 
            return new SubEntityServerXML(this, transaction, new Forecast(), forecasts);
        else if (tag.equals("status")) 
            return new SubEntityServerXML(this, transaction, new Status(), statuses);
        else if (tag.equals("tracking")) 
            return new SubEntityServerXML(this, transaction, new Tracking(), trackings);
        else if (tag.equals("businessprocess")) 
            return new SubEntityServerXML(this, transaction, new BusinessProcess(), businessProcesses);
        return null;
    }
}
