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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/xml/NetworkRecipientXML.java,v $
$Revision: 1.4 $
$Date: 2007/02/22 15:36:13 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.xml;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.projectfactory.server.resources.EMail;
import net.sourceforge.projectfactory.server.resources.Resource;
import net.sourceforge.projectfactory.xml.WriterXML;


/**
 * XML Server-side parser used to manage identification of recipients.
 * @author David Lambert
 */
class NetworkRecipientXML extends ReaderServerXML {

    /** Object ID, unique identifier assigned during the creation of the object. */
    private String iid = "";

    /** Name of the entity. */
    protected String name = "";

    /** Id used in the network in order to identify the actor.
     *  Security is based on this Id, that means based on the operating system. */
    protected String networkId = "";

    /** Id used in the network in order to identify the actor.
     *  Security is based on this Id, that means based on the operating system.
     *  This is an alternative Id, a unique actor can use multiple Ids on
     *  different systems. */
    protected String alternateNetworkId = "";

    /** Email addresses. */    
    List<EMail> emails = new ArrayList(2);

    /** Constructor. */
    NetworkRecipientXML(TransactionXML transaction) {
        super(transaction);
    }

    /** Interprets a tag. */
    protected void getTag(String tag, String text) {
        if (tag.equals("iid")) {
            iid = text;
        }
        else if (tag.equals("name")) {
            name = text;
        }
        else if (tag.equals("networkid")) {
            networkId = text;
        }
        else if (tag.equals("alternatednetworkid")) {
            alternateNetworkId = text;
        }
        else if (tag.equals("address")) {
            EMail email = new EMail();
            email.address = text;
            emails.add(email);
        }
    }

    /** Ends the tag interpretation. */
    protected void end() {
        for(Resource actor: server.actors.actors) {
            if(actor.matchNetworkRecipient(transaction, 
                                            iid, 
                                            name, 
                                            networkId, 
                                            alternateNetworkId, 
                                            emails,
                                            true)) {
                xml.xmlMessage(WriterXML.TRACE, 
                                "message:actor:identified", name);
                return;
            }
        }
        xml.xmlMessage(WriterXML.WARNING, 
                        "message:actor:notidentified", name);
    }
}
