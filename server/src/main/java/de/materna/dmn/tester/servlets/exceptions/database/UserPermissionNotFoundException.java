package de.materna.dmn.tester.servlets.exceptions.database;

public class UserPermissionNotFoundException extends DatabaseMissingEntryException {

	private static final long serialVersionUID = 1L;

	public UserPermissionNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}