package de.materna.dmn.tester.servlets.exceptions.registration;

public class EmailInUseException extends RegistrationFailureException {

	private static final long serialVersionUID = 1L;

	public EmailInUseException(String errorMessage) {
		super(errorMessage);
	}
}