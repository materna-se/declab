package de.materna.dmn.tester.servlets.portal.dto.workspace;

import java.util.UUID;

public class ReadWorkspaceRequest {
	private UUID workspaceUuid;

	private UUID sessionTokenUuid;

	public ReadWorkspaceRequest() {
	}

	public ReadWorkspaceRequest(UUID workplaceUuid, UUID sessionTokenUuid) {
		this.setWorkspaceUuid(workplaceUuid);
		this.setSessionTokenUuid(sessionTokenUuid);
	}

	public UUID getWorkspaceUuid() {
		return workspaceUuid;
	}

	public void setWorkspaceUuid(UUID workplaceUuid) {
		this.workspaceUuid = workplaceUuid;
	}

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}

}
