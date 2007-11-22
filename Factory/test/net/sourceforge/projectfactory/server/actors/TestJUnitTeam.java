package net.sourceforge.projectfactory.server.actors;

import java.util.ArrayList;


public class TestJUnitTeam {

	private ArrayList<TestJUnitTeam> teams;
	
	private String iid;
	private String active;
	private String revision;
	private String name;
	private String created;
	private String createdBy;
	
	
	public TestJUnitTeam() {

		this.teams = new ArrayList<TestJUnitTeam>();
	}


	
	public ArrayList<TestJUnitTeam> getTeams() {
	
		return teams;
	}


	
	public void setTeams(TestJUnitTeam team) {
	
		this.teams.add(team);
	}


	
	public String getIid() {
	
		return iid;
	}


	
	public void setIid(String iid) {
	
		this.iid = iid;
	}


	
	public String getActive() {
	
		return active;
	}


	
	public void setActive(String active) {
	
		this.active = active;
	}


	
	public String getRevision() {
	
		return revision;
	}


	
	public void setRevision(String revision) {
	
		this.revision = revision;
	}


	
	public String getName() {
	
		return name;
	}


	
	public void setName(String name) {
	
		this.name = name;
	}


	
	public String getCreated() {
	
		return created;
	}


	
	public void setCreated(String created) {
	
		this.created = created;
	}


	
	public String getCreatedBy() {
	
		return createdBy;
	}


	
	public void setCreatedBy(String createdBy) {
	
		this.createdBy = createdBy;
	}
	
	@Override
	public String toString() {
	
		StringBuilder sb = new StringBuilder();
		for (TestJUnitTeam t : this.teams) {
			sb.append("iid = " + t.getIid());
			sb.append(" active = " + t.getActive());
			sb.append(" revision = " + t.revision);
			sb.append(" name = " + t.getName());
			sb.append(" created = " + t.getCreated());
			sb.append(" created by = " + t.getCreatedBy());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
