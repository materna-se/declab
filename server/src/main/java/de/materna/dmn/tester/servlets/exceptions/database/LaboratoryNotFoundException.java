package de.materna.dmn.tester.servlets.exceptions.database;

public class LaboratoryNotFoundException extends DatabaseMissingEntryException {

	private static final long serialVersionUID = 1L;

	public LaboratoryNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}