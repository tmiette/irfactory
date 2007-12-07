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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actions/ActionBase.java,v $
$Revision: 1.2 $
$Date: 2007/03/04 21:04:59 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.actions;

import net.sourceforge.projectfactory.server.projects.Project;
import net.sourceforge.projectfactory.server.projects.Task;
import net.sourceforge.projectfactory.server.actions.Recipient;
import net.sourceforge.projectfactory.server.actions.Step;
import net.sourceforge.projectfactory.server.actions.ActionItem;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;

/** 
  * Defines an action or an answer.
  * @author David Lambert
  */
public class ActionBase extends Entity {
    public int status;
    public String details;
    public boolean attachProject;
    public boolean attachForecast;
    public java.util.Date begin;
    public java.util.Date target;
    public Project project;
    public Task task;
    public java.util.List<Recipient> recipients = new java.util.ArrayList();
    public java.util.List<Step> steps = new java.util.ArrayList();
    public java.util.List<ActionItem> items = new java.util.ArrayList();

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "action");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "status", status);
            xmlOut(xml, "details", details);
            xmlOut(xml, "attachproject", attachProject);
            xmlOut(xml, "attachforecast", attachForecast);
            xmlOut(xml, "begin", begin);
            xmlOut(xml, "target", target);
            xmlOut(xml, transaction, "project", project);
            xmlOut(xml, transaction, "task", task);
            xmlOut(xml, transaction, recipients);
            xmlOut(xml, transaction, steps);
            xmlOut(xml, transaction, items);
        }
        if (tags) xmlEnd(xml);
    }
    
    
    
    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("status")) {
            status = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("details")) {
            details = value;
            return true;
        }
        if (tag.equals("attachproject")) {
            attachProject = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("attachforecast")) {
            attachForecast = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("begin")) {
            begin = xmlInDate(xml, value);
            return true;
        }
        if (tag.equals("target")) {
            target = xmlInDate(xml, value);
            return true;
        }
        if (tag.equals("project") && transaction.getServer().projects != null) {
            project = (Project)xmlInEntity(xml,transaction,value,
                new Project(),transaction.getServer().projects.projects,
                "error:incorrect:project",this);
            return true;
        }
        if (tag.equals("task") && project != null) {
            task = (Task)xmlInEntity(xml,transaction,value,
                new Task(),project.tasks,
                "error:incorrect:task",this);
            return true;
        }
        return false;
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        if (tag.equals("recipient"))
            return new BaseEntityServerXML(transaction, new Recipient((Action)this),recipients);
        if (tag.equals("step"))
            return new BaseEntityServerXML(transaction, new Step(),steps);
        if (tag.equals("item"))
            return new BaseEntityServerXML(transaction, new ActionItem(),items);
        return null;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) return;
        ActionBase otherEntity = (ActionBase) other;
        super.update(transaction, other);
        this.status = otherEntity.status;
        this.details = otherEntity.details;
        this.attachProject = otherEntity.attachProject;
        this.attachForecast = otherEntity.attachForecast;
        this.begin = otherEntity.begin;
        this.target = otherEntity.target;
        this.project = otherEntity.project;
        this.task = otherEntity.task;
        update(this.recipients,otherEntity.recipients);
        update(this.steps,otherEntity.steps);
        update(this.items,otherEntity.items);
    }
    /** Indicates if the names must be unique in the system or not. */
    protected boolean hasUniqueName() {
        return false;
    }
}
