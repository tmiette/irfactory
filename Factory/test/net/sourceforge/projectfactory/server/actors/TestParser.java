package net.sourceforge.projectfactory.server.actors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


public class TestParser {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		String answer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><team iid=\"akiri11957570428972\" name=\"Team Test\" summary=\"Team Test@label:inactive\" updated=\"Thursday, November 22, 2007 7:44:02 PM CET\"/></response>";
		System.out.println("XML a parser : ");
		System.out.println(answer);
		XMLParserHandler handler = new XMLParserHandler();
		InputStream in = new ByteArrayInputStream(answer.getBytes());

		handler.parse(in);
		
		System.out.println(JUnitXMLObject.getTeams().get(0));
		
		InputStream in2 = new ByteArrayInputStream(answer.getBytes());

		handler.parse(in2);
		System.out.println(JUnitXMLObject.getTeams().get(1));
	}

}
