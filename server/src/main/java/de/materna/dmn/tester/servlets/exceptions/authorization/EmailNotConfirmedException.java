package de.materna.dmn.tester.servlets.exceptions.authorization;

public class EmailNotConfirmedException extends AuthorizationFailureException {

	private static final long serialVersionUID = 1L;

	public EmailNotConfirmedException(String errorMessage) {
		super(errorMessage);
	}
}