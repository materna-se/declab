package de.materna.dmn.tester.beans.permission;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import de.materna.dmn.tester.beans.laboratory.Laboratory;
import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.enums.PermissionType;

@Entity
@Table(name = "`permission`")
public class Permission {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "userUuid")
	private String userUuid;
	@Column(name = "laboratoryUuid")
	private String laboratoryUuid;
	@Column(name = "workspaceUuid")
	private String workspaceUuid;
	@Column(name = "type")
	private PermissionType type;

	public Permission() {
	}

	public Permission(User user, Laboratory laboratory, PermissionType type) {
		this(user.getUuid(), laboratory.getUuid(), null, type);
	}

	public Permission(User user, Workspace workspace, PermissionType type) {
		this(user.getUuid(), null, workspace.getUuid(), type);
	}

	public Permission(Laboratory laboratory, Workspace workspace, PermissionType type) {
		this(laboratory.getUuid(), null, workspace.getUuid(), type);
	}

	public Permission(String userUuid, String laboratoryUuid, String workspaceUuid, PermissionType type) {
		setUserUuid(userUuid);
		setLaboratoryUuid(laboratoryUuid);
		setWorkspaceUuid(workspaceUuid);
		setType(type);
	}

	public Long getId() {
		return id;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getLaboratoryUuid() {
		return laboratoryUuid;
	}

	public void setLaboratoryUuid(String laboratoryUuid) {
		this.laboratoryUuid = laboratoryUuid;
	}

	public String getWorkspaceUuid() {
		return workspaceUuid;
	}

	public void setWorkspaceUuid(String workspaceUuid) {
		this.workspaceUuid = workspaceUuid;
	}

	public PermissionType getType() {
		return type;
	}

	public void setType(PermissionType type) {
		this.type = type;
	}
}
