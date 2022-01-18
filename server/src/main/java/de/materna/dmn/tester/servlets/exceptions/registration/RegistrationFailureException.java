package de.materna.dmn.tester.servlets.exceptions.registration;

public class RegistrationFailureException extends Exception {

	private static final long serialVersionUID = 1L;

	public RegistrationFailureException(String errorMessage) {
		super(errorMessage);
	}
}