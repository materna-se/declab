package de.materna.dmn.tester.servlets.portal.dto;

import java.util.UUID;

public class DeleteWorkplaceRequest {
	private UUID sessionTokenUuid;
	private UUID workplaceUuid;

	public DeleteWorkplaceRequest() {
	}

	public DeleteWorkplaceRequest(UUID sessionTokenUuid, UUID workplaceUuid) {
		this.setSessionTokenUuid(sessionTokenUuid);
		this.setWorkplaceUuid(workplaceUuid);
	}

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}

	public UUID getWorkplaceUuid() {
		return workplaceUuid;
	}

	public void setWorkplaceUuid(UUID workplaceUuid) {
		this.workplaceUuid = workplaceUuid;
	}
}
