package net.sourceforge.projectfactory.server.actors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;
import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.middleware.FactoryConnection;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;

public class TestTeam extends TestCase {
	
	private FactoryWriterXML query;
	private FactoryWriterXML answer;
	private FactoryConnection connection;
	private XMLParserHandler handler = new XMLParserHandler();
	
	public void testCreateTeam() throws IOException {
		FrameMain main = new FrameMain(connection);
		
		query = new FactoryWriterXML("query:new");
		query.xmlStart("team");
		query.xmlOut("name","Team Test");
		query.xmlOut("revision","1");
		query.xmlOut("active","y");
		query.xmlEnd();
		
		answer = new FactoryWriterXML();
		connection.queryLocal(query, answer);

		handler.parse(new ByteArrayInputStream(answer.toString().getBytes()));
		
		ArrayList<TestJUnitTeam> teams =  JUnitXMLObject.getTeams();
		
		if(teams.size() == 0) fail("Team was not created");
		else{
			TestJUnitTeam team = teams.get(0);
			assertEquals(team.getName(), "Team Test");
		}
	}
	
	public void testAddActor() throws IOException {
		
		query = new FactoryWriterXML("query:update");
		
		query.xmlStart("team");
		query.xmlOut("name","Team Test");
		query.xmlOut("revision","1");
		query.xmlOut("active","y");
		query.xmlStart("member").xmlAttribute("actor", "Tom").xmlAttribute("interim", "y").xmlEnd();
		query.xmlStart("member").xmlAttribute("actor", "Alan").xmlAttribute("interim", "y").xmlEnd();
		query.xmlEnd();
		answer = new FactoryWriterXML();
		connection.queryLocal(query, answer);
		answer = getTeam();
		handler.parse(new ByteArrayInputStream(answer.toString().getBytes()));
		
		//System.out.println("REPONSE : "+answer);
		
		ArrayList<TestJUnitTeam> teams =  JUnitXMLObject.getTeams();
		
		TestJUnitTeam team = teams.get(1);
		
		assertNotNull(team.getMember("Tom"));
		
		assertNotNull(team.getMember("Alan"));
		
	}
	
	public void testDeleteActor() throws IOException {
		
		query = new FactoryWriterXML("query:update");
		
		query.xmlStart("team");
		query.xmlOut("name","Team Test");
		query.xmlOut("revision","2");
		query.xmlOut("active","y");
		query.xmlStart("member").xmlAttribute("actor", "Tom").xmlAttribute("interim", "y").xmlEnd();
		query.xmlEnd();
		
		answer = new FactoryWriterXML();
		connection.queryLocal(query, answer);
		
		
		handler.parse(new ByteArrayInputStream(getTeam().toString().getBytes()));
		
		ArrayList<TestJUnitTeam> teams =  JUnitXMLObject.getTeams();
		
		TestJUnitTeam team = teams.get(2);
		assertNotNull(team.getMember("Tom"));
		assertNull(team.getMember("Alan"));
		
	}
	
	private FactoryWriterXML getTeam() {
		
		query = new FactoryWriterXML("query:get");
		
		query.xmlStart("team").xmlAttribute("name", "Team Test").xmlEnd();
		
		answer = new FactoryWriterXML();
		connection.queryLocal(query, answer);
		return answer;
	}
	
	protected void setUp() throws Exception {
		
		this.connection = new FactoryConnection();		
	}
}
