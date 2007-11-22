package net.sourceforge.projectfactory.server.actors;


public class TestJUnitMember {

	private String actor;
	
	
	public void setActor(String actor) {

		this.actor = actor;
		System.out.println("Création actor "+actor);
	}
	
	
	public String getActor() {

		return actor;
	}
	
	@Override
	public String toString() {
	
		
		return ("acteur = " + this.actor);
	}
}
