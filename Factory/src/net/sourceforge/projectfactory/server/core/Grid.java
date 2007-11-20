/*

Copyright (c) 2006, 2007 David Lambert

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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/core/Grid.java,v $
$Revision: 1.11 $
$Date: 2007/02/27 22:12:14 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.core;


/**
 * Represents a grid used in panels.
 * @author David Lambert
 */
public class Grid extends GridBase {

    /** Used in order to generate code. */
    private String definition;
    
    /** Returns a string that defines the grid. */
    String getDefinition() {
        definition = "";
        int i = 0;
        for (GridItem item: items) {
            if(i++ > 0)
                definition = definition + ",";
            
            definition = definition +
                            "\n                                " +
                            "\"" + item.getTag().toLowerCase() + "\"" +
                            "," +
                            "\"" + item.getLabel() + "\"" +
                            "," +
                            item.length;
        }
        return definition;
    }
    
    /** Returns a string that initialize the grid. */
    String getInitialisation(Panel panel, String xmlTag) {
        definition = "";
        
        if(noSort)  
            add(xmlTag + ".setNoSort();");
        if(noButton)  
            add(xmlTag + ".hideButtons();");
        if(hierarchical)  
            add(xmlTag + ".setHierarchical();");

        int i = 0;
        for (GridItem item: items) {
            if(item.type != null && item.type.equals("date"))  {
                add(xmlTag + ".setDateType(" + i + ",this);");
            }
            else if(item.type != null && item.type.equals("boolean"))  {
                add(xmlTag + ".setBooleanType(" + i + ");");
            }
            else if(item.type != null && item.type.equals("highlight"))  {
                add(xmlTag + ".setHighlight(" + i + ");");
            }
            else if(item.type != null && item.type.equals("integer"))  {
                add(xmlTag + ".setIntegerType(" + i + ");");
            }
            else if(item.type != null && item.type.equals("percent"))  {
                add(xmlTag + ".setPercentType(" + i + ");");
            }
            else if(item.type != null && item.type.equals("action"))  {
                add(xmlTag + ".setActionType(" + i + ", this);");
            }
            if(item.comboBox != null)  {
                add(xmlTag + ".setCombo(" + i + 
                                    ", new ComboBoxCode(\"" + 
                                    item.comboBox + 
                                    "\"));");
            }

            if(item.defaultValue != null)  {
                add(xmlTag + ".setColumnDefault(\"" + 
                                    item.defaultValue + 
                                    "\", " + i + ");");
            }

            if(item.lookupClass != null)  {
                String lookupGrid = "";
                if(item.lookupGrid != null) {
                    for(PanelItem itemPanel: panel.items) {
                        if(itemPanel.type.equals("grid") &&
                            itemPanel.grid != null &&
                            itemPanel.grid.equals(item.lookupGrid) &&
                            itemPanel.xmlTag != null) {
                                lookupGrid = itemPanel.xmlTag;
                                break;
                            }
                    }
                }
                add(xmlTag + ".attachLookup(" + 
                                    i +
                                    ",this,\"" +
                                    item.lookupClass.toLowerCase() + 
                                    "\"" + 
                                    (lookupGrid.length() > 0 ? "," + lookupGrid : "") +
                                    ");");
            }

            if(item.readOnly)  
                add(xmlTag + ".setReadOnly(" + i + ");");

            if(item.duplicate)  
                add(xmlTag + ".setDupMode(" + i + ");");

            ++i;
        }
        return definition;
    }

    /** Adds a string to the string definition. */
    private void add(String newDefinition) {
        if(newDefinition.length() > 0) {
            if(definition.length() > 0)
                definition = definition + "\n";
            definition = definition + "        " + newDefinition;
        }
    }
    
    /** Indicates if the grid requires a lookup. */
    boolean gotLookup() {
        for (GridItem item: items) 
            if(item.type != null && item.type.equals("date"))
                return true;
        for (GridItem item: items) 
            if(item.lookupClass != null)
                return true;
        return false;
    }
}
