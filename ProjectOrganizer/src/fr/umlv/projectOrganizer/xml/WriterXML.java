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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/xml/FactoryWriterXML.java,v $
$Revision: 1.16 $
$Date: 2007/03/04 21:04:35 $
$Author: ddlamb_2000 $

*/
package fr.umlv.projectOrganizer.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import fr.umlv.projectOrganizer.AboutProjectsOrganizer;

/**
  * Class defined in order to provide an easy way to generate XML on the fly.
  * This class is two-fold since it provides 2 output streams, one for data
  * and one for errors. The class is used in conjonction with FactoryReaderXML
  * where it servers at output during XML parsing.
  * The role of this class is also to provide a level of
  * abstraction for Factory.
  * @author David Lambert
  */
public class WriterXML {

    /** SAX Transformer. */
    private SAXTransformerFactory xmlOutput;

    /** Handler toward output stream. */
    private TransformerHandler out;

    /** Handler toward error stream. */
    private TransformerHandler err;

    /** Name of the document to generate. */
    private String document;

    /** Writer for output stream. */
    private Writer outWriter;

    /** Writer for error stream. */
    private Writer errWriter;

    /** Tag currently generated. */
    private String tag;

    /** Tag currently generated. */
    private String tagErr;
    
    /** Error flag. */
    private boolean error;

    /** Stack of tags. */
    private List<String> stackTags = new ArrayList(5);

    /** Stack of tags. */
    private List<String> stackTagsErr = new ArrayList(5);

    /** Attributes attached to an element. */
    private AttributesImpl attributesImpl;

    /** Attributes attached to an element. */
    private AttributesImpl attributesImplErr;

    /** Default document name for output stream. */
    public static final String RESPONSE = "response";

    /** Default document name for error stream. */
    public static final String ERRORS = "errors";

    /** Level for messages. */
    public static final String TRACE = "trace";

    /** Level for messages. */
    public static final String MESSAGE = "message";

    /** Level for messages. */
    public static final String WARNING = "warning";

    /** Level for messages. */
    public static final String ERROR = "error";

    /** Level for messages. */
    public static final String FATAL = "fatal";

