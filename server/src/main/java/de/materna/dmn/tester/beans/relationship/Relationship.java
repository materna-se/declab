package de.materna.dmn.tester.beans.relationship;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import de.materna.dmn.tester.enums.RelationshipType;

@Entity
@Table(name = "relationship")
public class Relationship {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "user")
	private UUID user;
	@Column(name = "laboratory")
	private UUID laboratory;
	@Column(name = "workspace")
	private UUID workplace;
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

	public UUID getWorkplace() {
		return workplace;
	}

	public void setWorkplace(UUID workplace) {
		this.workplace = workplace;
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
