package de.materna.dmn.tester.servlets.portal.dto.workspace;

public class ReadWorkspaceRequest {
	private String workspaceUuid;

	private String jwt;

	public ReadWorkspaceRequest() {
	}

	public ReadWorkspaceRequest(String workplaceUuid, String jwt) {
		this.setWorkspaceUuid(workplaceUuid);
		this.setJwt(jwt);
	}

	public String getWorkspaceUuid() {
		return workspaceUuid;
	}

	public void setWorkspaceUuid(String workplaceUuid) {
		this.workspaceUuid = workplaceUuid;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}
