package net.sourceforge.projectfactory.server.actors;

import junit.framework.TestCase;
import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.middleware.FactoryConnection;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;

public class TestTeam extends TestCase {
	
	private FactoryWriterXML query;
	private FactoryWriterXML answer;
	private FactoryConnection connection;
	
	public void testCreateTeam() {
		FrameMain main = new FrameMain(connection);
		
		query = new FactoryWriterXML("query:new");
		query.xmlStart("team");
		query.xmlOut("name","Team Test");
		query.xmlOut("revision","1");
		query.xmlOut("active","1");
		query.xmlOut("administrator","n");
		query.xmlEnd();
		
		answer = new FactoryWriterXML();
		connection.queryLocal(query, answer);
		/*send the response to the parser*/
		/*test the data structure of team with assertions*/
	}
	
	public void testAddActor() {
		
		query = new FactoryWriterXML("query:update");
		
		query.xmlStart("team");
		query.xmlOut("name","Team Test");
		query.xmlOut("revision","1");
		query.xmlOut("active","1");
		query.xmlOut("administrator","n");
		query.xmlOut("member").xmlAttribute("name", "Tom");
		query.xmlOut("member").xmlAttribute("name", "Doak");
		query.xmlEnd();
		
		answer = new FactoryWriterXML();
		connection.queryLocal(query, answer);
		/*send the response to the parser*/
		/*test the data structure of team with assertions*/
	}
	
	private void getTeam() {
		
		query = new FactoryWriterXML("query:get");
		
		query.xmlStart("team").xmlAttribute("name", "Team Test").xmlEnd();
		
		answer = new FactoryWriterXML();
		connection.queryLocal(query, answer);
		/*send the response to the parser*/
	}
	
	protected void setUp() throws Exception {
		
		this.connection = new FactoryConnection();		
	}
}
