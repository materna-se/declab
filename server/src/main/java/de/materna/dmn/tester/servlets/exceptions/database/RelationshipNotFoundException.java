package de.materna.dmn.tester.servlets.exceptions.database;

public class RelationshipNotFoundException extends DatabaseMissingEntryException {

	private static final long serialVersionUID = 1L;

	public RelationshipNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}