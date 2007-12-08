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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/core/CoreEntity.java,v $
$Revision: 1.6 $
$Date: 2007/02/27 22:12:14 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.core;

import net.sourceforge.projectfactory.server.xml.TransactionXML;


/**
 * Represents an entity managed in core server.
 * @author David Lambert
 */
public class CoreEntity extends CoreEntityBase {

    /** Generated code. */
    protected String generatedcode;

    /** Generated code. */
    protected String generatedcodeExtends;

    /** Generates java code. */
    protected void out(String text) {
        if(text != null)
            generatedcode = generatedcode + text + "\n";
    }

    /** Generates java code. */
    protected void outExtends(String text) {
        if(text != null)
            generatedcodeExtends = generatedcodeExtends + text + "\n";
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);
        draft = true;
    }
}
