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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/xml/PreferenceXML.java,v $
$Revision: 1.14 $
$Date: 2007/02/27 22:12:04 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.xml;

import net.sourceforge.projectfactory.server.data.Preference;
import net.sourceforge.projectfactory.server.entities.xml.EntityServerXML;


/**
 * XML Server-side parser used to manage preferences on the server.
 * @author David Lambert
 */
class PreferenceXML extends EntityServerXML {

    /** Constructor. */
    PreferenceXML(TransactionXML transaction) {
        super(transaction, new Preference(), null);
    }

    /** Interprets a tag. */
    protected void getTag(String tag, String text) {
        entity.xmlIn(xml, transaction, tag, text);
    }

    /** Ends the tag interpretation. */
    protected void end() {
        switch (transaction.getCode()) {
        case TransactionXML.UPDATE:
        case TransactionXML.LOAD:
            server.data.preference.update(transaction, entity);
            break;

        case TransactionXML.GET:
            transaction.setCode(TransactionXML.DETAIL);
            server.data.preference.xmlOut(xml, transaction, true);
            break;
        }
        super.end();
        entity = null;
    }
}