    /** Constructor. */
    public WriterXML(Writer outWriter, String document, 
                            boolean indent) {
        xmlOutput = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
        try {
            out = xmlOutput.newTransformerHandler();
            err = xmlOutput.newTransformerHandler();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            out = null;
            err = null;
            return;
        }
        this.outWriter = outWriter;
        this.errWriter = new StringWriter();
        if (indent) {
            out.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
            out.getTransformer().setOutputProperty(OutputKeys.STANDALONE, 
                                                   "yes");
        }
        out.setResult(new StreamResult(outWriter));
        err.setResult(new StreamResult(errWriter));
        try {
            out.startDocument();
            err.startDocument();
            /*this.document = document;
            if (document != null) {
                
                out.startElement("", "", document, null);
                err.startElement("", "", ERRORS, null);
                if (indent)
                    out.comment(AboutProjectsOrganizer.getBuild().toCharArray(), 0, 
                                AboutProjectsOrganizer.getBuild().length());
            }*/
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /** Constructor. */
    public WriterXML(String document) {
        this(new StringWriter(), document, false);
    }

    /** Constructor. */
    public WriterXML(String document, boolean indent) {
        this(new StringWriter(), document, indent);
    }

    /** Constructor. */
    public WriterXML(String fileName, 
                            String document) throws UnsupportedEncodingException, 
                                                    FileNotFoundException {
        this(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"), 
             document, true);
    }

    /** Constructor. */
    public WriterXML() {
        this(new StringWriter(), RESPONSE, false);
    }

    /** Copies the content of the provided document in the document. */
    public void copyFrom(String document) {
        xmlOutput = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
        try {
            out = xmlOutput.newTransformerHandler();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            out = null;
            err = null;
            return;
        }
        this.outWriter = new StringWriter();
        try {
            this.outWriter.write(document);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            out = null;
            err = null;
            return;
        }
        out.setResult(new StreamResult(outWriter));
        out = null;
    }

    /** Copies the content of the provided document in the document. */
    public void copyErrorFrom(String document) {
        xmlOutput = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
        try {
            err = xmlOutput.newTransformerHandler();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            out = null;
            err = null;
            return;
        }
        this.errWriter = new StringWriter();
        try {
            this.errWriter.write(document);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            out = null;
            err = null;
            return;
        }
        err.setResult(new StreamResult(errWriter));
        err = null;
    }

    /** Ends the generation. */
    public final void end() {
        try {
            if (out == null || err == null)
                return;
            if (document != null) {
                out.endElement("", "", document);
                err.endElement("", "", ERRORS);
                document = null;
            }
            out.endDocument();
            err.endDocument();
        } catch (SAXException e) {
            xmlException(e);
        }
    }

    /** Returns the data output writer. */
    public final Writer getOutWriter() {
        end();
        return outWriter != null ? outWriter : new StringWriter();
    }

    /** Returns the error output writer. */
    public final Writer getErrWriter() {
        return errWriter != null ? errWriter : new StringWriter();
    }

    /** Prepares an element for output in the data stream. */
    public final WriterXML xmlStart(String tag) {
        if (out == null)
            return this;
        xmlClass();
        this.tag = tag;
        stackTags.add(tag);
        attributesImpl = new AttributesImpl();
        return this;
    }

    /** Adds an attribute. */
    public final WriterXML xmlAttribute(String tag, String value) {
        if (out == null)
            return this;
        if (value != null && attributesImpl != null){
        	attributesImpl.addAttribute("", "", tag, "CDATA", value);
        }
        return this;
    }

    /** Adds an attribute. */
    public final WriterXML xmlAttribute(String tag, int value) {
        return xmlAttribute(tag, Integer.toString(value));
    }

    /** Outputs the element. */
    private final WriterXML xmlClass() {
        if (out == null)
            return this;
        if (tag != null && attributesImpl != null) {
            try {
                out.startElement("", "", tag, attributesImpl);
            } catch (SAXException e) {
                xmlException(e);
            }
        }
        attributesImpl = null;
        return this;
    }

    /** Adds an element (tag / value) into the current element
     *  as like as an attribute. */
    public final WriterXML xmlOut(String tag, String value) {
        if (out == null)
            return this;
        xmlClass();
        if (tag != null && value != null && value.length() > 0) {
            try {
                out.startElement("", "", tag, null);
                out.characters(value.toCharArray(), 0, value.length());
                out.endElement("", "", tag);
            } catch (SAXException e) {
                xmlException(e);
            }
        }
        return this;
    }

    /** Adds an element (tag / value) into the current element
     *  as like as an attribute. */
    public final WriterXML xmlOut(String tag, int value) {
        return xmlOut(tag, Integer.toString(value));
    }

    /** Adds an empty element into the current element. */
    public final WriterXML xmlOut(String tag) {
        if (out == null)
            return this;
        xmlClass();
        if (tag != null) {
            try {
                out.startElement("", "", tag, null);
                out.endElement("", "", tag);
            } catch (SAXException e) {
                xmlException(e);
            }
        }
        return this;
    }

    /** Finishes the output of the element. */
    public final WriterXML xmlEnd() {
        if (out == null)
            return this;
        xmlClass();
        if (tag != null && stackTags.size() > 0) {
            try {
                out.endElement("", "", tag);
            } catch (SAXException e) {
                xmlException(e);
            }
            if (stackTags != null) {
                stackTags.remove(tag);
                if (stackTags.size() > 0)
                    tag = stackTags.get(stackTags.size() - 1);
            }
        }
        return this;
    }

    /** Prepares an element for output in the error stream. */
    public final WriterXML xmlStartError(String tag) {
        if (err == null)
            return this;
        xmlClassError();
        this.tagErr = tag;
        stackTagsErr.add(tag);
        attributesImplErr = new AttributesImpl();
        return this;
    }

    /** Adds an attribute. */
    public final WriterXML xmlAttributeError(String tag, String value) {
        if (err == null)
            return this;
        if (value != null && attributesImplErr != null && value.length() > 0)
            attributesImplErr.addAttribute("", "", tag, "CDATA", value);
        return this;
    }

    /** Outputs the element. */
    private final WriterXML xmlClassError() {
        if (err == null)
            return this;
        if (tagErr != null && attributesImplErr != null) {
            try {
                err.startElement("", "", tagErr, attributesImplErr);
            } catch (SAXException e) {
                xmlException(e);
            }
        }
        attributesImplErr = null;
        return this;
    }

    /** Adds an element (tag / value) into the current element
     *  as like as an attribute. */
    private final WriterXML xmlOutError(String tag, String value) {
        if (err == null)
            return this;
        xmlClassError();
        if (tag != null && value != null && value.length() > 0) {
            try {
                err.startElement("", "", tag, null);
                err.characters(value.toCharArray(), 0, value.length());
                err.endElement("", "", tag);
            } catch (SAXException e) {
                xmlException(e);
            }
        }
        return this;
    }

    /** Finishes the output of the element. */
    public final WriterXML xmlEndError() {
        if (err == null)
            return this;
        xmlClassError();
        if (tagErr != null && stackTagsErr.size() > 0) {
            try {
                err.endElement("", "", tagErr);
            } catch (SAXException e) {
                xmlException(e);
            }
            if (stackTagsErr != null) {
                stackTagsErr.remove(tagErr);
                if (stackTagsErr.size() > 0)
                    tagErr = stackTagsErr.get(stackTagsErr.size() - 1);
            }
        }
        return this;
    }

    /** Generates an exception into the error stream. */
    public void xmlException(Exception e) {
        if (out == null || err == null) {
            e.printStackTrace();
            return;
        }
        xmlStartError(FATAL);
        xmlAttributeError("exception", e.toString());
        xmlEndError();
    }

    /** Generates a message in the error stream. */
    public void xmlMessage(String level, String message,  
                           String... args) {
        if(level.equals(ERROR) || level.equals(FATAL))
            error = true;
        xmlStartError(level);
        xmlAttributeError("message", message);
        if (args != null) {
            for (int i = 0; i < args.length; i++)
                xmlOutError("arg", args[i]);
        }
        xmlEndError();
    }

    /** Returns a string representation. */
    public String toString() {
        return getOutWriter() != null ? getOutWriter().toString() : "";
    }
    
    /** Indicates if the output contains errors. */
    public boolean isInError() {
        return error;
    }
}
