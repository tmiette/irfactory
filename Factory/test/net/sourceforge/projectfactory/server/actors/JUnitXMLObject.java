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
}
