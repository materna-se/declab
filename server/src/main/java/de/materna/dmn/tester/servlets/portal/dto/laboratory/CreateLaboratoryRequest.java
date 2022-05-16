package de.materna.dmn.tester.servlets.portal.dto.laboratory;

import de.materna.dmn.tester.enums.VisabilityType;

public class CreateLaboratoryRequest {
	private String name;
	private String description;
	private VisabilityType visability;

	private String sessionTokenUuid;

	public CreateLaboratoryRequest() {
	}

	public CreateLaboratoryRequest(String name, String description, VisabilityType visability,
			String sessionTokenUuid) {
		setName(name);
		setDescription(description);
		setVisability(visability);
		setSessionTokenUuid(sessionTokenUuid);
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

	public VisabilityType getVisability() {
		return visability;
	}

	public void setVisability(VisabilityType visability) {
		this.visability = visability;
	}

	public String getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(String sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
