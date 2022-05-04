package de.materna.dmn.tester.servlets.portal.dto;

import java.util.UUID;

public class DeleteWorkspaceRequest {
	private UUID sessionTokenUuid;
	private UUID workspaceUuid;

	public DeleteWorkspaceRequest() {
	}

	public DeleteWorkspaceRequest(UUID sessionTokenUuid, UUID workplaceUuid) {
		this.setSessionTokenUuid(sessionTokenUuid);
		this.setWorkspaceUuid(workplaceUuid);
	}

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}

	public UUID getWorkspaceUuid() {
		return workspaceUuid;
	}

	public void setWorkspaceUuid(UUID workplaceUuid) {
		this.workspaceUuid = workplaceUuid;
	}
}
