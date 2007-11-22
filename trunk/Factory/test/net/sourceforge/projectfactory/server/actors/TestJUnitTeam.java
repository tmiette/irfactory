package net.sourceforge.projectfactory.server.actors;

import java.util.ArrayList;

public class TestJUnitTeam {

	private String iid;

	private String name;

	private String updated;

	private String summary;

	public TestJUnitTeam() {

	}

	
	public String getIid() {
	
		return iid;
	}

	
	public void setIid(String iid) {
	
		this.iid = iid;
	}

	
	public String getName() {
	
		return name;
	}

	
	public void setName(String name) {
	
		this.name = name;
	}

	
	public String getUpdated() {
	
		return updated;
	}

	
	public void setUpdated(String updated) {
	
		this.updated = updated;
	}

	
	public String getSummary() {
	
		return summary;
	}

	
	public void setSummary(String summary) {
	
		this.summary = summary;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
			sb.append("iid = " + getIid());
			sb.append(" name = " + getName());
			sb.append(" summary = " + getSummary());
			sb.append(" updated = " + getUpdated());
			sb.append("\n");

		return sb.toString();
	}
}