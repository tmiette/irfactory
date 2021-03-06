package net.sourceforge.projectfactory.server.actors;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLCustomHandler extends DefaultHandler {

	//the new team corresponding to xml flux
	private TestJUnitTeam team;

	//the new member corrsponding to the xml flux
	private TestJUnitMember member;

	/**
	 * This method executes different methods corresponding to the element
	 * encountered by the SAX parser. All elements accepted by the program are
	 * presents, if the SAX parser meet an unknown element, the method ignore it.
	 * During the calls of the different methods, all objects supported by the
	 * program are created and added in the the scene are created.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attrs) throws SAXException {

		if (localName.equals("team")) {
			//creating of the new team
			this.team = createTeam(attrs);
		}
		if (localName.equals("member")) {
			//creating of the new member
			this.member = createMember(attrs);
		}

	}

	/**
	 * This method is called when an element ends. At the end of specify element
	 * the object corresponding is added in the scene, and attributes are reseted.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {

		if (localName.equals("team")) {
			//adding the new team to an ArrayList in JUnitXMLObject object
			JUnitXMLObject.getTeams().add(this.team);
			this.team = null;
		}

		if (localName.equals("member")) {
			//adding a member to the team
			this.team.addMember(member);
			this.member = null;
		}

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

	/**
	 * This method permits to create a new team
	 * 
	 * @param attrs
	 *          attributes corresponding to the team's fields
	 * @return TestJUnitTeam the new team created
	 */
	private TestJUnitTeam createTeam(Attributes attrs) {

		TestJUnitTeam team = new TestJUnitTeam();
		//getting the values of the team's fields
		team.setIid(attrs.getValue("", "iid"));
		team.setName(attrs.getValue("", "name"));
		team.setUpdated(attrs.getValue("", "updated"));
		team.setSummary(attrs.getValue("", "summary"));
		return team;
	}

	/**
	 * This method permits to create a new member
	 * 
	 * @param attrs
	 *          attributes corresponding to the member's fields
	 * @return TestJUnitMember the new member created
	 */
	private TestJUnitMember createMember(Attributes attrs) {

		TestJUnitMember member = new TestJUnitMember();
		//getting the values of the member's fields
		member.setActor(attrs.getValue("", "actor"));
		return member;
	}
}
