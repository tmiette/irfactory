package net.sourceforge.projectfactory.server.actors;

import java.util.ArrayList;

public class JUnitXMLObject {

	private static ArrayList<TestJUnitTeam> teams = new ArrayList<TestJUnitTeam>();

	private static ArrayList<TestJUnitMember> members = new ArrayList<TestJUnitMember>();

	
	public static ArrayList<TestJUnitMember> getMembers() {

		return members;
	}
	
	
	public static ArrayList<TestJUnitTeam> getTeams() {

		return teams;
	}
	
	@Override
	public String toString() {
	
		StringBuilder sb = new StringBuilder();
		sb.append("Ensemble des membres :\n");
		for (TestJUnitMember m : members) {
			sb.append(m.toString());
		}
		sb.append("Ensemble des team :\n");
		for (TestJUnitTeam t : teams) {
			sb.append(t.toString());
		}
		return sb.toString();
	}
}
