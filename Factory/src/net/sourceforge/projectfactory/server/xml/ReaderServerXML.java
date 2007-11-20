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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/xml/ReaderServerXML.java,v $
$Revision: 1.2 $
$Date: 2007/01/17 22:11:42 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.server.xml;

import net.sourceforge.projectfactory.server.FactoryServer;
import net.sourceforge.projectfactory.xml.FactoryReaderXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;


/**
 * XML Parser used to be run on server side.
 * @author David Lambert
 */
public class ReaderServerXML extends FactoryReaderXML {
	
    /** Transaction. */
    protected TransactionXML transaction;

    /** XML writer attached to the transaction. */
    protected FactoryWriterXML xml;

    /** Server attached to the transaction. */
    protected FactoryServer server;
    
    /** Constructor. */
    protected ReaderServerXML(TransactionXML transaction) {
        this.transaction = transaction;
        this.xml = transaction.getXml();
        this.server = transaction.getServer();
    }
}
