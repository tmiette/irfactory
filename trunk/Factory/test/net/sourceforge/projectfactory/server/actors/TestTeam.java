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
	private XMLParserHandler handler;
	private String firstActor = "Actor Test 1";
	private String secondActor = "Actor Test 2";
	private String teamTest = "Team Test";
	
	/**
	 * Initializes the connection which is used to communicate with the server
	 */
	@Override protected void setUp() throws Exception {
		this.connection = new FactoryConnection();
		new FrameMain(this.connection); // FrameMain is used to initialize the connection
		
		handler = new XMLParserHandler();
	}
	
	/**
	 * This method tests a creation of a team
	 */
	public void testCreateTeam() throws IOException {
		
		//parameterizes the query
		query = new FactoryWriterXML("query:new");
		query.xmlStart("team");
		query.xmlOut("name", teamTest);
		query.xmlOut("revision","1");
		query.xmlOut("active","y");
		query.xmlEnd();
		
		
		//sends the query
		connection.queryLocal(query, new FactoryWriterXML());
		
		//interprets a response to a get query on the team which has been created @see also getTeam()
		handler.parse(new ByteArrayInputStream(getTeam("Team Test").toString().getBytes()));
		
		//Validation of the test
		ArrayList<TestJUnitTeam> teams =  JUnitXMLObject.getTeams();
		
		if(teams.size() == 0) fail("Team was not created");
		else{
			TestJUnitTeam team = teams.get(0);
			assertEquals(team.getName(), teamTest);
		}
	}
	
	/**
	 * This method tests adding an actor in a team
	 */
	public void testAddActor() throws IOException {
		
		//creates two actors
		this.createActor(firstActor);
		this.createActor(secondActor);
		
		//parameterizes the query
		query = new FactoryWriterXML("query:update");
		query.xmlStart("team");
		query.xmlOut("name",teamTest);
		query.xmlOut("revision","1");
		query.xmlOut("active","y");
		query.xmlStart("member").xmlAttribute("actor", firstActor).xmlAttribute("interim", "y").xmlEnd();
		query.xmlStart("member").xmlAttribute("actor", secondActor).xmlAttribute("interim", "y").xmlEnd();
		query.xmlEnd();
		
		//sends the query
		connection.queryLocal(query, new FactoryWriterXML());
		
		//interprets a response to a get query on the team which has been created @see also getTeam()
		handler.parse(new ByteArrayInputStream(getTeam(teamTest).toString().getBytes()));
		
		//Validation of the test
		ArrayList<TestJUnitTeam> teams =  JUnitXMLObject.getTeams();
		
		TestJUnitTeam team = teams.get(1);
		assertNotNull(team.getMember(firstActor));
		assertNotNull(team.getMember(secondActor));
		
	}
	
	/**	
	 * This method tests for the removal of an actor from his team
	 */
	public void testDeleteActor() throws IOException {
		
		//parameterizes the query
		query = new FactoryWriterXML("query:update");
		query.xmlStart("team");
		query.xmlOut("name", teamTest);
		query.xmlOut("revision","2");
		query.xmlOut("active","y");
		query.xmlStart("member").xmlAttribute("actor", firstActor).xmlAttribute("interim", "y").xmlEnd();
		query.xmlEnd();
		
		//sends the query
		connection.queryLocal(query, new FactoryWriterXML());
		
		//interprets a response to a get query on the team which has been created @see also getTeam()
		handler.parse(new ByteArrayInputStream(getTeam(teamTest).toString().getBytes()));
		
		//Validation of the test
		ArrayList<TestJUnitTeam> teams =  JUnitXMLObject.getTeams();
		
		TestJUnitTeam team = teams.get(2);
		assertNotNull(team.getMember(firstActor));
		assertNull(team.getMember(secondActor)); //secondActor must be deleted
		
		this.clean();
	}
	
	/**
	 * A personalize tear down method 
	 * It's called after all tests, this method delete the Test Team and its members
	 */
	private void clean(){
		deleteTeam(teamTest);
		deleteActor(firstActor);
		deleteActor(secondActor);
	}
	
	/**Gets the team of the given name and its members
	 * 
	 * @return the team in FactoryWriterXml format
	 */
	private FactoryWriterXML getTeam(String name) {
		
		//parameterizes the query
		query = new FactoryWriterXML("query:get");
		query.xmlStart("team").xmlAttribute("name", name).xmlEnd();
		
		answer = new FactoryWriterXML();
		
		//sends the query and get the answer
		connection.queryLocal(query, answer);
		
		return answer;
	}
	
	/**Creates an actor with the given name
	 * 
	 * @param name of the actor
	 */
	private void createActor(String name){
		
		//parameterizes the query
		query = new FactoryWriterXML("query:new");
		query.xmlStart("actor");
		query.xmlOut("name", name);
		query.xmlOut("revision","1");
		query.xmlOut("active","y");
		query.xmlEnd();
		
		
		//sends the query
		connection.queryLocal(query, new FactoryWriterXML());
	}
	
	/**Deletes actor of the given name
	 * 
	 * @param name of the actor
	 */
	private void deleteActor(String name){
		
		//parameterizes the query
		query = new FactoryWriterXML("query:delete");
		query.xmlStart("actor");
		query.xmlOut("name", name);
		query.xmlOut("revision","1");
		query.xmlOut("active","y");
		query.xmlEnd();
		
		
		//sends the query
		connection.queryLocal(query, new FactoryWriterXML());
	}
	
	/**Deletes team with the given name
	 * 
	 * @param name of the team
	 */
	private void deleteTeam(String name){
		
		//parameterizes the query
		query = new FactoryWriterXML("query:delete");
		query.xmlStart("team");
		query.xmlOut("name", name);
		query.xmlOut("revision","3");
		query.xmlOut("active","y");
		query.xmlEnd();
		
		
		//sends the query
		connection.queryLocal(query, new FactoryWriterXML());
	}
}
