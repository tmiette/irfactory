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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/entities/xml/SubEntityServerXML.java,v $
$Revision: 1.1 $
$Date: 2007/01/15 13:19:05 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.entities.xml;

import java.util.List;

import net.sourceforge.projectfactory.server.ApplicationServerBase;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.xml.TransactionXML;

/**
 * XML Server-side parser used to manage objects attached to another element.
 * @author David Lambert
 */
public class SubEntityServerXML extends EntityServerXML {

    ApplicationServerBase baseServer;

    /** Constructor. */
    public SubEntityServerXML(ApplicationServerBase baseServer, 
                            TransactionXML transaction, 
                            Entity entity, 
                            List list) {
        super(transaction, entity, list);
        this.baseServer = baseServer;
    }

    /** Set the server to be dirty after the end of parsing. */
    protected void afterEnd() {
        if (isDirty())
            baseServer.setDirty();
    }
}
