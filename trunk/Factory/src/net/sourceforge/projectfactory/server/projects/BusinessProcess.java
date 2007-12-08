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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/projects/BusinessProcess.java,v $
$Revision: 1.20 $
$Date: 2007/02/12 16:12:27 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.projects;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Defines a business process, which is a list of tasks defined in order to generate projects.
 * @author David Lambert
 */
public class BusinessProcess extends Entity {

    /** Status for the tasks list. */
    private int statusTasks;

    /** Tasks list. */
    public List<Task> tasks = new ArrayList(20);

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags)
            xmlStart(xml, "businessprocess");

        super.xmlOut(xml, transaction, false);

        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "statustasks", statusTasks);

            for (Task task: tasks)
                task.xmlOut(xml, transaction, true);

            if (transaction.isDetail()) {
                for (Project project: 
                     transaction.getServer().projects.projects) {
                    if (project.isActive() && 
                        (project.businessProcess == this)) {
                        xmlStart(xml, "project");
                        xmlAttribute(xml, "name", project.getName());
                        xmlAttribute(xml, "begin", project.getBegin());
                        xmlAttribute(xml, "target", project.getTarget());
                        xmlAttribute(xml, "lead", project.getLead());
                        xmlEnd(xml);
                    }
                }
            }
        }

        if (tags)
            xmlEnd(xml);
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("task")) 
            return new BaseEntityServerXML(transaction, new Task(), tasks);
        return null;
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) {
            return true;
        }

        if (tag.equals("statustasks")) {
            statusTasks = xmlInInt(xml, value);
            return true;
        }

        return false;
    }

    /** Initializes the business process. */
    public void create(TransactionXML transaction) {
        super.create(transaction);
        for (Task task: tasks)
            task.create(transaction);
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) {
            return;
        }

        BusinessProcess otherBusinessProcess = (BusinessProcess)other;
        super.update(transaction, other);
        this.statusTasks = otherBusinessProcess.statusTasks;
        updateTasks(transaction, tasks, otherBusinessProcess.tasks);
    }

    /**
     * Update the tasks list from another list.
	 * Tasks are updated based on object IDs, instead of a single move.
     */
    protected void updateTasks(TransactionXML transaction, List list, 
                               List listOther) {
        Task otherItem;
        Task thirdItem;
        List<Task> listInter = new ArrayList(10);
        int inter;
        int indexOther;
        int size = listOther.size();

        for (inter = 0; inter < size; inter++) {
            otherItem = (Task)listOther.get(inter);
            indexOther = list.indexOf(otherItem);

            if (indexOther >= 0) {
                thirdItem = (Task)list.get(indexOther);
                thirdItem.update(transaction, otherItem);
                listInter.add(thirdItem);
            } else {
                listInter.add(otherItem);
            }
        }

        for (inter = 0; inter < list.size(); inter++) {
            thirdItem = (Task)list.get(inter);

            if (!listOther.contains(thirdItem)) {
                boolean found = false;

                /* Update any plan, forecast or status */
                for (Plan plan: transaction.getServer().projects.plans) {
                    if (plan.references(thirdItem)) {
                        found = true;
                        break;
                    }
                }

                for (Forecast forecast: 
                     transaction.getServer().projects.forecasts) {
                    if (forecast.references(thirdItem)) {
                        found = true;
                        break;
                    }
                }

                for (Status status: 
                     transaction.getServer().projects.statuses) {
                    if (status.references(thirdItem)) {
                        found = true;
                        break;
                    }
                }

                for (Tracking tracking: 
                     transaction.getServer().projects.trackings) {
                    if (tracking.references(thirdItem)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    thirdItem.actionStatus = 6;
                    listInter.add(thirdItem);
                }
            }
        }

        list.clear();
        list.addAll(listInter);
        
        // Update items based on level
         for (inter = 0; inter < list.size()-1; inter++) {
             thirdItem = (Task)list.get(inter);
             otherItem = (Task)list.get(inter+1);
             if(otherItem.level > thirdItem.level) 
                thirdItem.reset();
         }
        
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);
        statusTasks = 1;
    }

    /** Assigns a key search string for items. */
    public void setSearchKey() {
        for (Task task: tasks)
            task.setSearchKey(getName());
    }
}
