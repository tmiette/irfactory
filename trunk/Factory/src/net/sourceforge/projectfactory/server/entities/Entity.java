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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/entities/Entity.java,v $
$Revision: 1.47 $
$Date: 2007/02/22 15:35:54 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.entities;

import java.util.Date;
import java.util.List;

import net.sourceforge.projectfactory.middleware.FactorySession;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * Base class for every 'independent' object managed by the server. These
 * objects are referenced at least with a name, creation and update
 * identification with time stamps. They are managed by the server by lists of
 * objects.
 * @author David Lambert
 */
public abstract class Entity extends BaseEntity implements Comparable {

    /** Internal counter used in order to identify objects. */
    private static int instanceNumber;

    /** Name of the entity. */
    protected String name;

    /** Comment or Description. */
    protected String comment;

    /** Active flag. */
    protected boolean active;

    /** Creation time stamp. */
    protected Date created;

    /** Update time stamp. */
    protected Date updated;

    /** Create by (an actor). */
    private String createdBy;

    /** Update by (an actor). */
    private String updatedBy;

    /** Revision number. Each time the entity is updated,
      * revision number is increased. */
    protected int revision = 1;

    /** Object ID, unique identifier assigned during the creation of the object. */
    private String iid;

    /** Parent object ID. */
    private String parentIid;

    /** Key search string. */
    private String searchKey;

    /** Creation. Initializes the entity with a creation time stamp,
     *  actor and revision equal to one. */
    public void create(TransactionXML transaction) {
        reset();
        this.createdBy = transaction.getSession().getOperatorName();
        this.updatedBy = transaction.getSession().getOperatorName();
        this.created = new Date();
        this.updated = new Date();
        this.revision = 1;
        createIId();
    }

    /** Resets the create and update time stamps and actors. */
    public void reset() {
        this.createdBy = null;
        this.updatedBy = null;
        this.created = null;
        this.updated = null;
        this.iid = null;
    }

    /** Update method : updates the object from another entity.
     *  The attributes of the other entity
     *  are copied or attached to the entity.
     *  Revision number and time stamp and actor are also updated. */
    public void update(TransactionXML transaction, Entity entity) {
        this.name = entity.name;
        this.comment = entity.comment;
        this.active = entity.active;
        this.parentIid = entity.parentIid;

        if (transaction.isUpdate()) {
            update(transaction.getSession());
        } else {
            this.createdBy = entity.createdBy;
            this.updatedBy = entity.updatedBy;
            this.created = entity.created;
            this.updated = entity.updated;
            this.revision = entity.revision;
        }
    }

    /** Update last modified information. */
    public void update(FactorySession session) {
        this.updatedBy = session.getOperatorName();
        this.updated = new Date();
        if(this.createdBy == null)
            this.createdBy = this.updatedBy;
        if(this.created == null)
            this.created = this.updated;
        ++revision;
    }

    /** Compares this entity to the specified object. */
    public boolean equals(Object object) {
        if (object == this)
            return true;

        if (object == null || name == null)
            return false;

        if (iid != null && ((Entity)object).iid != null)
            return iid.equals(((Entity)object).iid);

        return name.equals(((Entity)object).name);
    }

    /** Compares this object with the specified object for order. */
    public int compareTo(Object object) {
        if(name == null || ((Entity)object).name == null)
            return 0;
            
        return name.compareToIgnoreCase(((Entity)object).name);
    }

    /** Returns entity's name. */
    public final String getName() {
        return name != null ? name : "";
    }

    /** Sets entity name. */
    public void setName(String name) {
        if (name.startsWith("::")) {
            // If the name starts with "::" it means it's composed
            // of the object ID followed by the name of the object.
            int indexName = name.indexOf(":", 2);
            if (indexName > 3) {
                this.iid = name.substring(2, indexName);
                this.name = name.substring(indexName + 1);
            } else
                this.iid = name;
        } else
            this.name = name;
    }

    /** Provides a summary when the object is displayed in a list. */
    public String getSummary() {
        return getName() + (active ? "" : "@label:inactive");
    }

