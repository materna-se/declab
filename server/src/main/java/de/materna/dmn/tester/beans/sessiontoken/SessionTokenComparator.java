package de.materna.dmn.tester.beans.sessiontoken;

import java.util.Comparator;

public class SessionTokenComparator implements Comparator<SessionToken> {

	@Override
	public int compare(SessionToken a, SessionToken b) {
		return a.getInitiation().compareTo(b.getInitiation());
	}

}