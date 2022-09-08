package de.materna.dmn.tester.servlets.exceptions.database;

public class PermissionNotFoundException extends DatabaseMissingEntryException {

	private static final long serialVersionUID = 1L;

	public PermissionNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}