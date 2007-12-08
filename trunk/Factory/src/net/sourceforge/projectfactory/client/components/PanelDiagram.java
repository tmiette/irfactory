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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/client/components/PanelDiagram.java,v $
$Revision: 1.9 $
$Date: 2007/02/07 18:12:10 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.client.components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Panel dedicated for the display of a Gantt-like diagram.
 * @author David Lambert
 */
public class PanelDiagram extends PanelLookup implements ActionListener {

    /** List of messages, events, dates to be displayed. */
    private List<MessageDayTreeNode> messages = new ArrayList(100);

    /** List of messages, events, dates to be displayed in a draft mode. */
    private List<MessageDayTreeNode> draftMessages = new ArrayList(100);

    /** Diagram. */
    private Diagram diagram = new Diagram();

    /** Button for next period display. */    
    private ButtonDown buttonNextPeriod = new ButtonDown();

    /** Button for previous period display. */    
    private ButtonUp buttonPreviousPeriod = new ButtonUp();
    
    /** Label used to display the current period. */
    private JLabel label = new JLabel();
    
    /** Number of periods that can be displayed. */
    private int maxPeriod = 1;
    
    /** Number of displayed months. */
    private int monthsDisplayed = 1;
    
    /** Diagram options. */
    private ComboBoxCode comboDisplayOptions;
    
    /** Diagram periods. */
    private ComboBoxCode comboDisplayPeriod;
    
    /** Diagram actors. */
    private ComboBoxCode comboDisplayActors;
    
    /** Initialization (UI). */
    public void init(ComboBoxCode comboDisplayOptions,
                        ComboBoxCode comboDisplayPeriod,
                        ComboBoxCode comboDisplayActors) throws Exception {
        this.comboDisplayOptions = comboDisplayOptions;
        this.comboDisplayPeriod = comboDisplayPeriod;
        this.comboDisplayActors = comboDisplayActors;
        setLayout(new BorderLayout());
        setSize(new Dimension(50, 70));
        setMinimumSize(new Dimension(150, 100));
        JPanel panelSelection = new JPanel();
        panelSelection.setLayout(new GridBagLayout());

        buttonPreviousPeriod.addActionListener(this);
        buttonNextPeriod.addActionListener(this);
        comboDisplayOptions.addActionListener(this);
        comboDisplayPeriod.addActionListener(this);
        comboDisplayActors.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(buttonPreviousPeriod, BorderLayout.WEST);
        buttonPanel.add(label, BorderLayout.CENTER);
        buttonPanel.add(buttonNextPeriod, BorderLayout.EAST);
        panelSelection.add(buttonPanel, 
                           new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, 
                                                  GridBagConstraints.WEST, 
                                                  GridBagConstraints.NONE, 
                                                  new Insets(0, 0, 0, 0), 0, 
                                                  0));

