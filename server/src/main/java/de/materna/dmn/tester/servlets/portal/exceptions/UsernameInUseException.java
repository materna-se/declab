package de.materna.dmn.tester.servlets.portal.exceptions;

public class UsernameInUseException extends RegistrationFailureException {

	private static final long serialVersionUID = 1L;

	public UsernameInUseException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}