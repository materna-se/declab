package de.materna.dmn.tester.beans.workspace;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import de.materna.dmn.tester.enums.VisabilityType;

@Entity
@Table(name = "workspace", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class Workspace {
	@Id
	@Column(name = "uuid", unique = true, nullable = false)
	private UUID uuid;
	@Column(name = "name")
	@NotEmpty(message = "The name of the Workspace cannot be empty.")
	private String name;
	@Column(name = "description")
	private String description;
	@Column(name = "visability")
	private VisabilityType visability;
	@Column(name = "laboratory")
	@NotNull
	private UUID laboratoryUuid;

	public Workspace() {
	}

	public Workspace(String name, String description, VisabilityType visability, UUID laboratoryUuid) {
		this.uuid = UUID.randomUUID();
		setName(name);
		setDescription(description);
		setVisability(visability);
		setLaboratoryUuid(laboratoryUuid);
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

	public UUID getLaboratoryUuid() {
		return laboratoryUuid;
	}

	public void setLaboratoryUuid(UUID laboratoryUuid) {
		this.laboratoryUuid = laboratoryUuid;
	}
}
