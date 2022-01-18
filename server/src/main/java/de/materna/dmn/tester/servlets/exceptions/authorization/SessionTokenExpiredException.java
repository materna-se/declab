package de.materna.dmn.tester.servlets.exceptions.authorization;

public class SessionTokenExpiredException extends AuthorizationFailureException {

	private static final long serialVersionUID = 1L;

	public SessionTokenExpiredException(String errorMessage) {
		super(errorMessage);
	}
}