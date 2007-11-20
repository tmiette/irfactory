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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/Arrow.java,v $
$Revision: 1.2 $
$Date: 2006/03/07 20:41:12 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;


/**
 * Defines an icon with the form of a basic triangle.
 * @author David Lambert
 */
public class Arrow implements Icon {

    /** Arrow is descending / ascending. */
    private boolean descending;

    /** Icon size. */
    private int size;

    /** Constructor. */
    public Arrow(boolean descending, int size) {
        this.descending = descending;
        this.size = size;
    }

    /** Paints the icon. */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (size > 0) {
            Color color = (c == null) ? Color.GRAY : c.getBackground();
            int dx = size / 2;
            int dy = descending ? dx : (-dx);
            y = y + ((5 * size) / 6) + (descending ? (-dy) : 0);
            int shift = descending ? 1 : (-1);
            g.translate(x, y);
            g.setColor(color.darker());
            g.drawLine(dx / 2, dy, 0, 0);
            g.drawLine(dx / 2, dy + shift, 0, shift);
            g.setColor(color.brighter());
            g.drawLine(dx / 2, dy, dx, 0);
            g.drawLine(dx / 2, dy + shift, dx, shift);
            g.setColor(color.darker().darker());
            g.drawLine(dx, 0, 0, 0);
            g.setColor(color);
            g.translate(-x, -y);
        }
    }

    /** Returns the icon width. */
    public int getIconWidth() {
        return size;
    }

    /** Returns the icon height. */
    public int getIconHeight() {
        return size;
    }
}
