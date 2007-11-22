package net.sourceforge.projectfactory.server.actors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;


public class TestParser {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		ArrayList<TestJUnitTeam> teams;
		
		String answer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><team iid=\"akiri11957570428972\" name=\"Team Test\" summary=\"Team Test@label:inactive\" updated=\"Thursday, November 22, 2007 7:44:02 PM CET\"/></response>";
		System.out.println("XML a parser : ");
		System.out.println(answer);
		XMLParserHandler handler = new XMLParserHandler();
		InputStream in = new StringBufferInputStream(answer.toString());
		handler.parse(in);
		teams = XMLCustomHandler.teams;
		
		System.out.println(teams.get(0));
		
		InputStream in2 = new StringBufferInputStream(answer.toString());
		handler.parse(in2);
		System.out.println(teams.get(1));
	}

}
