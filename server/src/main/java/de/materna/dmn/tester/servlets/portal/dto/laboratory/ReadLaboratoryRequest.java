package de.materna.dmn.tester.servlets.portal.dto.laboratory;

import java.util.UUID;

public class ReadLaboratoryRequest {
	private UUID laboratoryUuid;

	private UUID sessionTokenUuid;

	public ReadLaboratoryRequest() {
	}

	public ReadLaboratoryRequest(UUID laboratoryUuid, UUID sessionTokenUuid) {
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
