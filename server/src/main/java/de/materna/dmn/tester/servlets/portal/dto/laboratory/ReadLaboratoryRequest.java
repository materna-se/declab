package de.materna.dmn.tester.servlets.portal.dto.laboratory;

public class ReadLaboratoryRequest {
	private String laboratoryUuid;
	private String query;

	private String jwt;

	public ReadLaboratoryRequest() {
	}

	public ReadLaboratoryRequest(String laboratoryUuid, String query, String jwt) {
		this.setLaboratoryUuid(laboratoryUuid);
		this.setQuery(query);
		this.setJwt(jwt);
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

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}
