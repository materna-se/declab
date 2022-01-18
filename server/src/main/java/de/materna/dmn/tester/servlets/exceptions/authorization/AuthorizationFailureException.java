package de.materna.dmn.tester.servlets.exceptions.authorization;

public class AuthorizationFailureException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthorizationFailureException(String errorMessage) {
		super(errorMessage);
	}
}