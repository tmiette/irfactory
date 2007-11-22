package net.sourceforge.projectfactory.server.actors;

import java.util.ArrayList;

public class JUnitXMLObject {

	private static ArrayList<TestJUnitTeam> teams = new ArrayList<TestJUnitTeam>();

	public static ArrayList<TestJUnitTeam> getTeams() {

		return teams;
	}
	
	@Override
	public String toString() {
	
		StringBuilder sb = new StringBuilder();
		sb.append("Ensemble des team :\n");
		for (TestJUnitTeam t : teams) {
			sb.append(t.toString());
		}
		return sb.toString();
	}
}
