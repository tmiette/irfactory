package net.sourceforge.projectfactory.server.actors;

import junit.framework.TestCase;
import net.sourceforge.projectfactory.middleware.FactoryConnection;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;

public class TestActor extends TestCase {
	
	private FactoryWriterXML query;
	private FactoryWriterXML answer;
	private FactoryConnection connection;
	
	public TestActor(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		
		this.connection = new FactoryConnection();
		
		// this query creates a record
		query = new FactoryWriterXML("query:new");
		query.xmlStart("actor");
		
		query.xmlOut("name","Doak");
		query.xmlOut("revision","1");
		query.xmlOut("active","1");
		query.xmlOut("administrator","n");
		
		query.xmlEnd();
		
		answer = new FactoryWriterXML();
		connection.queryLocal(query, answer);
		
	}
}
