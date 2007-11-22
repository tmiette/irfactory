package net.sourceforge.projectfactory.server.actors;

import junit.framework.TestCase;
import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.middleware.FactoryConnection;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;

public class TestActor extends TestCase {
	
	private FactoryWriterXML query;
	private FactoryWriterXML answer;
	private FactoryConnection connection;
	
	public void testActor() throws InterruptedException {
		FrameMain main = new FrameMain(connection);
		
		query = new FactoryWriterXML("query:new");
		query.xmlStart("team");
		query.xmlOut("name","la Team");
		query.xmlOut("revision","1");
		query.xmlOut("active","1");
		query.xmlOut("administrator","n");
		
		query.xmlEnd();
		
		answer = new FactoryWriterXML();
		connection.queryLocal(query, answer);
		
		
		//Thread.sleep(20000);
		//System.out.println("la réponse est : "+answer);
	}
	protected void setUp() throws Exception {
		
		this.connection = new FactoryConnection();		
	}
}
