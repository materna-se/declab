package de.materna.dmn.tester.servlets.portal.dto;

import java.util.UUID;

import de.materna.dmn.tester.enums.VisabilityType;

public class CreateLaboratoryRequest {
	private UUID sessionTokenUuid;
	private String name;
	private String description;
	private VisabilityType visability;

	public CreateLaboratoryRequest() {
	}

	public CreateLaboratoryRequest(String name, String description, UUID sessionTokenUuid, VisabilityType visability) {
		setName(name);
		setDescription(description);
		setSessionTokenUuid(sessionTokenUuid);
		setVisability(visability);
	}

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
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
}
