package de.materna.dmn.tester.beans.relationship;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.materna.dmn.tester.enums.RelationshipType;

@Entity
@Table(name = "relationship")
public class Relationship {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "user")
	@NotNull
	private UUID user;
	@Column(name = "laboratory")
	private UUID laboratory;
	@Column(name = "workspace")
	private UUID workspace;
	@Column(name = "type")
	private RelationshipType type;

	public Relationship() {
	}

	public UUID getUser() {
		return user;
	}

	public void setUser(UUID user) {
		this.user = user;
	}

	public UUID getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(UUID laboratory) {
		this.laboratory = laboratory;
	}

	public UUID getWorkspace() {
		return workspace;
	}

	public void setWorkspace(UUID workspace) {
		this.workspace = workspace;
	}

	public RelationshipType getType() {
		return type;
	}

	public void setType(RelationshipType type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

}
