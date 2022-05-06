package de.materna.dmn.tester.servlets.portal.dto;

import java.util.UUID;

import de.materna.dmn.tester.enums.VisabilityType;

public class UpdateLaboratoryRequest {
	private UUID laboratoryUuid;
	private String name;
	private String description;
	private VisabilityType visability;

	private UUID sessionTokenUuid;

	public UpdateLaboratoryRequest() {
	}

	public UpdateLaboratoryRequest(UUID laboratoryUuid, String name, String description, VisabilityType visability,
			UUID sessionTokenUuid) {
		setLaboratoryUuid(laboratoryUuid);
		setName(name);
		setDescription(description);
		setVisability(visability);
		setSessionTokenUuid(sessionTokenUuid);
	}

	public UUID getLaboratoryUuid() {
		return laboratoryUuid;
	}

	public void setLaboratoryUuid(UUID laboratoryUuid) {
		this.laboratoryUuid = laboratoryUuid;
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

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
