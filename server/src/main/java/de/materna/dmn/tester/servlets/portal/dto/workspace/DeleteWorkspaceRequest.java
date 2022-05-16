package de.materna.dmn.tester.servlets.portal.dto.workspace;

public class DeleteWorkspaceRequest {
	private String workspaceUuid;

	private String sessionTokenUuid;

	public DeleteWorkspaceRequest() {
	}

	public DeleteWorkspaceRequest(String workplaceUuid, String sessionTokenUuid) {
		this.setWorkspaceUuid(workplaceUuid);
		this.setSessionTokenUuid(sessionTokenUuid);
	}

	public String getWorkspaceUuid() {
		return workspaceUuid;
	}

	public void setWorkspaceUuid(String workplaceUuid) {
		this.workspaceUuid = workplaceUuid;
	}

	public String getSessionTokenUuid() {
		return sessionTokenUuid;
	}

	public void setSessionTokenUuid(String sessionTokenUuid) {
		this.sessionTokenUuid = sessionTokenUuid;
	}
}
