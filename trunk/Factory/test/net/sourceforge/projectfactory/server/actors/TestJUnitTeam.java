package net.sourceforge.projectfactory.server.actors;

import java.util.ArrayList;

public class TestJUnitTeam {

	// members list
	private ArrayList<TestJUnitMember> members = new ArrayList<TestJUnitMember>();

	// members fields
	private String iid;

	private String name;

	private String updated;

	private String summary;

	/**
	 * Adds a member to the team
	 * @param m member
	 */
	public void addMember(TestJUnitMember m) {

		System.out.println("AJOUT :  " + m.getActor());
		this.members.add(m);
	}

	/**
	 * Gets a specific member corresponding to name
	 * @param name of the member
	 * @return member matching name member
	 */
	public TestJUnitMember getMember(String name) {

		System.out.println("ARRAY : " + this.members + "  " + name);
		for (TestJUnitMember m : members) {

			if (m.getActor().equals(name)) {

				return m;
			}
		}
		return null;
	}

	public ArrayList<TestJUnitMember> getArrayMember() {

		return members;
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
		sb.append("\nAffichage des membres : \n");
		for (TestJUnitMember m : this.members) {
			sb.append(m.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
