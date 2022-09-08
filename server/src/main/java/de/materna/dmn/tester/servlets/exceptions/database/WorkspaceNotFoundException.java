package de.materna.dmn.tester.servlets.exceptions.database;

public class WorkspaceNotFoundException extends DatabaseMissingEntryException {

	private static final long serialVersionUID = 1L;

	public WorkspaceNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}