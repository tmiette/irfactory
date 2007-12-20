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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/xml/FactoryReaderXML.java,v $
$Revision: 1.6 $
$Date: 2007/02/23 16:27:44 $
$Author: ddlamb_2000 $

*/
package fr.umlv.projectOrganizer.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
  * FactoryReaderXML is the abstract class used in order to define
  * specialized readers that are called by the XML handler during parsing.
  * The goal is to provide a way to read data "on the fly" (SAX).
  * Because it's an abstract class, you have to derive it in order to manage
  * your own data. 3 methods needs to be derived: startsTag(), getTag() and end().
  * The role of this class is also to provide a level of
  * abstraction for Factory.
  * @author David Lambert
  */
public abstract class ReaderXML {

    /** Level of elements during parsing. */
    private int level;

    /** Handler that runs the reader. */
    private HandlerXML handler;

    /** Output streams. */
    protected WriterXML out;

    /** Runs the parser, based on an handler. Used in order to continue
     * the streaming from another reader. */
    private final void xmlIn(HandlerXML handler, WriterXML out) {
        this.handler = handler;
        this.out = out;
        handler.newReader(this);
    }

    /** Runs the parser, based on another handler. Used in order to continue
     * the streaming from another reader. */
    public final void xmlIn(ReaderXML reader) {
        xmlIn(reader.handler, reader.out);
    }

    /** Runs the parser, based on an input source. */
    private final void xmlIn(InputSource is, WriterXML out, 
                            boolean validating) throws SAXException {
        this.handler = new HandlerXML();
        xmlIn(handler, out);
        SAXParserFactory xmlInput = SAXParserFactory.newInstance();
        xmlInput.setValidating(validating);
        try {
            xmlInput.newSAXParser().parse(is, handler);
        } catch (ParserConfigurationException e) {
            xmlException(e);
        } catch (IOException e) {
            xmlException(e);
        }
    }

    /** Runs the parser, based on an URI. */
    public void xmlIn(String uri, WriterXML out, boolean validating) {
        try {
            xmlIn(new InputSource(uri), out, validating);
        } catch (SAXException e) {
            System.err.println(uri.toString());
            xmlException(e);
        }
    }

    /** Runs the parser, based on a query or string. */
    public void xmlIn(Writer query, WriterXML out, boolean validating) {
        if (query != null && query.toString().length() > 0) {
            try {
            xmlIn(new InputSource(
                        new StringReader(query.toString())), 
                        out, 
                        validating);
            } catch (SAXException e) {
                System.err.println(query.toString());
                xmlException(e);
            }
        }
    }

    /** Runs the parser, based on a query stored in FactoryWriterXML. */
    public void xmlIn(WriterXML query, WriterXML out, 
                      boolean validating) {
        if (query != null) {
            query.end();
            xmlIn(query.getOutWriter(), out, validating);
        }
    }

    /** Runs the parser, based on a buffered reader. */
    public void xmlIn(BufferedReader uri, WriterXML out, boolean validating) {
        try {
            xmlIn(new InputSource(uri), out, validating);
        } catch (SAXException e) {
            System.err.println(uri.toString());
            xmlException(e);
        }
    }

    /** Runs the parser, based on an error stream stored in FactoryWriterXML. */
    public void xmlErrorIn(WriterXML query) {
        if (query != null) {
            query.end();
            xmlIn(query.getErrWriter(), null, false);
        }
    }

    /** Manages an exception redirected to the error output stream. */
    protected void xmlException(Exception e) {
        if (out == null) {
            e.printStackTrace();
            return;
        }
        out.xmlMessage(WriterXML.FATAL, e.toString());
    }

    /** Interprets a that starts an element. Should be
      * overriden by any derived class thats need to interpret a tag with
      * associated value on the fly. */
    protected void startsTag(String tag) {
    }

    /** Interprets a tag (defined in the class attributes 'tag'), with the
      * associated text, provided as an argument of the method. Should be
      * overriden by any derived class thats need to interpret a tag with
      * associated value on the fly. */
    protected void getTag(String tag, String value) {
        if (out != null) {
            out.xmlMessage(WriterXML.WARNING, "message:missing:tag", "", 
                           tag, value, getClass().toString());
        }
    }

    /** Ends the interpretation of a tag (defined in the class attributes 'tag').
      * Should be overriden by any derived class that needs to interpret a tag on
      * the fly. */
    protected void end() {
    }

    /** Returns the handler that runs the reader. */
    protected HandlerXML getHandler() {
        return handler;
    }

    /** Returns the level. */
    public int getLevel() {
        return level;
}

    /** Increases the level. */
    public void addLevel() {
        ++level;
    }

    /** Decreases the level. */
    public void decLevel() {
        --level;
    }

    /** Sets the level to zero. */
    public void resetLevel() {
        level = 0;
    }
}
