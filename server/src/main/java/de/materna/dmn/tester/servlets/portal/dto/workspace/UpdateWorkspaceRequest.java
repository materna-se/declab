package de.materna.dmn.tester.servlets.portal.dto.workspace;

import de.materna.dmn.tester.enums.VisabilityType;

public class UpdateWorkspaceRequest {
	private String workspaceUuid;
	private String name;
	private String description;
	private VisabilityType visability;

	private String jwt;

	public UpdateWorkspaceRequest() {
	}

	public UpdateWorkspaceRequest(String workspaceUuid, String name, String description, VisabilityType visability,
			String jwt) {
		setWorkspaceUuid(workspaceUuid);
		setName(name);
		setDescription(description);
		setVisability(visability);
		setJwt(jwt);
	}

	public String getWorkspaceUuid() {
		return workspaceUuid;
	}

	public void setWorkspaceUuid(String workspaceUuid) {
		this.workspaceUuid = workspaceUuid;
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
