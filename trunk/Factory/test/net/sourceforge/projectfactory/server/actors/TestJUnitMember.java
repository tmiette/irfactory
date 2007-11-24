package net.sourceforge.projectfactory.server.actors;


public class TestJUnitMember {

	//member name
	private String actor;
	
	
	/**
	 * Assigns a member name
	 * @param actor member name
	 */
	public void setActor(String actor) {

		this.actor = actor;
	}
	
	/**
	 * Getter of a member name
	 * @return member name
	 */
	public String getActor() {

		return actor;
	}
	
	@Override
	public String toString() {
	
		
		return ("acteur = " + this.actor);
	}
}
