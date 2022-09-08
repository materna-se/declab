package de.materna.dmn.tester.beans.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.materna.dmn.tester.enums.RestErrorCode;

public class RestError {
	private Status status;
	private String message;
	private String details;
	private String subject;

	public RestError(RestErrorCode code) {
		this(code, null);
	}

	public RestError(RestErrorCode code, String subject) {
		setStatus(status);
		setSubject(subject != null ? subject : "");
		switch (code) {
		case BAD_JWT:
			setStatus(Response.Status.UNAUTHORIZED);
			setMessage("JWT refused");
			setDetails("There was a problem with the given JWT. You have to login by username and password.");
			break;
		case EMAIL_TAKEN:
			setStatus(Response.Status.CONFLICT);
			setMessage("Email address already in use");
			setDetails("The chosen email address is already in use. Please choose an alternative.");
			break;
		case MISSING_RIGHTS:
			setStatus(Response.Status.FORBIDDEN);
			setMessage("Missing right");
			setDetails("You do not have the necessary rights. Your request has been denied.");
			break;
		case NOT_CREATED:
			setStatus(Response.Status.NOT_MODIFIED);
			setMessage("Not created");
			setDetails("Your request has been denied. Your new entry has not been created.");
			break;
		case NOT_FOUND:
			setStatus(Response.Status.NO_CONTENT);
			setMessage("Requested entry not found");
			setDetails("Your requested entry could not be found in the database.");
			break;
		case NOT_LOGGED_IN:
			setStatus(Response.Status.UNAUTHORIZED);
			setMessage("Not logged in");
			setDetails("You have to log in first.");
			break;
		case NOT_MODIFIED:
			setStatus(Response.Status.NOT_MODIFIED);
			setMessage("Not modified");
			setDetails("Your changes to the database could not be executed.");
			break;
		case PASSWORD_WRONG:
		case USERNAME_NOT_FOUND:
			setStatus(Response.Status.UNAUTHORIZED);
			setMessage("Incorrect username and password");
			setDetails("Ensure that the username and password included in the request are correct.");
			break;
		case USERNAME_TAKEN:
			setStatus(Response.Status.CONFLICT);
			setMessage("Username already in use");
			setDetails("The chosen username is already in use. Please choose an alternative.");
			break;
		default:
			setStatus(Response.Status.BAD_REQUEST);
			setMessage("An unspecified error has occured. Your request has been denied.");
			setDetails("No detailed information available.");
			break;
		}
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}