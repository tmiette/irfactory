package net.sourceforge.projectfactory.server.actors;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLCustomHandler extends DefaultHandler {

	private TestJUnitTeam team;

	private TestJUnitMember member;

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
			System.out.println("detection team");
			if (team == null) {
				System.out.println("creation d'une team");
				TestJUnitTeam team = createTeam(attrs);
				JUnitXMLObject.getTeams().add(team);
			}
		}
		if (localName.equals("member")) {
			System.out.println("detection member");
			if (member == null) {
				System.out.println("creation d'un membre");
				TestJUnitMember member = createMember(attrs);
				this.team.addMember(member);
			}
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
			System.out.println("fin detection team");
		}
		
		if (localName.equals("member")) {
			System.out.println("fin detection member");
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

	private TestJUnitTeam createTeam(Attributes attrs) {

		TestJUnitTeam team = new TestJUnitTeam();
		team.setIid(attrs.getValue("", "iid"));
		team.setName(attrs.getValue("", "name"));
		team.setUpdated(attrs.getValue("", "updated"));
		team.setSummary(attrs.getValue("", "summary"));
		return team;
	}

	private TestJUnitMember createMember(Attributes attrs) {

		TestJUnitMember member = new TestJUnitMember();
		member.setActor(attrs.getValue("", "actor"));
		return member;
	}
}
