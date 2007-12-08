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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/data/ActionBar.java,v $
$Revision: 1.1 $
$Date: 2007/02/27 22:12:41 $
$Author: ddlamb_2000 $

*/

package net.sourceforge.projectfactory.server.data;


/**
  * Defines an action available in the action bar.
  * @author David Lambert
  */
public class ActionBar extends ActionBarBase implements Comparable {

    /** Compares this object with the specified object for order. */
    public int compareTo(Object object) {
        if (this.orderNumber > ((ActionBar)object).orderNumber) 
            return 1;

        if (this.orderNumber < ((ActionBar)object).orderNumber) 
            return -1;

        return 0;
    }
}
