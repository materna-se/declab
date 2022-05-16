package de.materna.dmn.tester.beans.userpermission;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.materna.dmn.tester.enums.UserPermissionType;

@Entity
@Table(name = "userpermission")
public class UserPermission {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "user")
	@NotNull
	private String user;
	@Column(name = "laboratory")
	private String laboratory;
	@Column(name = "workspace")
	private String workspace;
	@Column(name = "type")
	private UserPermissionType type;

	public UserPermission() {
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(String laboratory) {
		this.laboratory = laboratory;
	}

	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public UserPermissionType getType() {
		return type;
	}

	public void setType(UserPermissionType type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}
}
