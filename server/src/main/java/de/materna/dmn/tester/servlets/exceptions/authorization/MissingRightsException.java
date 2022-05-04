package de.materna.dmn.tester.servlets.exceptions.authorization;

public class MissingRightsException extends AuthorizationFailureException {

	private static final long serialVersionUID = 1L;

	public MissingRightsException(String errorMessage) {
		super(errorMessage);
	}
}