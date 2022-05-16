package de.materna.dmn.tester.servlets.portal.dto.laboratory;

public class ReadLaboratoryRequest {
	private String laboratoryUuid;

	private String sessionTokenUuid;

	public ReadLaboratoryRequest() {
	}

	public ReadLaboratoryRequest(String laboratoryUuid, String sessionTokenUuid) {
		this.setLaboratoryUuid(laboratoryUuid);
		this.setSessionTokenUuid(sessionTokenUuid);
	}

	public String getLaboratoryUuid() {
		return laboratoryUuid;
	}

	public void setLaboratoryUuid(String laboratoryUuid) {
		this.laboratoryUuid = laboratoryUuid;
	}

	public String getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(String sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
