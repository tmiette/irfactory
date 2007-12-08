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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/actors/ServerBase.java,v $
$Revision: 1.1 $
$Date: 2007/02/12 11:17:51 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.resources;

import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.server.TransactionXML;

/** 
  * Defines a remote server.
  * @author David Lambert
  */
public class ServerBase extends Entity {
    public String address;
    public int port;
    public String encryptKey;
    public boolean replication;
    public boolean allowReplication;
    public boolean activeInMail;
    public String inMailType;
    public String inMailServer;
    public int inMailPort;
    public String inMailUserName;
    public String inMailPassword;
    public boolean activeOutMail;
    public String outMailType;
    public String outMailServer;
    public int outMailPort;
    public String outMailAuth;
    public String outMailUserName;
    public String outMailPassword;
    public String path;

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, boolean tags) {
        if (tags) xmlStart(xml, "server");
        super.xmlOut(xml, transaction, false);
        if (transaction.isDetail() || transaction.isSave()) {
            xmlOut(xml, "address", address);
            xmlOut(xml, "port", port);
            xmlOut(xml, "encryptkey", encryptKey);
            xmlOut(xml, "replication", replication);
            xmlOut(xml, "allowreplication", allowReplication);
            xmlOut(xml, "activeinmail", activeInMail);
            xmlOut(xml, "inmailtype", inMailType);
            xmlOut(xml, "inmailserver", inMailServer);
            xmlOut(xml, "inmailport", inMailPort);
            xmlOut(xml, "inmailusername", inMailUserName);
            xmlOut(xml, "inmailpassword", inMailPassword);
            xmlOut(xml, "activeoutmail", activeOutMail);
            xmlOut(xml, "outmailtype", outMailType);
            xmlOut(xml, "outmailserver", outMailServer);
            xmlOut(xml, "outmailport", outMailPort);
            xmlOut(xml, "outmailauth", outMailAuth);
            xmlOut(xml, "outmailusername", outMailUserName);
            xmlOut(xml, "outmailpassword", outMailPassword);
            xmlOut(xml, "path", path);
        }
        if (tags) xmlEnd(xml);
    }

    /** Reads the object from an XML input. */
    public boolean xmlIn(WriterXML xml, TransactionXML transaction, String tag, String value) {
        if (super.xmlIn(xml, transaction, tag, value)) return true;
        if (tag.equals("address")) {
            address = value;
            return true;
        }
        if (tag.equals("port")) {
            port = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("encryptkey")) {
            encryptKey = value;
            return true;
        }
        if (tag.equals("replication")) {
            replication = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("allowreplication")) {
            allowReplication = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("activeinmail")) {
            activeInMail = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("inmailtype")) {
            inMailType = value;
            return true;
        }
        if (tag.equals("inmailserver")) {
            inMailServer = value;
            return true;
        }
        if (tag.equals("inmailport")) {
            inMailPort = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("inmailusername")) {
            inMailUserName = value;
            return true;
        }
        if (tag.equals("inmailpassword")) {
            inMailPassword = value;
            return true;
        }
        if (tag.equals("activeoutmail")) {
            activeOutMail = xmlInBoolean(value);
            return true;
        }
        if (tag.equals("outmailtype")) {
            outMailType = value;
            return true;
        }
        if (tag.equals("outmailserver")) {
            outMailServer = value;
            return true;
        }
        if (tag.equals("outmailport")) {
            outMailPort = xmlInInt(xml, value);
            return true;
        }
        if (tag.equals("outmailauth")) {
            outMailAuth = value;
            return true;
        }
        if (tag.equals("outmailusername")) {
            outMailUserName = value;
            return true;
        }
        if (tag.equals("outmailpassword")) {
            outMailPassword = value;
            return true;
        }
        if (tag.equals("path")) {
            path = value;
            return true;
        }
        return false;
    }

    /** Starts a tag. */
    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {
        return null;
    }

    /** Update method : updates the object from another entity. */
    public void update(TransactionXML transaction, Entity other) {
        if (this.getClass() != other.getClass()) return;
        Server otherEntity = (Server) other;
        super.update(transaction, other);
        this.address = otherEntity.address;
        this.port = otherEntity.port;
        this.encryptKey = otherEntity.encryptKey;
        this.replication = otherEntity.replication;
        this.allowReplication = otherEntity.allowReplication;
        this.activeInMail = otherEntity.activeInMail;
        this.inMailType = otherEntity.inMailType;
        this.inMailServer = otherEntity.inMailServer;
        this.inMailPort = otherEntity.inMailPort;
        this.inMailUserName = otherEntity.inMailUserName;
        this.inMailPassword = otherEntity.inMailPassword;
        this.activeOutMail = otherEntity.activeOutMail;
        this.outMailType = otherEntity.outMailType;
        this.outMailServer = otherEntity.outMailServer;
        this.outMailPort = otherEntity.outMailPort;
        this.outMailAuth = otherEntity.outMailAuth;
        this.outMailUserName = otherEntity.outMailUserName;
        this.outMailPassword = otherEntity.outMailPassword;
        this.path = otherEntity.path;
    }
}
