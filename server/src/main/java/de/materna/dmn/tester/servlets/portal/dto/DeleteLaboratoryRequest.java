package de.materna.dmn.tester.servlets.portal.dto;

import java.util.UUID;

public class DeleteLaboratoryRequest {
	private UUID sessionTokenUuid;
	private UUID laboratoryUuid;

	public DeleteLaboratoryRequest() {
	}

	public DeleteLaboratoryRequest(UUID sessionTokenUuid, UUID laboratoryUuid) {
		this.setSessionTokenUuid(sessionTokenUuid);
		this.setLaboratoryUuid(laboratoryUuid);
	}

	public UUID getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(UUID sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}

	public UUID getLaboratoryUuid() {
		return laboratoryUuid;
	}

	public void setLaboratoryUuid(UUID laboratoryUuid) {
		this.laboratoryUuid = laboratoryUuid;
	}
}
