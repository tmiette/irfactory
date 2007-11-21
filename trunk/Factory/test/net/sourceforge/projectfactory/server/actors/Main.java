package net.sourceforge.projectfactory.server.actors;

import net.sourceforge.projectfactory.client.FrameMain;
import net.sourceforge.projectfactory.middleware.FactoryConnection;
import net.sourceforge.projectfactory.xml.FactoryWriterXML;

public class Main {
	
	static private FactoryWriterXML query;
	static private FactoryWriterXML answer;
	static private FactoryConnection connection;
	
	public static void main(String[] args) {
		connection = new FactoryConnection();
		// this query creates a record
		query = new FactoryWriterXML("query:new");
		query.xmlStart("actor");
		query.xmlOut("iid","doak11955005096444");
		query.xmlOut("name","Toto");
		query.xmlOut("revision","1");
		query.xmlOut("active","y");
		query.xmlOut("administrator","n");
		query.xmlEnd();
		answer = new FactoryWriterXML();
		connection.queryLocal(query, answer);
		System.out.println(query);
		
		System.out.println(answer);
	}
	
	
}
