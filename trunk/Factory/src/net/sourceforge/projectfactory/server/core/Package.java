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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/core/Package.java,v $
$Revision: 1.11 $
$Date: 2007/02/27 22:12:14 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.core;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.List;

import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Represents a package used in order to store application definitions.
 * @author David Lambert
 */
public class Package extends PackageBase {

    /** Writes the object as an XML output. */
    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags) xmlStart(xml, "package");
        super.xmlOut(xml, transaction, false);
        if(transaction.isDetail()) {
            if(gotPanels(transaction)) {
                generateCode(transaction);
                xmlOut(xml, "generatedcode", generatedcode);
            }
        }
        if (transaction.isExpand() || transaction.isSummary()) xmlOutExpand(xml, transaction);
        if (tags) xmlEnd(xml);
    }

    /** Writes associated objects as an XML output. */
    private void xmlOutExpand(FactoryWriterXML xml, 
                              TransactionXML transaction) {
        for (Grid grid: transaction.getServer().core.grids) 
            if (grid.packageReference != null && 
                    grid.packageReference.equals(this)) 
                grid.xmlOutSummary(xml, "grid");

        for (Panel panel: transaction.getServer().core.panels) 
            if (panel.packageReference != null && 
                    panel.packageReference.equals(this)) {
                xmlStart(xml, "panel");
                panel.xmlSummary(xml);
                xmlOutExpandPanelCode(xml, transaction, panel);
                xmlEnd(xml);
        }

        for (ClassDefinition classDefinition: transaction.getServer().core.classes) 
            if (classDefinition.packageReference != null && 
                    classDefinition.packageReference.equals(this))  {
                xmlStart(xml, "classdefinition");
                classDefinition.xmlSummary(xml);
                xmlOutExpandClassCode(xml, transaction, classDefinition);
                xmlEnd(xml);
        }
    }

    /** Writes associated objects as an XML output. */
    private void xmlOutExpandPanelCode(FactoryWriterXML xml, 
                              TransactionXML transaction,
                              Panel panel) {
        for (PanelCode panelCode: transaction.getServer().core.panelCodes) 
            if (panelCode.packageReference != null && 
                    panelCode.packageReference.equals(this) &&
                    panelCode.panelName != null &&
                    panelCode.panelName.equals(panel)) 
                panelCode.xmlOutSummary(xml, "panelcode");
    }

    /** Writes associated objects as an XML output. */
    private void xmlOutExpandClassCode(FactoryWriterXML xml, 
                              TransactionXML transaction,
                              ClassDefinition classDefinition) {
        for (ClassCode classCode: transaction.getServer().core.classCodes) 
            if (classCode.packageReference != null && 
                    classCode.packageReference.equals(this) &&
                    classCode.className != null &&
                    classCode.className.equals(classDefinition)) 
                classCode.xmlOutSummary(xml, "classcode");
    }

    /** Indicates if the package contains panels. */
    private boolean gotPanels(TransactionXML transaction) {
        for (Panel panel: transaction.getServer().core.panels) 
            if (panel.packageReference != null && 
                    panel.packageReference.equals(this)) {
                return true;
        }
        return false;
    }

    /** Validates the object before any save or update. */
    public boolean xmlValidate(FactoryWriterXML xml, TransactionXML transaction, 
                            List list) {
        if(super.xmlValidate(xml, transaction, list))
            return true;


        if(!isActive()) 
            return false;
            
        if(gotPanels(transaction)) {
            generateCode(transaction);
                
            /** Write the output into a file. */
			String path = transaction.getServer().getLocalhost().getPath();
			if(path == null || path.length() == 0) 
				return false;
			
            String filename = path;
            if(!filename.endsWith(XMLWrapper.SLASH))
                filename = filename + XMLWrapper.SLASH;
            filename = filename + 
                        XMLWrapper.replaceAll(getName(), ".", XMLWrapper.SLASH) + 
                        XMLWrapper.SLASH + "PanelLoader.java";
            try {
                OutputStreamWriter fileWriter = 
                        new OutputStreamWriter(
                            new FileOutputStream(filename), "UTF-8");
                BufferedWriter outputFile = new BufferedWriter(fileWriter);
                outputFile.write(generatedcode);
                outputFile.close();
                xmlMessage(xml, "message:savingcomplete", filename);
            } catch (IOException e) {
                xmlError(xml, "error:filenotsaved", filename);
                xml.xmlException(e);
                return true;
            }
        }
        
        return false;
    }

    /** Generates java code. */
    private void generateCode(TransactionXML transaction) {
        generatedcode = "";
            
        out(headerFile);
        out("");
        
        out("package " + getName() + ";");
        out("");
        out("import net.sourceforge.projectfactory.client.FrameMain;");
        out("");
        out("/** ");
        if(comment != null) {
            String[] code = comment.split("\n");
            for(String line: code) 
                out("  * " + line);
        }
        out("  * @author " + transaction.getSession().getOperatorName());
        out("  */");
        out("public class PanelLoader {");

        out("");
        out("    /** Construct data panels and attach them to the main frame. */");
        out("    public void load(FrameMain frame) throws Exception {");

        for (Panel panel: transaction.getServer().core.panels) 
            if (panel.packageReference != null && 
                    panel.packageReference.equals(this)) {
                if(panel.isActive())
                    out("        frame.addPanel(new " + 
                                    panel.getName() + 
                                    "(frame), \"" + 
                                    panel.getName().toLowerCase() + "\");");
        }
        out("    }");

        out("}");
    }
}
