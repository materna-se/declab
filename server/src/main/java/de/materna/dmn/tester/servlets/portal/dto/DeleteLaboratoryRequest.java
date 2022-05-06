package de.materna.dmn.tester.servlets.portal.dto;

import java.util.UUID;

public class DeleteLaboratoryRequest {
	private UUID laboratoryUuid;

	private UUID sessionTokenUuid;

	public DeleteLaboratoryRequest() {
	}

	public DeleteLaboratoryRequest(UUID laboratoryUuid, UUID sessionTokenUuid) {
		this.setLaboratoryUuid(laboratoryUuid);
		this.setSessionTokenUuid(sessionTokenUuid);
	}

	public UUID getLaboratoryUuid() {
		return laboratoryUuid;
	}

	public void setLaboratoryUuid(UUID laboratoryUuid) {
		this.laboratoryUuid = laboratoryUuid;
	}

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
