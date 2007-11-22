package net.sourceforge.projectfactory.server.actors;


import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLCustomHandler extends DefaultHandler {

	public static TestJUnitTeam team;
	
	/**
	 * This method executes differents methods corresponding to the element
	 * encountered by the SAX parser. All elements accepted by the program are
	 * presents, if the SAX parser meet an unknow element, the method ignore it.
	 * During the calls of the differents methods, all objects supported by the
	 * program are created and added in the the scene are created.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attrs) throws SAXException {

		if (localName.equals("team")) {
			if (this.team == null) {
				this.team = new TestJUnitTeam();
				createTeam(attrs);
			}
		}
		
	}

	/**
	 * This method is called when an element ends. At the end of specify element
	 * the object corresponding is added in the scene, and attributes are
	 * reseted.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name)
	throws SAXException {

		
}

	/**
	 * This method permits to print to stdout where is the SAX cursor.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	@Override
	public void setDocumentLocator(Locator locator) {
		super.setDocumentLocator(locator);
	}
	
	
	private void createTeam(Attributes attrs){
		
		this.team.setIid(attrs.getValue("", "iid"));
		this.team.setActive(attrs.getValue("", "active"));
		this.team.setRevision(attrs.getValue("", "revision"));
		this.team.setName(attrs.getValue("", "name"));
		this.team.setCreated(attrs.getValue("", "created"));
		this.team.setCreatedBy(attrs.getValue("", "createdBy"));
		this.team.setTeams(this.team);
	}
}
