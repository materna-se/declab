package de.materna.dmn.tester.servlets.exceptions.registration;

public class UsernameInUseException extends RegistrationFailureException {

	private static final long serialVersionUID = 1L;

	public UsernameInUseException(String errorMessage) {
		super(errorMessage);
	}
}