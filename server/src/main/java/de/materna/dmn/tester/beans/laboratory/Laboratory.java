package de.materna.dmn.tester.beans.laboratory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;

import de.materna.dmn.tester.beans.workspace.Workspace;
import de.materna.dmn.tester.enums.VisabilityType;

@Entity
@Table(name = "laboratory", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class Laboratory {
	@Id
	@Column(name = "uuid", unique = true, nullable = false)
	private UUID uuid;
	@Column(name = "name")
	@NotEmpty(message = "The name of the Laboratory cannot be empty.")
	private String name;
	@Column(name = "description")
	private String description;
	@Column(name = "visability")
	private VisabilityType visability;
	@Column(name = "workspaces")
	private List<Workspace> workspaces;

	public Laboratory() {
	}

	public Laboratory(String name, String description, VisabilityType visability) {
		this.uuid = UUID.randomUUID();
		this.name = name;
		this.description = description;
		this.visability = visability;
		this.workspaces = new ArrayList<>();
	}

	public UUID getUuid() {
		return uuid;
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

	public List<Workspace> getWorkspaces() {
		return workspaces;
	}

	public void setWorkspaces(List<Workspace> workspaces) {
		this.workspaces = workspaces;
	}
}
