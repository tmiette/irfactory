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

$Source: /cvsroot/projectfactory/development/net/sourceforge/projectfactory/xml/FactoryHandlerXML.java,v $
$Revision: 1.6 $
$Date: 2006/12/22 08:25:06 $
$Author: ddlamb_2000 $

*/
package fr.umlv.projectOrganizer.xml;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Handler used for XML parsing.
 * The parser triggers some specialized readers that create data and
 * and makes objects to read the coming stream of data.
 * The handler is created automatically by FactoryReaderXML in order
 * to parse an input.
 * The role of this class is also to provide a level of
 * abstraction for Factory.
 * @author David Lambert
 */
class HandlerXML extends DefaultHandler {

    /** Data reader. */
    private ReaderXML reader;

    /** Stack of specialized readers. */
    private List readers = new ArrayList(5);

    /** Copy of the latst tag used during the generation. */
    private String lastTag;

    /** Characters received by the stream. */
    private String characters;

    static int count = 0;

    /** Pushes a reader to the handler in a stack. */
    void newReader(ReaderXML newReader) {
        this.reader = newReader;
        readers.add(reader);
    }

    /** Receives notification of the start of an element. */
    public void startElement(String uri, String sName, String qName, 
                             Attributes attrs) {
        if (reader != null) {
        	reader.addLevel();
            reader.startsTag(qName);
            lastTag = qName;
            characters = "";
            if (attrs != null && attrs.getLength() > 0) {
                for (int i = 0; i < attrs.getLength(); i++)
                    reader.getTag(attrs.getQName(i), attrs.getValue(i));
            }
        }
    }

    /** Receives notification of character data inside an element. */
    public void characters(char[] ch, int start, int length) {
        if (reader != null && ch != null) {
            if (length > 0 && reader != null) {
                characters = 
                        characters + new String(ch, start, ch[start + length - 
                                                           1] == 10 ? 
                                                           length - 1 : 
                                                           length);
            }
        }
    }

    /** Receives notification of the end of an element. */
    public void endElement(String uri, String sName, String qName) {
    	reader.end();
    }

    /** Receives notification of the end of the document. */
    public void endDocument() {
        if (reader != null) {
            reader.endDocument();
        }
    }

    /** Receives notification of a recoverable parser error. */
    public void warning(SAXParseException e) {
        e.printStackTrace();
        reader.xmlException(e);
    }


    /** Receives notification of a recoverable parser error. */
    public void error(SAXParseException e) {
        e.printStackTrace();
        reader.xmlException(e);
    }

    /** Reports a fatal XML parsing error. */
    public void fatalError(SAXParseException e) {
        e.printStackTrace();
        reader.xmlException(e);
    }
}
