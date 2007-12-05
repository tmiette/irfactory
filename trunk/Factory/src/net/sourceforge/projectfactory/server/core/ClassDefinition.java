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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/server/core/ClassDefinition.java,v $
$Revision: 1.17 $
$Date: 2007/03/04 21:03:29 $
$Author: ddlamb_2000 $

*/
package net.sourceforge.projectfactory.server.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.List;

import net.sourceforge.projectfactory.server.xml.TransactionXML;
import net.sourceforge.projectfactory.xml.WriterXML;
import net.sourceforge.projectfactory.xml.XMLWrapper;


/**
 * Represents a class definition.
 * @author David Lambert
 */
public class ClassDefinition extends ClassDefinitionBase {

    /** Writes the object as an XML output. */
    public void xmlOut(WriterXML xml, TransactionXML transaction, 
                       boolean tags) {
        if (tags) xmlStart(xml, "classdefinition");

        super.xmlOut(xml, transaction, false);

        if(transaction.isDetail()) {
            generateCode(transaction);
            xmlOut(xml, "generatedcode", generatedcode);
        }

        if (transaction.isExpand() || transaction.isSummary()) 
            xmlOutExpandClassCode(xml, transaction);

        if (tags) xmlEnd(xml);
    }

    /** Writes associated objects as an XML output. */
    private void xmlOutExpandClassCode(WriterXML xml, 
                              TransactionXML transaction) {
        for (ClassCode classCode: transaction.getServer().core.classCodes) 
            if (classCode.packageReference != null && 
                    classCode.packageReference.equals(packageReference) &&
                    classCode.className != null &&
                    classCode.className.equals(this)) 
                classCode.xmlOutSummary(xml, "classcode");
    }

    /** Initializes with default values. */
    public void defaults(TransactionXML transaction) {
        super.defaults(transaction);
        uniqueName = true;
		classType = "standalone";
    }

    /** Validates the object before any save or update. */
    public boolean xmlValidate(WriterXML xml, TransactionXML transaction, 
                            List list) {
        if(super.xmlValidate(xml, transaction, list))
            return true;


        if(!isActive() || draft || packageReference == null) 
            return false;
            
        if(transaction.getSession().isRemote()) {
            draft = true;
            return false;
        }
            
        generateCode(transaction);
            
        String path = transaction.getServer().getLocalhost().getPath();
        if(path == null || path.length() == 0) {
            draft = true;
            return false;
        }

        /** Write the output into a file. */
         String filename = path;
         if(!filename.endsWith(XMLWrapper.SLASH))
             filename = filename + XMLWrapper.SLASH;
         filename = filename + 
                     XMLWrapper.replaceAll(packageReference.getName(), ".", XMLWrapper.SLASH) + 
                     XMLWrapper.SLASH + getName() + 
                     (createDerived ? "Base" : "") + 
                     ".java";
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

        /** Write the output into a file. */
        if(createDerived) {
             filename = path;
             if(!filename.endsWith(XMLWrapper.SLASH))
                 filename = filename + XMLWrapper.SLASH;
             filename = filename + 
                         XMLWrapper.replaceAll(packageReference.getName(), ".", XMLWrapper.SLASH) + 
                         XMLWrapper.SLASH + getName() + ".java";
            try {
                File file = new File(filename);
                if(!file.exists()) {
                    OutputStreamWriter fileWriter = 
                            new OutputStreamWriter(
                                new FileOutputStream(filename), "UTF-8");
                    BufferedWriter outputFile = new BufferedWriter(fileWriter);
                    outputFile.write(generatedcodeExtends);
                    outputFile.close();
                    xmlMessage(xml, "message:savingcomplete", filename);
                }
            } catch (IOException e) {
                xmlError(xml, "error:filenotsaved", filename);
                xml.xmlException(e);
                return true;
            }
        }

        return false;
    }

    /** Provides a summary when the object is displayed in a list. */
    public String getSummary() {
        String indicator = "";
        if (draft)
            indicator = "@label:inactive";

        else if ("standalone".equals(classType))
            indicator = "@label:bold";
            
        if(createDerived)
            indicator = indicator + " (*)";

        return super.getSummary() + indicator;
    }

