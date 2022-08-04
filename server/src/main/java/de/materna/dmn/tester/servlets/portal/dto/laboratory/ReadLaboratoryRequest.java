package de.materna.dmn.tester.servlets.portal.dto.laboratory;

public class ReadLaboratoryRequest {
	private String laboratoryUuid;
	private String query;

	private String sessionTokenUuid;

	public ReadLaboratoryRequest() {
	}

	public ReadLaboratoryRequest(String laboratoryUuid, String query, String sessionTokenUuid) {
		this.setLaboratoryUuid(laboratoryUuid);
		this.setQuery(query);
		this.setSessionTokenUuid(sessionTokenUuid);
	}

	public String getLaboratoryUuid() {
		return laboratoryUuid;
	}

	public void setLaboratoryUuid(String laboratoryUuid) {
		this.laboratoryUuid = laboratoryUuid;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(String sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
