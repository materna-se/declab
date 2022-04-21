package de.materna.dmn.tester.servlets.portal.dto;

import java.util.UUID;

import de.materna.dmn.tester.enums.VisabilityType;

public class CreateWorkspaceRequest {
	private UUID sessionTokenUuid;
	private String name;
	private String description;
	private VisabilityType visability;

	public CreateWorkspaceRequest() {
	}

	public CreateWorkspaceRequest(String name, String description, UUID sessionTokenUuid, VisabilityType visability) {
		this.name = name;
		this.description = description;
		this.sessionTokenUuid = sessionTokenUuid;
		this.visability = visability;
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
