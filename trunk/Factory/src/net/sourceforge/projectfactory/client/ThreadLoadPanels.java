/*

Copyright (c) 2006 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/ThreadLoadPanels.java,v $
$Revision: 1.9 $
$Date: 2007/02/08 17:04:47 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client;

/**
 * Thread used in order to construct data panels
 * in the background during application startup.
 * @author David Lambert
 */
public class ThreadLoadPanels implements Runnable {
	
	/** Indicates the thread is terminated. */
	protected volatile boolean terminated;
	
	/** Main frame. */
	protected MainFrame frame;
	
	/** Constructor. Initializes the thread. */
	public ThreadLoadPanels(MainFrame frame) {
		this.frame = frame;
		Thread runner = new Thread(this);
		runner.start();
	}
	
	/** Indicates the thread is terminated. */
	public boolean isTerminated() {
		return terminated;
	}
	
	/** Construct data panels and attach them to the main frame. */
	public void run() {
		try {
            (new net.sourceforge.projectfactory.client.panels.PanelLoader()).load(frame);
		} catch (Exception e) {
			frame.addMessage(e);
		} finally {
			terminated = true;
		}
	}
}

