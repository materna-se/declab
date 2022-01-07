package de.materna.dmn.tester.beans.relationship;

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
	private String user;
	@Column(name = "laboratory")
	private String laboratory;
	@Column(name = "workspace")
	private String workplace;
	@Column(name = "type")
	private RelationshipType type;

	public Relationship() {
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

	public String getWorkplace() {
		return workplace;
	}

	public void setWorkplace(String workplace) {
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
