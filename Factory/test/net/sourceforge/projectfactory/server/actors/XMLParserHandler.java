package net.sourceforge.projectfactory.server.actors;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;



public class XMLParserHandler {

	public void parse(String stream) throws IOException {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);

		try {
			SAXParser parser = factory.newSAXParser();
			XMLCustomHandler xch = new XMLCustomHandler();
			parser.parse(stream, xch);

		} catch (ParserConfigurationException e) {
			throw new IOException();
		} catch (SAXException e) {
			Throwable cause = e.getCause();
			if (cause instanceof IOException)
				throw (IOException) cause;
			throw new IOException();
		}
	}
}
