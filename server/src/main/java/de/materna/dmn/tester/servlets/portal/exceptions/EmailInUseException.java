package de.materna.dmn.tester.servlets.portal.exceptions;

public class EmailInUseException extends RegistrationFailureException {

	private static final long serialVersionUID = 1L;

	public EmailInUseException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}