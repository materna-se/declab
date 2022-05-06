package de.materna.dmn.tester.servlets.portal.dto;

import java.util.UUID;

import de.materna.dmn.tester.enums.VisabilityType;

public class UpdateWorkspaceRequest {
	private UUID workspaceUuid;
	private String name;
	private String description;
	private VisabilityType visability;

	private UUID sessionTokenUuid;

	public UpdateWorkspaceRequest() {
	}

	public UpdateWorkspaceRequest(UUID workspaceUuid, String name, String description, VisabilityType visability,
			UUID sessionTokenUuid) {
		setWorkspaceUuid(workspaceUuid);
		setName(name);
		setDescription(description);
		setVisability(visability);
		setSessionTokenUuid(sessionTokenUuid);
	}

	public UUID getWorkspaceUuid() {
		return workspaceUuid;
	}

	public void setWorkspaceUuid(UUID workspaceUuid) {
		this.workspaceUuid = workspaceUuid;
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
