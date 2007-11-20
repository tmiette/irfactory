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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/FactoryActor.java,v $
$Revision: 1.29 $
$Date: 2007/02/07 18:12:50 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.actors;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.projectfactory.server.FactoryServerBase;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.SubEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Aggregates all the objects which define actors.
 * @author David Lambert
 */
public class FactoryActor extends FactoryServerBase {

    /** List of actors. */
    public List<Actor> actors = new ArrayList(30);

    /** List of locations. */
    public List<Location> locations = new ArrayList(10);

    /** List of holiday schedules. */
    public List<HolidaySchedule> holidaySchedules = new ArrayList(10);

    /** List of teams. */
    public List<Team> teams = new ArrayList(20);

    /** List of servers. */
    public List<Server> servers = new ArrayList(5);

    /** Constructor. */
    public FactoryActor(FactoryServerBase serverBase) {
        super(serverBase);
    }

    /** Clears objects. */
    public void clear() {
        actors.clear();
        teams.clear();
        locations.clear();
        holidaySchedules.clear();
        servers.clear();
    }

    /** Sorts objects. */
    public void sort() {
        Collections.sort(actors);
        Collections.sort(teams);
        Collections.sort(locations);
        Collections.sort(holidaySchedules);
        Collections.sort(servers);
    }

    /** Saves all entities. */
    public void saveAll(FactoryWriterXML xml, TransactionXML transaction, 
                        boolean demo, String iid) {
        saveEntity(xml, transaction, actors, demo, iid);
        saveEntity(xml, transaction, teams, demo, iid);
        saveEntity(xml, transaction, locations, demo, iid);
        saveEntity(xml, transaction, holidaySchedules, demo, iid);
        saveEntity(xml, transaction, servers, demo, iid);
    }

    /** Saves all objects in file(s). */
    public void saveAll(FactoryWriterXML xml, TransactionXML transaction) {
        String filename = transaction.getServer().getPath() + XMLWrapper.SLASH + "actors.xml";
        try {
            FactoryWriterXML outputXml = 
                new FactoryWriterXML(filename, "factory");
            saveEntity(outputXml, transaction, actors, false, null);
            saveEntity(outputXml, transaction, teams, false, null);
            saveEntity(outputXml, transaction, locations, false, null);
            saveEntity(outputXml, transaction, holidaySchedules, false, null);
            outputXml.end();
            traceSaving(xml, filename);
        } catch (IOException ex) {
            traceError(xml, filename);
        }

        filename = transaction.getServer().getPath() + XMLWrapper.SLASH + "servers.xml";
        try {
            FactoryWriterXML outputXml = 
                new FactoryWriterXML(filename, "factory");
            saveEntity(outputXml, transaction, servers, false, null);
            outputXml.end();
            traceSaving(xml, filename);
        } catch (IOException ex) {
            traceError(xml, filename);
        }
    }

    /** Adds files to be opened. */
    public void addFileList(List<String> files, String prefix) {
        files.add(prefix + "actors.xml");
        files.add(prefix + "servers.xml");
    }

    /** Adds classes used for replication. */
    public void addClassList(List classes) {
        classes.add("holidayschedule");
        classes.add("location");
        classes.add("actor");
        classes.add("team");
    }

    /** Sends the object names to the client based on a list. */
    public boolean isAvailable(TransactionXML transaction, String category, 
                               String filter, Entity entity) {
        if (category.equals("actors")) {
            if (filter.equals("filter:actors:active"))
                if (!entity.isActive())
                    return false;
            if (filter.equals("filter:actors:inactive"))
                if (entity.isActive())
                    return false;
            if (filter.equals("filter:actors:myteam"))
                if (!entity.isActive() || 
                    !((Actor)entity).isInSameTeam(transaction, 
                                                  transaction.getSession().getOperator()))
                    return false;
            if (filter.equals("filter:actors:administrator"))
                if (!((Actor)entity).isAdministrator())
                    return false;
        } else if (category.equals("teams")) {
            if (filter.equals("filter:teams:active"))
                if (!entity.isActive())
                    return false;
            if (filter.equals("filter:teams:inactive"))
                if (entity.isActive())
                    return false;
            if (filter.equals("filter:teams:mine"))
                if (!entity.isActive() || 
                    !((Team)entity).isMember(transaction.getSession().getOperator()))
                    return false;
        }
        if (category.equals("servers")) {
            if (entity.getName().equals("localhost"))
                return false;
        }
        return true;
    }

    /** Creates a list based on category. */
    public void listByCategory(TransactionXML transaction, List lists, 
                               String category, String filter) {
        if (category.equals("actors"))
            lists.add(actors);
        else if (category.equals("teams"))
            lists.add(teams);
        else if (category.equals("organization")) {
            if (filter.equals("filter:prefs:holidayschedules") || 
                filter.equals("filter:prefs:all"))
                lists.add(holidaySchedules);
            if (filter.equals("filter:prefs:locations") || 
                filter.equals("filter:prefs:all"))
                lists.add(locations);
        } else if (category.equals("servers") && 
                   !transaction.getSession().isRemote()) {
            lists.add(servers);
        }
    }

    /** Creates the list based on class. */
    public void listByClass(TransactionXML transaction, List lists, 
                            String classname) {
        if (classname.equals("actor"))
            lists.add(actors);
        else if (classname.equals("team"))
            lists.add(teams);
        else if (classname.equals("holidayschedule"))
            lists.add(holidaySchedules);
        else if (classname.equals("server") && 
                 !transaction.getSession().isRemote())
            lists.add(servers);
        else if (classname.equals("location"))
            lists.add(locations);
    }

    /** Returns the appropriate parser in order to read an element. */
    public SubEntityServerXML getParser(TransactionXML transaction, String tag) {
        if (tag.equals("actor")) 
            return new SubEntityServerXML(this, transaction, new Actor(), actors);
        else if (tag.equals("team")) 
            return new SubEntityServerXML(this, transaction, new Team(), teams);
        else if (tag.equals("location")) 
            return new SubEntityServerXML(this, transaction, new Location(), locations);
        else if (tag.equals("holidayschedule")) 
            return new SubEntityServerXML(this, transaction, new HolidaySchedule(), holidaySchedules);
        else if (tag.equals("server")) 
            return new SubEntityServerXML(this, transaction, new Server(), servers);
        else if (tag.equals("localhost")) 
            return new SubEntityServerXML(this, transaction, new Server(), servers);
        return null;
    }

    /** Returns actor designed by a network Id. */
    public Actor getActor(String networkId) {
        for (Actor actor: actors) {
            if (actor.getNetworkId().equals(networkId) || 
                actor.getAltNetworkId().equals(networkId))
                return actor;
        }
        return null;
    }

    /** Creates an actor using network Id. */
    public Actor createActor(TransactionXML transaction, String networkId) {
        Actor actor = new Actor();
        actor.createDefaultOperator(networkId);
        actors.add(actor);
        actor.create(transaction);
        return actor;
    }

    /** Returns server designed by a name. */
    public Server getServer(String name) {
        for (Server server: servers) {
            if (server.getName().equals(name))
                return server;
        }
        return null;
    }

    /** Creates a server localhost. */
    public Server createServer(TransactionXML transaction) {
        Server nserver = new Server();
        nserver.createDefaultServer(transaction);
        servers.add(nserver);
        nserver.create(transaction);
        return nserver;
    }
}