    /** Generates java code. */
    private void generateCode(TransactionXML transaction) {
        generatedcode = "";
        generatedcodeExtends = "";
        if(packageReference == null)
            return;
            
        out(packageReference.headerFile);
        out("");
        out("package " + packageReference.getName() + ";");
        out("");

        // Import classes        
        for (ClassItem item: items) {
           if(item.type != null && 
                (item.type.equals("class") || item.type.equals("list")) && 
                item.classReference != null) {
                for(ClassDefinition reference: transaction.getServer().core.classes) {
                    if(reference.getName().equals(item.classReference) &&
                        reference.packageReference != null) {
                        out("import " + 
                                reference.packageReference.getName() + 
                                "." + item.classReference + ";");
                        break;
                    }
                }
           }
        }
        if(classType!= null && classType.equals("standalone")) {
            out("import net.sourceforge.projectfactory.server.entities.Entity;");
            out("import net.sourceforge.projectfactory.server.entities.xml.BaseEntityServerXML;");
        }
        if(classType!= null && classType.equals("subclass")) 
            out("import net.sourceforge.projectfactory.server.entities.BaseEntity;");
        if(classType!= null && classType.equals("duration")) 
            out("import net.sourceforge.projectfactory.server.entities.Duration;");
        out("import net.sourceforge.projectfactory.server.xml.TransactionXML;");
        out("import net.sourceforge.projectfactory.xml.FactoryWriterXML;");

        out("");
        out("/** ");
        if(comment != null) {
            String[] code = comment.split("\n");
            for(String line: code) 
                out("  * " + line);
        }
        out("  * @author " + transaction.getSession().getOperatorName());
        out("  */");
        if(superClass != null) 
            out("public class " + getName() + 
                    (createDerived ? "Base" : "") + 
                    " extends " + superClass + " {");
        else if(classType != null && classType.equals("standalone")) 
            out("public class " + getName() + 
                    (createDerived ? "Base" : "")+ 
                    " extends Entity {");
        else if(classType != null && classType.equals("subclass")) 
            out("public class " + getName() + 
                    (createDerived ? "Base" : "")+ 
                    " extends BaseEntity {");
        else if(classType != null && classType.equals("duration")) 
            out("public class " + getName() + 
                    (createDerived ? "Base" : "")+ 
                    " extends Duration {");
            
        // Declarations
        for (ClassItem item: items) {
            if(item.type != null) {
                if(item.type.equals("string")) {
                        out("    public String " + item.xmlTag + ";");
                } 
                else if(item.type.equals("date")) {
                    out("    public java.util.Date " + item.xmlTag + ";");
                }
                else if(item.type.equals("integer")) {
                    out("    public int " + item.xmlTag + ";");
                }
                else if(item.type.equals("boolean")) {
                    out("    public boolean " + item.xmlTag + ";");
                }
                else if(item.type.equals("class") && item.classReference != null) {
                    for(ClassDefinition reference: transaction.getServer().core.classes)
                        if(reference.getName().equals(item.classReference)) {
                            out("    public " + item.classReference + " " + item.xmlTag + ";");
                            break;
                        }
                }
                else if(item.type.equals("list") && item.classReference != null) {
                    out("    public java.util.List<" + item.classReference + 
                                "> " + item.xmlTag + 
                                " = new java.util.ArrayList();");
                }
            }
        }

        // Static values
        for (ClassValue item: staticValues)
               out("    public static int " + item.label + " = " + item.staticValue + ";");

        // Constructor (if parent)
         for (ClassItem item: items) {
            if(item.fromParent && item.type != null && 
                    item.type.equals("class") && 
                    item.classReference != null) {
                for(ClassDefinition reference: transaction.getServer().core.classes)
                    if(reference.getName().equals(item.classReference)) {
                        out("");
                        out("    /** Constructor. */");
                        out("    public " + getName() + 
                                        (createDerived ? "Base" : "") + 
                                        "(" + item.classReference + " " +
                                        item.xmlTag + ") {");
                        out("        this." + item.xmlTag + " = " + item.xmlTag + ";");
                        out("    }");
                        break;
                    }
                }
         }

        // xmlOut
        out("");
        out("    /** Writes the object as an XML output. */");
        out("    public void xmlOut(FactoryWriterXML xml, TransactionXML transaction, boolean tags) {");
        out("        if (tags) xmlStart(xml, \"" + 
                        (xmlTag != null ? xmlTag.toLowerCase() : getName().toLowerCase()) + 
                        "\");");
        out("        super.xmlOut(xml, transaction, false);");
        out("        if (transaction.isDetail() || transaction.isSave()) {");
        for (ClassItem item: items) {
            if(classType!= null && !item.fromParent) {
                String statement = 
                        classType.equals("subclass") || classType.equals("duration") ?
                        "xmlAttribute" : "xmlOut";
                if(item.type.equals("string")) {
                    out("            " + 
                                statement + "(xml, \"" + 
                                item.getTag().toLowerCase() + "\", " + 
                                item.getTag() + ");");
                }
                else if(item.type.equals("boolean")) {
                    out("            " + 
                                statement + "(xml, \"" + 
                                item.getTag().toLowerCase() + "\", " + 
                                item.getTag() + ");");
                }
                else if(item.type.equals("date")) {
                    out("            " + 
                                statement + "(xml, \"" + 
                                item.getTag().toLowerCase() + "\", " + 
                                item.getTag() + ");");
                }
                else if(item.type.equals("integer")) {
                    out("            " + 
                                statement + "(xml, \"" + 
                                item.getTag().toLowerCase() + "\", " + 
                                item.getTag() + ");");
                }
                else if(item.type.equals("class") && item.classReference != null) {
                    out("            " + 
                                statement + "(xml, transaction, \"" + 
                                item.getTag().toLowerCase() + "\", " + 
                                item.getTag() + ");");
                }
                else if(item.type.equals("list") && item.classReference != null) {
                    out("            " + 
                                statement + "(xml, transaction, " + 
                                item.getTag() + ");");
                }
            }
        }
        out("        }");
        out("        if (tags) xmlEnd(xml);");
        out("    }");

        // xmlIn
        out("");
        out("    /** Reads the object from an XML input. */");
        out("    public boolean xmlIn(FactoryWriterXML xml, TransactionXML transaction, String tag, String value) {");
        out("        if (super.xmlIn(xml, transaction, tag, value)) return true;");
        for (ClassItem item: items) {
            if(!item.fromParent && !item.type.equals("list")) {
                if(item.type.equals("string")) {
                    out("        if (tag.equals(\""+ item.getTag().toLowerCase() + "\")) {");
                    out("            " + item.getTag() + " = value;");
                    out("            return true;");
                    out("        }");
                }
                else if(item.type.equals("boolean")) {
                    out("        if (tag.equals(\""+ item.getTag().toLowerCase() + "\")) {");
                    out("            " + item.getTag() + " = xmlInBoolean(value);");
                    out("            return true;");
                    out("        }");
                }
                else if(item.type.equals("date")) {
                    out("        if (tag.equals(\""+ item.getTag().toLowerCase() + "\")) {");
                    out("            " + item.getTag() + " = xmlInDate(xml, value);");
                    out("            return true;");
                    out("        }");
                }
                else if(item.type.equals("integer")) {
                    out("        if (tag.equals(\""+ item.getTag().toLowerCase() + "\")) {");
                    out("            " + item.getTag() + " = xmlInInt(xml, value);");
                    out("            return true;");
                    out("        }");
                }
                else if(item.type.equals("class") && 
                            item.classReference != null &&
                            item.listName != null) {
                    String controlPrefix = "";
                    if(item.fromParent) 
                        controlPrefix = " && " + item.xmlTag + " != null";

                    int lastDot = item.listName.lastIndexOf(".");
                    if(lastDot>0)
                        controlPrefix = controlPrefix + 
                                        " && " + 
                                        item.listName.substring(0, lastDot) + 
                                        " != null";
                    String parent = "this";
                    for (ClassItem item2: items) {
                        if(item2.fromParent) {
                            parent = item2.xmlTag;
                            break;
                        }
                    }

                                        
                    out("        if (tag.equals(\""+ 
                                    item.getTag().toLowerCase() + 
                                    "\")" + 
                                    controlPrefix + ") {");
                    out("            " + item.getTag() + 
                                    " = (" + item.classReference + ")" +
                                    "xmlInEntity(xml,transaction,value,");
                    out("                new " + item.classReference + "()," +
                                    item.listName + ",");
                    out("                " + (item.errorMessage == null ? 
                                        null : 
                                        "\"" + item.errorMessage + "\"") + 
                                    "," + parent + ");");
                    out("            return true;");
                    out("        }");
                }
            }
        }
        out("        return false;");
        out("    }");


        // xmlIn
        if(classType!= null && classType.equals("standalone")) {
            out("");
            out("    /** Starts a tag. */");
            out("    public BaseEntityServerXML xmlIn(TransactionXML transaction, String tag) {");
            for (ClassItem item: items) {
                if(!item.fromParent && item.type.equals("list") && item.classReference != null) {
                    for(ClassDefinition reference: transaction.getServer().core.classes)
                        if(reference.getName().equals(item.classReference)) {
                            String arguments = "";
                            for (ClassItem item2: reference.items) {
                                if(item2.fromParent &&
                                    item2.classReference != null &&
                                    item2.classReference.equals(getName())) {
                                        arguments = "(" + getName() + ")this";
                                        break;
                                    }
                            }
                            
                            String xmlTag = reference.xmlTag;
                            if(xmlTag == null)
                                xmlTag = item.classReference;
                            out("        if (tag.equals(\""+ xmlTag.toLowerCase() + "\"))");
                            out("            return new BaseEntityServerXML(transaction, new " +
                                        item.classReference + "(" + arguments + ")," +
                                        item.xmlTag + ");");
                            break;
                        }
                }
            }
            out("        return null;");
            out("    }");
        }

        // update
        if(classType != null && classType.equals("standalone"))  {
            out("");
            out("    /** Update method : updates the object from another entity. */");
            out("    public void update(TransactionXML transaction, Entity other) {");
            out("        if (this.getClass() != other.getClass()) return;");
            out("        " + getName() + 
                                (createDerived ? "Base" : "") +
                                " otherEntity = (" + 
                                getName() + 
                                (createDerived ? "Base" : "") +
                                ") other;");
            out("        super.update(transaction, other);");
            for (ClassItem item: items) {
                if(!item.fromParent) {
                    if(item.type.equals("string")) {
                        out("        this." + 
                                    item.getTag() + 
                                    " = otherEntity." + 
                                    item.getTag() + ";");
                    }
                    else if(item.type.equals("boolean")) {
                        out("        this." + 
                                    item.getTag() + 
                                    " = otherEntity." + 
                                    item.getTag() + ";");
                    }
                    else if(item.type.equals("date")) {
                        out("        this." + 
                                    item.getTag() + 
                                    " = otherEntity." + 
                                    item.getTag() + ";");
                    }
                    else if(item.type.equals("integer")) {
                        out("        this." + 
                                    item.getTag() + 
                                    " = otherEntity." + 
                                    item.getTag() + ";");
                    }
                    else if(item.type.equals("class") && item.classReference != null) {
                        out("        this." + 
                                    item.getTag() + 
                                    " = otherEntity." + 
                                    item.getTag() + ";");
                    }
                    else if(item.type.equals("list")) {
                        out("        update(this." + 
                                    item.getTag() + 
                                    ",otherEntity." + 
                                    item.getTag() + ");");
                    }
                }
            }
            out("    }");
        }
        
        if(!uniqueName && classType.equals("standalone")) {
            out("    /** Indicates if the names must be unique in the system or not. */");
            out("    protected boolean hasUniqueName() {");
            out("        return false;");
            out("    }");
        }

        // Add additional methods attached to the panel
        for(ClassCode classCode : transaction.getServer().core.classCodes) {
            if(classCode.isActive() && 
                    classCode.className != null && 
                    classCode.className.equals(this)) {
                out("");
                out("    /** " + classCode.smallComments + " */");
                out("    " + classCode.getName() + " {");
                if(classCode.code != null) {
                    String [] code = classCode.code.split("\n");
                    for(String line: code) 
                        out("        " + line);
                }
                out("    }");
            }
        }

        out("}");

        // Derived class
        if(createDerived) {
            outExtends(packageReference.headerFile);
            outExtends("");
            outExtends("package " + packageReference.getName() + ";");
            outExtends("");
            outExtends("/** ");
            if(comment != null) {
                String[] code = comment.split("\n");
                for(String line: code) 
                    outExtends("  * " + line);
            }
            outExtends("  * @author " + transaction.getSession().getOperatorName());
            outExtends("  */");
            outExtends("public class " + getName() + " extends " + getName() + "Base {");
            outExtends("}");
        }
    }
}
