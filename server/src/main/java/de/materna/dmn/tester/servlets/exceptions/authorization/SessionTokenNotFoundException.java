package de.materna.dmn.tester.servlets.exceptions.authorization;

public class SessionTokenNotFoundException extends AuthorizationFailureException {

	private static final long serialVersionUID = 1L;

	public SessionTokenNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}