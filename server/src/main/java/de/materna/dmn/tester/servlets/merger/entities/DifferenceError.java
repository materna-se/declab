package de.materna.dmn.tester.servlets.merger.entities;

import de.materna.dmn.tester.enums.DifferenceErrorSeverity;

public class DifferenceError {

	DifferenceErrorSeverity severity;
	String message;

	public DifferenceError(DifferenceErrorSeverity severity, String message) {
		this.severity = severity;
		this.message = message;
	}

	public DifferenceErrorSeverity getSeverity() {
		return severity;
	}

	public String getMessage() {
		return message;
	}
}
