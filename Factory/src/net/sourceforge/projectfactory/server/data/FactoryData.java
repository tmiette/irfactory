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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/data/FactoryData.java,v $
$Revision: 1.6 $
$Date: 2007/02/27 22:12:41 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.data;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.projectfactory.server.FactoryServerBase;
import net.sourceforge.projectfactory.server.data.ComboBox;
import net.sourceforge.projectfactory.server.data.Dictionary;
import net.sourceforge.projectfactory.server.data.Preference;
import net.sourceforge.projectfactory.server.entities.Entity;
import net.sourceforge.projectfactory.server.entities.xml.SubEntityServerXML;
import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Aggregates all the system data objects.
 * @author David Lambert
 */
public class FactoryData extends FactoryServerBase {

    /** Reference to preferences. */
    public Preference preference = new Preference();

    /** List of dictionary entries. */
    public List<Dictionary> dictionaries = new ArrayList(500);

    /** List of combo boxes. */
    public List<ComboBox> comboBoxes = new ArrayList(500);

    /** List of action bars. */
    public List<ActionBar> actionBars = new ArrayList(10);

    /** Constructor. */
    public FactoryData(FactoryServerBase serverBase) {
        super(serverBase);
    }

    /** Clears objects. */
    public void clear() {
        dictionaries.clear();
		comboBoxes.clear();
        actionBars.clear();
    }

    /** Sorts objects. */
    public void sort() {
        Collections.sort(dictionaries);
        Collections.sort(comboBoxes);
        Collections.sort(actionBars);
    }

    /** Saves all entities. */
    public void saveAll(FactoryWriterXML xml, TransactionXML transaction, 
                        boolean demo, String iid) {
        for (String extension: 
                transaction.getServer().getApplicationExtensions()) {
            saveDictionaries(xml, transaction, extension, iid);
        }
    }

    /** Saves all preferences. */
    private void savePreferences(FactoryWriterXML xml, 
                                 TransactionXML transaction) {
        saveEntity(xml, transaction, preference, false, null);
    }

    /** Saves all dictionaries. */
    private void saveDictionaries(FactoryWriterXML xml, 
                                  TransactionXML transaction, 
                                  String application, String iid) {
        for (Dictionary dico: dictionaries)
            if (dico.getApplication().equals(application))
                saveEntity(xml, transaction, dico, false, iid);

        for (ComboBox combo: comboBoxes)
            if (combo.getApplication().equals(application))
                saveEntity(xml, transaction, combo, false, iid);

        for (ActionBar actionBar: actionBars)
            if (actionBar.getApplication().equals(application))
                saveEntity(xml, transaction, actionBar, false, iid);
    }

    /** Saves all objects in file(s). */
    public void saveAll(FactoryWriterXML xml, TransactionXML transaction) {
        for (String extension: 
				transaction.getServer().getApplicationExtensions()) {

            String filename = 
                transaction.getServer().getPath() + 
                XMLWrapper.SLASH + "dictionary" + extension + ".xml";
            try {
                FactoryWriterXML outputXml = 
                    new FactoryWriterXML(filename, "factory");
                saveDictionaries(outputXml, transaction, extension, null);
                outputXml.end();
                traceSaving(xml, filename);
            } catch (IOException ex) {
                traceError(xml, filename);
            }
        }
    }

    /** Saves preferences in file(s). */
    public void saveAllPreferences(FactoryWriterXML xml, 
                                   TransactionXML transaction) {
        String filename = transaction.getServer().getPath() + 
                            XMLWrapper.SLASH + "preferences.xml";
        try {
            FactoryWriterXML outputXml = 
                new FactoryWriterXML(filename, "factory");
            savePreferences(outputXml, transaction);
            outputXml.end();
            traceSaving(xml, filename);
        } catch (IOException ex) {
            ex.printStackTrace();
            traceError(xml, filename);
        }
    }

    /** Adds files to be opened. */
    public void addFileList(List<String> files, String prefix) {
        files.add(prefix + "dictionary.xml");
    }

    /** Adds classes used for replication. */
    public void addClassList(List classes) {
        classes.add("dictionary");
        classes.add("combobox");
    }

    /** Sends the object names to the client based on a list. */
    public boolean isAvailable(TransactionXML transaction, String category, 
                               String filter, Entity entity) {
        return true;
    }

    /** Creates a list based on category. */
    public void listByCategory(TransactionXML transaction, List lists, 
                               String category, String filter) {
        if (category.equals("data")) {
            if (filter.equals("filter:system:dictionaries"))
                lists.add(dictionaries);
            if (filter.equals("filter:system:comboboxes"))
                lists.add(comboBoxes);
            if (filter.equals("filter:system:actionbar"))
                lists.add(actionBars);
        }
    }

    /** Creates the list based on class. */
    public void listByClass(TransactionXML transaction, List lists, 
                            String classname) {
        if (classname.equals("dictionary"))
            lists.add(dictionaries);
        if (classname.equals("combobox"))
            lists.add(comboBoxes);
        if (classname.equals("actionbar"))
            lists.add(actionBars);
    }

    /** Returns the appropriate parser in order to read an element. */
    public SubEntityServerXML getParser(TransactionXML transaction, String tag) {
        if (tag.equals("dictionary")) 
            return new SubEntityServerXML(this, transaction, new Dictionary(), dictionaries);
        else if (tag.equals("combobox")) 
            return new SubEntityServerXML(this, transaction, new ComboBox(), comboBoxes);
        else if (tag.equals("actionbar")) 
            return new SubEntityServerXML(this, transaction, new ActionBar(), actionBars);
        return null;
    }
}
