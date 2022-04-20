package de.materna.dmn.tester.servlets.exceptions.database;

public class UserNotFoundException extends DatabaseMissingEntryException {

	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}