        panelSelection.add(comboDisplayOptions, 
                           new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, 
                                                  GridBagConstraints.CENTER, 
                                                  GridBagConstraints.HORIZONTAL, 
                                                  new Insets(0, 0, 0, 0), 0, 
                                                  0));

        panelSelection.add(comboDisplayPeriod, 
                           new GridBagConstraints(3, 1, 1, 1, 1.0, 1.0, 
                                                  GridBagConstraints.CENTER, 
                                                  GridBagConstraints.HORIZONTAL, 
                                                  new Insets(0, 0, 0, 0), 0, 
                                                  0));

        panelSelection.add(comboDisplayActors, 
                           new GridBagConstraints(4, 1, 1, 1, 1.0, 1.0, 
                                                  GridBagConstraints.CENTER, 
                                                  GridBagConstraints.HORIZONTAL, 
                                                  new Insets(0, 0, 0, 0), 0, 
                                                  0));

        JScrollPane scroll = new JScrollPane(diagram);
        scroll.setOpaque(true);
        add(panelSelection, BorderLayout.SOUTH);
        add(scroll, BorderLayout.CENTER);
    }

    /** Displays or redisplays the diagram based on loaded messages. */
    public void reloadDays() {
        if(comboDisplayPeriod.getSelectedCode().equals("1"))
            monthsDisplayed = 2;
        else if(comboDisplayPeriod.getSelectedCode().equals("2"))
            monthsDisplayed = 3;
        else if(comboDisplayPeriod.getSelectedCode().equals("3"))
            monthsDisplayed = 6;
        else 
            monthsDisplayed = 12;

        synchronized(messages) {
            messages.clear();
            for (MessageDayTreeNode draftMessage: draftMessages) {
                if(draftMessage.who.length()>0)
                    comboDisplayActors.addItemNoDup(
                                            draftMessage.who, 
                                            draftMessage.who);
                
                if (((!comboDisplayOptions.getSelectedCode().equals("3") && 
                     (comboDisplayOptions.getSelectedCode().equals("1") || 
                        draftMessage.type == 1 || 
                        draftMessage.type == 2 || 
                        draftMessage.type == 3)) || 
                        draftMessage.type == 10) 
                    && 
                    (comboDisplayActors.getSelectedCode().equals("1") ||
                        comboDisplayActors.getSelectedCode().equals(
                                                    draftMessage.who))){
                    
                    MessageDayTreeNode newMessage = 
                        new MessageDayTreeNode(draftMessage.date, 
                                               draftMessage.label, 
                                               draftMessage.who, 
                                               draftMessage.day, 
                                               draftMessage.displayDate, 
                                               draftMessage.type, 
                                               draftMessage.duration, 
                                               draftMessage.durationType, 
                                               draftMessage.complete);

                    boolean found = false;
                    for (MessageDayTreeNode message: messages) {
                        if (message.label.equals(draftMessage.label) && 
                            (!comboDisplayOptions.getSelectedCode().equals("1") || 
                             message.who.equals(draftMessage.who)) && 
                             message.type == draftMessage.type) {
                            if (!comboDisplayOptions.getSelectedCode().equals("1")) {
                                message.duration = 1;
                                message.durationType = 1;
                                newMessage.duration = 1;
                                newMessage.durationType = 1;
    
                                if (message.who.indexOf(draftMessage.who) < 0) {
                                    message.who += 
                                            (((message.who.length() == 0) ? "" : 
                                              ",") + draftMessage.who);
                                }
                            }
                            MessageDayTreeNode last = message.getLast();
                            last.next = newMessage;
                            found = true;
                            break;
                        }
                    }
    
                    if (!found) 
                        messages.add(newMessage);
                }
            }
    
            Collections.sort(messages);
    
            long min = 0;
            long max = 0;
            int minMonth = 0;
            int minYear = 0;
            int maxMonth = 0;
            int maxYear = 0;
    
            for (MessageDayTreeNode message: messages) {
                if (message.day != 0) {
                    if (min == 0 || message.day < min) {
                        min = message.day;
                        minMonth = message.date.get(Calendar.MONTH);
                        minYear = message.date.get(Calendar.YEAR);
                    }
                    if (max == 0 || message.day > max) {
                        max = message.day;
                        maxMonth = message.date.get(Calendar.MONTH);
                        maxYear = message.date.get(Calendar.YEAR);
                    }
                }
    
                while (message.next != null) {
                    message = message.next;
                    if (message.day != 0) {
                        if ((min == 0) || (message.day < min)) {
                            min = message.day;
                            minMonth = message.date.get(Calendar.MONTH);
                            minYear = message.date.get(Calendar.YEAR);
                        }
                        if ((max == 0) || (message.day > max)) {
                            max = message.day;
                            maxMonth = message.date.get(Calendar.MONTH);
                            maxYear = message.date.get(Calendar.YEAR);
                        }
                    }
                }
            }
    
            int period = 1;
            int month = minMonth;
            int year = minYear;
            int count = 1;
    
            for (; ; ) {
                maxPeriod = period;
                for (MessageDayTreeNode message: messages) {
                    if (message.date.get(Calendar.MONTH) == month && 
                        message.date.get(Calendar.YEAR) == year) {
                        message.period = period;
                    }
                    while (message.next != null) {
                        message = message.next;
                        if (message.date.get(Calendar.MONTH) == month && 
                            message.date.get(Calendar.YEAR) == year) {
                            message.period = period;
                        }
                    }
                }
    
                if (++month > 12) {
                    month = 0;
                    ++year;
                }
    
                if ((year > maxYear) || 
                    ((year == maxYear) && (month > maxMonth))) {
                    break;
                }
                if (++count > monthsDisplayed) {
                    count = 1;
                    ++period;
                }
            }
        }

        diagram.displayPeriod = 1;
        refreshButtons();
        diagram.calculateSize();
        diagram.createImage();
        diagram.revalidate();
        diagram.repaint(0, 0, diagram.getWidth(), diagram.getHeight());
    }

    /** Triggers a thread for redisplay of the diagram. */
    public void runLookup() {
        synchronized (diagram) {
            new ThreadLoadCalendar();
        }
    }

    /** Stores a message defined for the diagram. */
    public void addCalendarMessage(Date dateMessage, String label, String who, 
                                   int type, int duration, int durationType, 
                                   int complete) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dateMessage);

        MessageDayTreeNode newMessage = 
            new MessageDayTreeNode(rightNow, label, who, 
                                   dateMessage.getTime() / 1000 / 3600 / 24, 
                                   XMLWrapper.dsLocal.format(dateMessage), 
                                   type, duration, 
                                   durationType, complete);
        synchronized(messages) {
            draftMessages.add(newMessage);
        }
    }

    /** Removes all messages used for display of diagram. */
    public void clearCalendarMessages() {
        synchronized(messages) {
            draftMessages.clear();
            messages.clear();
        }
        comboDisplayActors.removeLastItems(1);
    }

    /** Returns the diagram image for the referenced period. */
    public BufferedImage getImage(int period) {
        int lastPeriod = diagram.displayPeriod;
        diagram.displayPeriod = period;
        diagram.calculateSize();
        diagram.createImage();

        BufferedImage bi = diagram.bi;
        diagram.displayPeriod = lastPeriod;
        diagram.calculateSize();
        diagram.createImage();

        return bi;
    }

    /** Returns the maximum number of displayed periods. */
    public int getPeriods() {
        return maxPeriod;
    }

    /** Activates or deactivates buttons based on current display. */
    private void refreshButtons() {
        buttonNextPeriod.setVisible(maxPeriod > 1 && 
                                    diagram.displayPeriod < maxPeriod);
        buttonPreviousPeriod.setVisible(maxPeriod > 1 && 
                                        diagram.displayPeriod > 1);
        label.setVisible(maxPeriod > 1);
        label.setText(" " + diagram.displayPeriod + "/" + maxPeriod + " ");
    }

    /** Manages clicks on buttons. */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == buttonNextPeriod && 
            diagram.displayPeriod < maxPeriod) 
            ++diagram.displayPeriod;

        if (source == buttonPreviousPeriod && 
            diagram.displayPeriod > 1) 
            --diagram.displayPeriod;

        diagram.calculateSize();
        diagram.createImage();
        diagram.revalidate();
        diagram.repaint(0, 0, diagram.getWidth(), diagram.getHeight());
        
        if (source != buttonNextPeriod && source != buttonPreviousPeriod)
            new ThreadLoadCalendar();
    }

    /**
     * Represents a thread used in order to populate the diagram.
     */
    private class ThreadLoadCalendar implements Runnable {
    
        /** Constructor. */  
        private ThreadLoadCalendar() {
            Thread runner = new Thread(this);
            runner.start();
        }

        /** Executes the thread. */
        public void run() {
            reloadDays();
        }
    }

    /**
     * Represents messages attached to the diagram.
     */
    private class MessageDayTreeNode implements Comparable {

        /** Label of the message. */
        String label;
        
        /** Person whose is assigned to the message. */
        String who;

        /** Date of the message. */
        Calendar date;
        
        /** Numeric representation of the date. */
        long day;
        
        /** String representation of the date. */
        String displayDate;
        
        /** Duration. */
        int duration;
        
        /** Duration type. */
        int durationType;
        
        /** Type of message. */
        int type;
        
        /** % complete. */
        int complete;
        
        /** Period the message is attached to. */
        int period;
        
        /** Difference between the date and the first message date. */
        int offset;
        
        /** Next message in chained list of messages. */
        MessageDayTreeNode next;

        /** Constructor. */
        MessageDayTreeNode(Calendar date, String label, String who, long day, 
                           String displayDate, int type, int duration, 
                           int durationType, int complete) {
            this.date = date;
            this.label = label;
            this.who = who;
            this.day = day;
            this.displayDate = displayDate;
            this.type = type;
            this.duration = duration;
            this.durationType = durationType;
            this.complete = complete;
        }

        /** Compares this entity to the specified object. */
        public boolean equals(Object object) {
            if (object == this) 
                return true;

            if (this.date == null) 
                return false;

            if (((MessageDayTreeNode)object).date == null) 
                return false;

            return this.date.equals(((MessageDayTreeNode)object).date);
        }

        /** Compares this object with the specified object for order. */
        public int compareTo(Object object) {
            if (this.date == null) 
                return -1;

            if (((MessageDayTreeNode)object).date == null) 
                return 1;

            int compareDate = 
                this.date.getTime().compareTo(((MessageDayTreeNode)object).date.getTime());

            if (compareDate == 0) {
                if (this.type == ((MessageDayTreeNode)object).type) 
                    return this.label.compareToIgnoreCase(((MessageDayTreeNode)object).label);

                if (this.type > ((MessageDayTreeNode)object).type) 
                    return -1;

                if (this.type < ((MessageDayTreeNode)object).type) 
                    return 1;
            }

            return compareDate;
        }

        /** Returns the last message attached in the chained list. */
        private MessageDayTreeNode getLast() {
            return next == null ? this : next.getLast();
        }

        public String toString() {
            String text = label;

            if ((who != null) && !who.equals("")) 
                text += (" [" + who + "]");

            if ((displayDate != null) && !displayDate.equals("")) 
                text += (": " + displayDate);

            MessageDayTreeNode last = getLast();

            if ((last != null) && (last != this)) 
                text += (" - " + last.displayDate);

            return text;
        }
    }

    /**
     * Represents a component designed in order to display a
     * Gantt-like diagram based on calendar messages.
     */
    private class Diagram extends JComponent {
    
        /** Width of grid used for display. */
        private static final int GRIDWIDTH = 10;

        /** Height of grid used for display. */
        private static final int GRIDHEIGHT = 30;
        
        /** Margin displayed around boxes. */
        private static final int margin = 12;

        /** Total width of the diagram. */
        private int totalWidth = 5;

        /** Total height of the diagram. */
        private int totalHeight = 5;
        
        /** Image created to display the diagram. */
        private BufferedImage bi;
        
        /** Period currently displayed. */
        private int displayPeriod = 1;

        /** Initialization (UI). */
        public void init() {
            setBackground(Color.white);
            setForeground(Color.black);
            setOpaque(true);
        }

        /** Selects a font based on diagram size. */
        void pickFont(Graphics2D g2, String longString, int xSpace) {
            int maxCharHeight = 15;
            int minFontSize = 9;
            Font font = g2.getFont();
            FontMetrics fontMetrics = g2.getFontMetrics();
            int size = font.getSize();
            String name = font.getName();
            int style = font.getStyle();
            boolean fontFits = false;

            while (!fontFits) {
                if ((fontMetrics.getHeight() <= maxCharHeight) && 
                    (fontMetrics.stringWidth(longString) <= xSpace)) {
                    fontFits = true;
                } else {
                    if (size <= minFontSize) {
                        fontFits = true;
                    } else {
                        g2.setFont(font = new Font(name, style, --size));
                        fontMetrics = g2.getFontMetrics();
                    }
                }
            }
        }

        /** Creates a buffered image of the current period. */
        private void createImage() {
            if (bi != null) 
                bi.flush();

            bi = (BufferedImage)createImage(totalWidth, totalHeight);

            synchronized(messages) {
                if (messages.size() == 0) 
                    return;

                float[] dash1 = { 2.0f };
                float[] dash2 = { 1.0f };
                BasicStroke dashed = new BasicStroke(1.0f, 
                                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
                                    10.0f, dash1, 0.0f);
                BasicStroke dashed2 = new BasicStroke(1.0f, 
                                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
                                    10.0f, dash2, 0.0f);
    
                Graphics2D big = bi.createGraphics();
                big.setBackground(Color.white);
                big.setPaint(Color.white);
                big.fill(new Rectangle.Double(0, 0, totalWidth, totalHeight));
                pickFont(big, "Filled and Stroked GeneralPath", GRIDWIDTH);
    
                int rectHeight = margin;
                Calendar firstDate = null;
                Calendar lastDate = null;
    
                for (MessageDayTreeNode message: messages) {
                    if (message.period == displayPeriod) {
                        if (firstDate == null) 
                            firstDate = message.date;
                        else if (message.date.before(firstDate)) 
                            firstDate = message.date;
    
                        if (lastDate == null) 
                            lastDate = message.date;
                        else if (message.date.after(lastDate)) 
                            lastDate = message.date;
                    }
    
                    message = message.next;
    
                    while (message != null) {
                        if (message.period == displayPeriod) {
                            if (firstDate == null) 
                                firstDate = message.date;
                            else if (message.date.before(firstDate)) 
                                firstDate = message.date;
    
                            if (lastDate == null) 
                                lastDate = message.date;
                            else if (message.date.after(lastDate)) 
                                lastDate = message.date;
                        }
    
                        message = message.next;
                    }
                }
    
                int x = 5;
                int y = 15 + margin;
                int lastX = x;
    
                if (firstDate != null) {
                    Calendar date = (Calendar)firstDate.clone();
    
                    while (!date.after(lastDate)) {
                        boolean display = false;
    
                        if (date.get(Calendar.DAY_OF_MONTH) == 1) {
                            display = true;
                        }
    
                        if (date.equals(firstDate) && 
                            (date.get(Calendar.DAY_OF_MONTH) < 20)) {
                            display = true;
                        }
    
                        if (display) {
                            big.setPaint(new GradientPaint(lastX, margin, 
                                                           Color.lightGray, x, 
                                                           margin, Color.white));
                            big.fill(new Rectangle2D.Double(lastX, 2 * margin, 
                                                            x - lastX, 
                                                            totalHeight - 
                                                            (2 * margin)));
    
                            int month = 1 + date.get(Calendar.MONTH);
                            int year = date.get(Calendar.YEAR);
                            big.setPaint(Color.black);
                            big.drawString(LocalMessage.get("label:month:" + 
                                                            ((month <= 9) ? "0" : 
                                                             "") + month) + " " + 
                                           year, 
                                            x + 1, 
                                            10);
                            lastX = x;
                        }
    
                        x += GRIDWIDTH;
                        date.add(Calendar.DATE, 1);
                    }
    
                    big.setPaint(new GradientPaint(lastX, margin, Color.lightGray, 
                                                   x, margin, Color.white));
                    big.fill(new Rectangle2D.Double(lastX, 2 * margin, x - lastX, 
                                                    totalHeight - (2 * margin)));
    
                    x = 5;
                    date = (Calendar)firstDate.clone();
    
                    while (!date.after(lastDate)) {
                        if (date.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                            big.setPaint(Color.gray);
                            big.setStroke(dashed2);
                            big.draw(new Line2D.Double(x, margin, x, totalHeight));
                            big.drawString("" + date.get(Calendar.DAY_OF_MONTH) + 
                                           " (" + 
                                           LocalMessage.get("label:week:small") + 
                                           " " + date.get(Calendar.WEEK_OF_YEAR) + 
                                           ")", 
                                            x + 2, 
                                            10 + margin);
                        }
    
                        x += GRIDWIDTH;
                        date.add(Calendar.DATE, 1);
                    }
                }
    
                int width;
                int height;
                int offsetHeight;
    
                for (MessageDayTreeNode message: messages) {
                    offsetHeight = 0;
    
                    boolean title = false;
    
                    if (message.period == displayPeriod) {
                        x = 5 + (GRIDWIDTH * message.offset);
                        width = GRIDWIDTH;
                        height = rectHeight + 4;
                        offsetHeight = GRIDHEIGHT;
    
                        if (message.duration == 0) 
                            width = 1;
                        else if (message.durationType != 1) 
                            width /= 2;
    
                        if (message.type == 3) {
                            height /= 2;
                            offsetHeight -= 10;
                            big.setPaint(new GradientPaint(x, y, Color.black, 
                                                           x + width, y, 
                                                           Color.white));
                        } else if (message.type == 10) {
                            height /= 2;
                            offsetHeight -= 10;
                            big.setPaint(new GradientPaint(x, y, Color.black, 
                                                           x + width, y, 
                                                           Color.gray));
                        } else if ((message.type != 1) && (message.type != 2)) {
                            height /= 2;
                            offsetHeight -= 10;
                            big.setPaint(new GradientPaint(x, y, Color.blue, 
                                                           x + width, y, 
                                                           Color.yellow));
                        } else {
                            big.setPaint(new GradientPaint(x, y, Color.blue, 
                                                           x + width, y, 
                                                           (message.complete >= 
                                                            80) ? Color.red : 
                                                           Color.green));
                        }
    
                        big.fill(new Rectangle2D.Double(x, y, width, height));
    
                        if (message.type == 3) 
                            big.setPaint(Color.red);
                        else if (message.type == 10) 
                            big.setPaint(Color.blue);
                        else if ((message.type != 1) && (message.type != 2)) 
                            big.setPaint(Color.darkGray);
                        else 
                            big.setPaint(Color.black);
    
                        big.drawString(message.toString(), 
                                        x + 2, 
                                        y + height + 9);
                        title = true;
                        lastX = x + width + 1;
                    }
    
                    message = message.next;
                    while (message != null) {
                        if (message.period == displayPeriod) {
                            x = 5 + (GRIDWIDTH * message.offset);
                            width = GRIDWIDTH;
                            height = rectHeight + 4;
                            offsetHeight = GRIDHEIGHT;
    
                            if ((x - lastX) > GRIDWIDTH) {
                                big.setPaint(Color.gray);
                                big.setStroke(dashed);
                                big.draw(new Line2D.Double(lastX, y + (height / 2), 
                                                           x, y + (height / 2)));
                            }
    
                            if (message.duration == 0) {
                                width = 1;
                            } else if (message.durationType != 1) {
                                width /= 2;
                            }
    
                            if (message.type == 3) {
                                height /= 2;
                                offsetHeight -= 10;
                                big.setPaint(new GradientPaint(x, y, Color.black, 
                                                               x + width, y, 
                                                               Color.white));
                            } else if (message.type == 10) {
                                height /= 2;
                                offsetHeight -= 10;
                                big.setPaint(new GradientPaint(x, y, Color.black, 
                                                               x + width, y, 
                                                               Color.gray));
                            } else if ((message.type != 1) && 
                                       (message.type != 2)) {
                                height /= 2;
                                offsetHeight -= 10;
                                big.setPaint(new GradientPaint(x, y, Color.blue, 
                                                               x + width, y, 
                                                               Color.yellow));
                            } else {
                                big.setPaint(new GradientPaint(x, y, Color.blue, 
                                                               x + width, y, 
                                                               (message.complete >= 
                                                                80) ? Color.red : 
                                                               Color.green));
                            }
    
                            big.fill(new Rectangle2D.Double(x, y, width, height));
    
                            if (!title) {
                                if (message.type == 3) {
                                    big.setPaint(Color.red);
                                } else if (message.type == 10) {
                                    big.setPaint(Color.blue);
                                } else if ((message.type != 1) && 
                                           (message.type != 2)) {
                                    big.setPaint(Color.darkGray);
                                } else {
                                    big.setPaint(Color.black);
                                }
    
                                big.drawString(message.toString(), 
                                                x + 2, 
                                                y + height + 9);
                                title = true;
                            }
    
                            lastX = x + width + 1;
                        }
    
                        message = message.next;
                    }
    
                    y += offsetHeight;
                }
            }

            refreshButtons();
        }

        /** Paints diagram image. */
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;

            if (bi != null) {
                g2.drawImage(bi, 0, 0, this);
            }
        }

        /** Calculates height and width of diagram image. */
        private void calculateSize() {
            long min = 0;

            synchronized(messages) {
                for (MessageDayTreeNode message: messages) {
                    if (message.period == displayPeriod && message.day != 0) {
                        if ((min == 0) || (message.day < min)) 
                            min = message.day;
                    }
    
                    while (message.next != null) {
                        message = message.next;
                        if (message.period == displayPeriod && 
                            message.day != 0) {
                            if ((min == 0) || (message.day < min)) 
                                min = message.day;
                        }
                    }
                }
    
                for (MessageDayTreeNode message: messages) {
                    if (message.period == displayPeriod && message.day != 0) 
                        message.offset = (int)(message.day - min);
    
                    while (message.next != null) {
                        message = message.next;
                        if (message.period == displayPeriod && 
                            message.day != 0) {
                            message.offset = (int)(message.day - min);
                        }
                    }
                }
    
                totalWidth = 5;
                totalHeight = 5;
    
                int y = 15 + margin;
                int x;
                int offsetHeight;
    
                for (MessageDayTreeNode message: messages) {
                    offsetHeight = 0;
                    if (message.period == displayPeriod) {
                        x = 5 + (1 + (GRIDWIDTH * message.offset)) + 
                                (5 * message.toString().length());
    
                        if (x > totalWidth) 
                            totalWidth = x;
    
                        offsetHeight = GRIDHEIGHT;
    
                        if ((message.type == 3) || 
                            ((message.type != 1) && (message.type != 2))) {
                            offsetHeight -= 10;
                        }
                    }
    
                    message = message.next;
                    while (message != null) {
                        if (message.period == displayPeriod) {
                            x = 5 + (1 + (GRIDWIDTH * message.offset)) + 
                                    (5 * message.toString().length());
    
                            if (x > totalWidth) 
                                totalWidth = x;
    
                            offsetHeight = GRIDHEIGHT;
    
                            if ((message.type == 3) || 
                                ((message.type != 1) && (message.type != 2))) {
                                offsetHeight -= 10;
                            }
                        }
    
                        message = message.next;
                    }
    
                    y += offsetHeight;
                }

                totalHeight = y;
                setPreferredSize(new Dimension(totalWidth, totalHeight));
            }
        }
    }
}
