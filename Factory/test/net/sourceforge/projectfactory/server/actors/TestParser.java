package net.sourceforge.projectfactory.server.actors;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TestParser {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String answer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><team iid=\"doak11957722570312\" active=\"y\" revision=\"2\" name=\"Team Test\" created=\"Thursday, November 22, 2007 11:57:37 PM CET\" updated=\"Thursday, November 22, 2007 11:57:37 PM CET\" createdby=\"doak\" updatedby=\"doak\"><member actor=\"Tom\" interim=\"y\"/><member actor=\"Alan\" interim=\"y\"/></team></response>";
		System.out.println("XML a parser : ");
		System.out.println(answer);
		XMLParserHandler handler = new XMLParserHandler();
		InputStream in = new ByteArrayInputStream(answer.getBytes());

		handler.parse(in);
		
		for (TestJUnitTeam t : JUnitXMLObject.getTeams()) {
			System.out.println(t.getArrayMember());
		}
		
	}
}