    /** Provides a tip to be displayed in a list. */
    public String getTip() {
        if (comment == null)
            return "";
        return comment.length() > 150 ? comment.substring(0, 150) + "..." : 
               comment;
    }

    /** Returns revision number. */
    public int getRevision() {
        return revision;
    }

    /** Indicates if the entity matches with the search string. */
    public boolean matches(String search) {
        return (name != null && name.toLowerCase().indexOf(search) >= 0) || 
            (comment != null && comment.toLowerCase().indexOf(search) >= 0);
    }

    /** Indicates if the entity matches with the key search string. */
    public boolean matchesSearchKey(String searchKey) {
        return (this.searchKey != null && searchKey != null) ? 
               this.searchKey.equalsIgnoreCase(searchKey) : false;
    }

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        createIId();
        if (transaction.isDetail() || transaction.isSave()) {
            xmlAttribute(xml, "iid", iid);
            xmlAttribute(xml, "active", active);
            xmlAttribute(xml, "revision", revision);
            xmlAttribute(xml, "parentiid", parentIid);
            xmlAttribute(xml, "name", name);
            xmlAttributeFull(xml, "created", created);
            xmlAttributeFull(xml, "updated", updated);
            if (isDemo()) {
                xmlAttribute(xml, "createdby", "DEMO-FR-Victor Hugo");
                xmlAttribute(xml, "updatedby", "DEMO-FR-Victor Hugo");
            } else {
                xmlAttribute(xml, "createdby", createdBy);
                xmlAttribute(xml, "updatedby", updatedBy);
            }
            xmlOut(xml, "comment", comment);
        } else if (transaction.isSummary() || 
                    transaction.isShortSummary() || 
                    transaction.isExpand()) {
            xmlSummary(xml);
        }
    }

    /** Writes the object as an XML output. */
    public void xmlSummary(FactoryWriterXML xml) {
        xmlAttribute(xml, "iid", iid);
        xmlAttribute(xml, "name", name);
        xmlAttribute(xml, "summary", getSummary());
        xmlAttribute(xml, "tip", getTip());
        xmlAttributeFull(xml, "updated", updated);
    }

    /** Writes the object as an XML output. */
    public void xmlOutSummary(FactoryWriterXML xml, String tag) {
        xmlStart(xml, tag);
        xmlSummary(xml);
        xmlEnd(xml);
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        return null;
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, 
                         String tag, String value) {
        if (tag.equals("iid")) {
            iid = value;
            return true;
        }

        if (tag.equals("name")) {
            name = value;
            return true;
        }

        if (tag.equals("comment")) {
            comment = comment == null ? value : comment + "\n" + value;
            return true;
        }

        if (tag.equals("active")) {
            active = xmlInBoolean(value);
            return true;
        }

        if (tag.equals("created")) {
            created = xmlInDateFull(xml, value);
            return true;
        }

        if (tag.equals("updated")) {
            updated = xmlInDateFull(xml, value);
            return true;
        }

        if (tag.equals("createdby")) {
            if (value.startsWith("::")) {
                int indexName = value.indexOf(":", 2);
                if (indexName > 3) {
                    createdBy = value.substring(indexName + 1);
                } else
                    createdBy = value;
            } else
                createdBy = value;
            return true;
        }

        if (tag.equals("updatedby")) {
            if (value.startsWith("::")) {
                int indexName = value.indexOf(":", 2);
                if (indexName > 3) {
                    updatedBy = value.substring(indexName + 1);
                } else
                    updatedBy = value;
            } else
                updatedBy = value;
            return true;
        }

        if (tag.equals("revision")) {
            revision = xmlInInt(xml, value);
            return true;
        }

        if (tag.equals("parentiid")) {
            parentIid = value;
            return true;
        }

        return false;
    }

    /** Returns a string representation of the object. */
    public String toString() {
        return name;
    }

    /** Activates the entity. */
    public void setActive(TransactionXML transaction) {
        if(!active) {
            active = true;
            if(iid != null)
                update(transaction.getSession());
        }
    }

    /** Deactivates the entity. */
    public void setInactive(TransactionXML transaction) {
        if(active) {
            active = false;
            if(iid != null)
                update(transaction.getSession());
        }
    }

    /** Is the entity active? */
    public boolean isActive() {
        return active;
    }

    /** Returns an equality test on the name.
     *  That means the object are similar but not equal. */
    public boolean isSimilar(Object object) {
        if (object.getClass() != this.getClass()) 
            return false;

        if (name == null || ((Entity)object).name == null) 
            return false;

        return this.name.equalsIgnoreCase(((Entity)object).name);
    }

    /** Returns an inequality test on the object ID. */
    public boolean isDifferent(Object object) {
        if (object.getClass() != this.getClass()) 
            return true;

        if (iid == null || ((Entity)object).iid == null) 
            return false;

        return !this.iid.equals(((Entity)object).iid);
    }

    /** Indicates if the names must be unique in the system or not. */
    protected boolean hasUniqueName() {
        return true;
    }

    /** Validates the object before any save or update. */
    public boolean xmlValidate(FactoryWriterXML xml, TransactionXML transaction, 
                            List list) {
        if (name == null || name.length() == 0) {
            xmlError(xml, "error:required:name", "");
            return true;
        }

        if (this.hasUniqueName() && list != null) {
            for (Entity entity : (List<Entity>) list) {
                if (isSimilar(entity) && isDifferent(entity)) {
                    xmlError(xml, "error:duplicate:name", name);
                    return true;
                }
            }
        }
        return false;
    }

    /** Validates the object before delete. */
    public boolean xmlValidateDelete(FactoryWriterXML xml, 
                                  TransactionXML transaction) {
        return false;
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        active = true;
    }

    /** Creates an object ID. */
    public void createIId() {
        if (iid == null)
            iid = System.getProperty("user.name").toLowerCase() + 
                    System.currentTimeMillis() + 
                    ++instanceNumber;
    }
    
    /** Changes the object ID. */
    public void changeIId(TransactionXML transaction, String iid) {
        if(!this.iid.equals(iid)) {
            this.iid = iid;
            transaction.getServer().setAllDirty();
        }
    }

    /** Returns the modification date. */
    public Date getUpdated() {
        return updated;
    }

    /** Indicates if the entity is demo data. */
    public boolean isDemo() {
        return name != null && name.startsWith("DEMO-");
    }

    /** Assigns the key search string. */
    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    /** Returns the object ID. */
    public String getIid() {
        return iid;
    }
    
    /** Returns the parent object ID. */
    public String getParentIid() {
        return parentIid;
    }
	
	/** Returns who last updated the entity. */
	protected String getUpdatedBy() {
		return updatedBy != null ? updatedBy : "";
	}

    /** Returns the main (or first) entity. */
    protected Entity getParent(TransactionXML transaction, String parentIid) {
        for (Entity entity:transaction.getServer().actions.actions) {
            if (entity.iid != null && entity.iid.equals(parentIid)) {
                if (entity.parentIid != null && entity.parentIid.length() > 0)
                    return getParent(transaction, entity.parentIid);
                else
                    return entity;
            }
        }
        return null;
    }
    
    /** Called after replication of the object. */
    public void afterReplication(FactoryWriterXML xml, 
									TransactionXML transaction,
									List list) {
    }
    
    /** Adds prerequisites to the list. */
    public void addPrerequisites(TransactionXML transaction, List<Entity> prerequisites) {
    }

    /** Adds prerequisites to the list. */
    protected void addPrerequisites(TransactionXML transaction, 
                                    List<Entity> prerequisites, 
                                    Entity entity) {
        if(prerequisites != null && entity != null) {
            for(Entity otherEntity: prerequisites) 
                if(otherEntity == entity)
                    return;
            entity.addPrerequisites(transaction, prerequisites);
            prerequisites.add(entity);
        }
    }
}
