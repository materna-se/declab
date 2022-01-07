package de.materna.dmn.tester.servlets.portal.exceptions;

public class RegistrationFailureException extends Exception {

	private static final long serialVersionUID = 1L;

	public RegistrationFailureException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}