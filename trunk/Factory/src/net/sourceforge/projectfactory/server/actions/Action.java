/*

Copyright (c) 2006 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actions/Action.java,v $
$Revision: 1.41 $
$Date: 2007/02/27 22:13:11 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.actions;

import java.util.List;

import net.sourceforge.projectfactory.server.actors.Actor;
import net.sourceforge.projectfactory.server.actors.Member;
import net.sourceforge.projectfactory.server.actors.Team;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.projects.Forecast;
import net.sourceforge.projectfactory.server.projects.ForecastItem;
import net.sourceforge.projectfactory.server.projects.Plan;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * Defines an action or an answer.
 * @author David Lambert
 */
public class Action extends ActionBase implements Comparable {

	/** Forecast. */
	private Forecast forecast;

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags) xmlStart(xml, "action");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail()) {
            if (isAnswer()) {
                Action parent = (Action)getParent(transaction, getParentIid());
                if (parent != null) {
                    xmlOut(xml, "status", parent.status);
                    xmlOut(xml, transaction, "project", parent.project);
                    xmlOut(xml, transaction, "task", parent.task);
                    xmlOut(xml, "begin", parent.begin);
                    xmlOut(xml, "target", parent.target);
                    parent.xmlOut(xml, transaction, steps);
                    parent.xmlOut(xml, transaction, items);
                }
            }
        } 
        else if (transaction.isExpand() || transaction.isSummary()) {
            xmlOutExpandAnswers(xml, transaction);
        }

        if (tags) xmlEnd(xml);
    }

    /** Writes associated plans as an XML output. */
    private void xmlOutExpandAnswers(WriterXML xml, 
                                     TransactionXML transaction) {
        for (Action action:transaction.getServer().actions.actions) {
            if (action.getParentIid() != null && 
                action.getParentIid().equals(getIid())) {
                xmlStart(xml, "action");
                action.xmlSummary(xml);
                action.xmlOutExpandAnswers(xml, transaction);
                xmlEnd(xml);
            }
        }
    }

    /** Writes the object as an XML output. */
    public void xmlOutRecipients(WriterXML xml, TransactionXML transaction) {
		for (Recipient recipient: recipients) 
			if(recipient.actor != null) 
				recipient.actor.xmlOutRecipient(xml, transaction);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;

        if (tag.equals("plan")) {
            Plan plan = (Plan) xmlInEntity(xml, 
                                        transaction, 
                                        value, 
                                        new Plan(), 
                                        transaction.getServer().projects.plans, 
                                        null, 
                                        this);
            if(plan != null)
                project = plan.project;
            return true;
        }

        return false;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        super.update(transaction, other);
        attachForecast(transaction);
    }

	/** Adds prerequisites to the list. */
	public void addPrerequisites(TransactionXML transaction, List<Entity> prerequisites) {
        if (getParentIid() != null) {
            for (Action parent:transaction.getServer().actions.actions) {
                if (parent.getIid().equals(getParentIid())) {
					addPrerequisites(transaction, prerequisites, parent);
					break;
				}
			}
		}
		if(attachProject)
			addPrerequisites(transaction, prerequisites, project);
		if(attachForecast)
			addPrerequisites(transaction, prerequisites, forecast);
		for (Recipient recipient: recipients)
			addPrerequisites(transaction, prerequisites, recipient.actor);
	}

    /** Attaches forecast. */
    public void attachForecast(TransactionXML transaction) {
        begin = null;
        target = null;
        if (project != null) {
            forecast = project.getLatestActiveForecast(transaction);
            if (forecast != null) {
                items.clear();
                for (ForecastItem item: forecast.items) {
                    if (item.task != null &&
                        item.dateItem != null &&
                            item.task.equals(task)) {
                        ActionItem newItem = new ActionItem();
                        newItem.dateItem = item.dateItem;
                        newItem.duration = item.duration;
                        newItem.durationType = item.durationType;
                        if (item.actor != null)
                            newItem.assigned = item.actor.getName();
                        if (item.anonymous != null)
                            newItem.assigned = item.anonymous.getName();
                        newItem.completion = item.completion;
                        newItem.purpose = item.task.getName();
                        items.add(newItem);

                        if (item.dateItem != null) {
                            if (begin == null)
                                begin = item.dateItem;
                            else if (item.dateItem.before(begin))
                                begin = item.dateItem;
                            if (target == null)
                                target = item.dateItem;
                            else if (item.dateItem.after(target))
                                target = item.dateItem;
                        }

                        if (item.actor != null) {
                            boolean found = false;
                            for (Recipient recipient: recipients) {
                                if (recipient.actor == item.actor) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                Recipient recipient = new Recipient(this);
                                recipient.actor = item.actor;
                                recipient.role = 2;
                                recipient.status = Recipient.NA;
                                recipients.add(recipient);
                            }
                        }
                    }
                }
            }
        }
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);
        if (getParentIid() != null) {
            for (Action parent:transaction.getServer().actions.actions) {
                if (parent.getIid().equals(getParentIid())) {
                    name = "> " + parent.name;
                    project = parent.project;
                    task = parent.task;
                    update(recipients, parent.recipients);
                    for (Recipient recipient: recipients) {
                        if (recipient.actor != null)
                            recipient.status = 
                                    !recipient.actor.equals(
										transaction.getSession().getOperator()) ? 
                                    Recipient.NOT_SENT : Recipient.NA;
                    }
                    break;
                }
            }
        } else {
            Recipient recipient = new Recipient(this);
            recipient.actor = transaction.getSession().getOperator();
            recipient.role = 5;
            recipient.status = Recipient.NA;
            recipients.add(recipient);
            status = 1;
        }
        setName();
        attachForecast(transaction);
    }

    /** Indicates if it's an answer. */
    public boolean isAnswer() {
        return getParentIid() != null && getParentIid().length() > 0;
    }

    /** Returns details. */
    public String getDetails() {
        return details != null ? details : "";
    }

    /** Indicates if the action is for the actor. */
    public boolean isForActor(TransactionXML transaction, Actor actor) {
        for (Recipient recipient: recipients) {
            if (recipient.actor == actor)
                return true;
        }
        return false;
    }

    /** Indicates if the action comes from  the actor. */
    public boolean isFromActor(Actor actor) {
        for (Recipient recipient: recipients) {
            if (recipient.actor == actor && 
					getUpdatedBy().equals(actor.getName()))
                return true;
        }
        return false;
    }

    /** Indicates if the action is for the team members of actor. */
    public boolean isForActorMembers(TransactionXML transaction, Actor actor) {
        for (Team team: transaction.getServer().actors.teams) {
            if (team.isMember(actor)) {
                for(Member member:team.members) {
                    if(isForActor(transaction, member.actor))
                        return true;
                }
            }
        }
        return false;
    }

    /** Sets action name. */
    private void setName() {
        if (getName().length() == 0 && task != null)
            setName(task.getName());
    }

    /** Validates the object before any save or update. */
    public boolean xmlValidate(WriterXML xml, TransactionXML transaction, 
                            List list) {
        setName();

        if(super.xmlValidate(xml, transaction, list))
            return true;

        for (Recipient recipient: recipients) {
            if (recipient.actor != null)
                recipient.status = 
                        recipient.actor.equals(transaction.getSession().getOperator()) ? 
                        Recipient.NA : Recipient.NOT_SENT;
        }
        if (!active)
            xmlWarning(xml, "warning:action:inactive", "", "");
            
        return false;
    }

    /** Indicates if the action has been sent or not. */
    private boolean isSent() {
        for (Recipient recipient: recipients) {
            if (recipient.status == Recipient.NOT_SENT)
                return false;
        }
        return true;
    }

    /** Indicates if the action has errors or not. */
    private boolean hasError() {
        for (Recipient recipient: recipients) {
            if (recipient.status == Recipient.ERROR)
                return true;
        }
        return false;
    }

    /** Compares this object with the specified object for order. */
    public int compareTo(Object object) {
        if(name == null || ((Action)object).name == null)
            return 0;
            
        if(status < ((Action)object).status)
            return -1;

        if(status > ((Action)object).status)
            return 1;
            
        return name.compareToIgnoreCase(((Action)object).name);
    }

    /** Provides a summary when the object is displayed in a list. */
    public String getSummary() {
        String indicator = "";
        if (isActive()) {
            if (!isSent())
                indicator = "@label:underline";
            else if (hasError())
                indicator = "@label:red";
            else if (status == 2)
                indicator = "@label:green";
            else if (status == 4)
                indicator = "@label:red";
        } else
            indicator = "@label:inactive";

        return getName() + indicator;
    }

    /** Provides a tip to be displayed in a list. */
    public String getTip() {
        if (details == null)
            return "";
        return details.length() > 150 ? details.substring(0, 150) + "..." : 
               details;
    }

    /** Indicates if the action is pending. */    
    public boolean isPending() {
        if(isActive()) {
            return status == 1 || status == 2 || status == 4 || status == 5;
        }
        return false;
    }
    
    /** Indicates if the action is complete. */    
    public boolean isComplete() {
        if(isActive()) {
            return status == 3 || status == 6;
        }
        return true;
    }

	/** Called after replication of the object. */
	public void afterReplication(WriterXML xml, 
									TransactionXML transaction,
									List list) {
		super.afterReplication(xml, transaction, list);
		boolean found = false;
        for (Recipient recipient: recipients) {
            if (recipient.actor != null) {
				if(recipient.actor.equals(transaction.getSession().getOperator())) {
					recipient.status = Recipient.RECEIVED;
					found = true;
				}
				else 
					recipient.status = Recipient.SENT;
			}
        }
		if(found) 
			xmlWarning(xml, "warning:newaction", getName());
	}
}
