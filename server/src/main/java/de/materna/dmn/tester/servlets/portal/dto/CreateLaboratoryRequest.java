package de.materna.dmn.tester.servlets.portal.dto;

import de.materna.dmn.tester.enums.VisabilityType;

public class CreateLaboratoryRequest {
	private String tokenString;
	private String name;
	private String description;
	private VisabilityType visability;

	public CreateLaboratoryRequest() {
	}

	public CreateLaboratoryRequest(String name, String description, String tokenString, VisabilityType visability) {
		this.name = name;
		this.description = description;
		this.tokenString = tokenString;
		this.visability = visability;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTokenString() {
		return tokenString;
	}

	public void setTokenString(String tokenString) {
		this.tokenString = tokenString;
	}

	public VisabilityType getVisability() {
		return visability;
	}

	public void setVisability(VisabilityType visability) {
		this.visability = visability;
	}
}
