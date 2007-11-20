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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/FrameSplash.java,v $
$Revision: 1.3 $
$Date: 2007/02/11 20:55:24 $
$Author: ddlamb_2000 $
 
*/
package net.sourceforge.projectfactory.client;

import net.sourceforge.projectfactory.client.components.LocalIcon;
import net.sourceforge.projectfactory.client.components.LocalMessage;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;


/**
 * Frame displaying a splash screen for the application.
 * @author David Lambert
 */
public class FrameSplash extends JFrame {
	
	/** Label displaying the welcome message. */
    private JLabel label = new JLabel();
	
	/** Progress bar. */
    private JProgressBar progress = new JProgressBar();

    /** Constructor. */
    public FrameSplash(String title,
						String copyright,
						String licence,
						String shortTitle) {
		String mainTitle = "<html><b><font size=+1>" + title + "</font></b>" +
		"<br>" + copyright + 
		"<br>" + licence + "</font></html>";

        enableEvents(AWTEvent.PAINT_EVENT_MASK);

        ImageIcon icon = LocalIcon.get("factory_icon.jpg");
        ImageIcon picture = LocalIcon.get("factory_main.jpg");
        setSize(new Dimension(480, 
                              (picture != null) ? 
							  (picture.getIconHeight() + 30) : 
                              180));

		setTitle(shortTitle);

        if (icon != null)
            setIconImage(icon.getImage());

        setUndecorated(true);
		JPanel panelContent = (JPanel)this.getContentPane();
        panelContent.setLayout(new BorderLayout());
        panelContent.setBorder(new TitledBorder(""));

        if (picture != null) 
            label.setIcon(picture);

		label.setText(mainTitle);
        panelContent.add(label, BorderLayout.CENTER);
        panelContent.add(progress, BorderLayout.SOUTH);
        progress.setIndeterminate(true);
    }

	/** Resets the progress bar to its initial state. */
    public void resetProgress() {
        progress.setIndeterminate(true);
        progress.setValue(0);
    }

	/** Increases value of progress bar and displays a message. */
    public void addProgressValue(String text) {
        progress.setIndeterminate(false);
        progress.setMaximum(FrameMain.PROGRESS_MAX + 1);
        progress.setValue(progress.getValue() + 1);
        progress.setStringPainted(true);
        progress.setString(LocalMessage.get("message:" + text));
    }
}
