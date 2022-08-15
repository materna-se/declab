package de.materna.dmn.tester.servlets.portal.dto.laboratory;

public class DeleteLaboratoryRequest {
	private String laboratoryUuid;

	private String jwt;

	public DeleteLaboratoryRequest() {
	}

	public DeleteLaboratoryRequest(String laboratoryUuid, String jwt) {
		this.setLaboratoryUuid(laboratoryUuid);
		this.setJwt(jwt);
	}

	public String getLaboratoryUuid() {
		return laboratoryUuid;
	}

	public void setLaboratoryUuid(String laboratoryUuid) {
		this.laboratoryUuid = laboratoryUuid;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}
