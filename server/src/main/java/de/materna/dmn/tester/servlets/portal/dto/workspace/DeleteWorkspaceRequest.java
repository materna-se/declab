package de.materna.dmn.tester.servlets.portal.dto.workspace;

public class DeleteWorkspaceRequest {
	private String workspaceUuid;

	private String jwt;

	public DeleteWorkspaceRequest() {
	}

	public DeleteWorkspaceRequest(String workplaceUuid, String jwt) {
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
