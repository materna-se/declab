package de.materna.dmn.tester.servlets.exceptions.database;

public class DatabaseMissingEntryException extends Exception {

	private static final long serialVersionUID = 1L;

	public DatabaseMissingEntryException(String errorMessage) {
		super(errorMessage);
	}
}