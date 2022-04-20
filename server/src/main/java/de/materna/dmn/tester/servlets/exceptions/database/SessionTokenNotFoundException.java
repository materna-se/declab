package de.materna.dmn.tester.servlets.exceptions.database;

public class SessionTokenNotFoundException extends DatabaseMissingEntryException {

	private static final long serialVersionUID = 1L;

	public SessionTokenNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}