package de.materna.dmn.tester.servlets.portal.dto.laboratory;

import de.materna.dmn.tester.enums.VisabilityType;

public class UpdateLaboratoryRequest {
	private String laboratoryUuid;
	private String name;
	private String description;
	private VisabilityType visability;

	private String jwt;

	public UpdateLaboratoryRequest() {
	}

	public UpdateLaboratoryRequest(String laboratoryUuid, String name, String description, VisabilityType visability,
			String jwt) {
		setLaboratoryUuid(laboratoryUuid);
		setName(name);
		setDescription(description);
		setVisability(visability);
		setJwt(jwt);
	}

	public String getLaboratoryUuid() {
		return laboratoryUuid;
	}

	public void setLaboratoryUuid(String laboratoryUuid) {
		this.laboratoryUuid = laboratoryUuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public VisabilityType getVisability() {
		return visability;
	}

	public void setVisability(VisabilityType visability) {
		this.visability = visability;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}
