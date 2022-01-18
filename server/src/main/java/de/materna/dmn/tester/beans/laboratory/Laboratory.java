package de.materna.dmn.tester.beans.laboratory;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;

import de.materna.dmn.tester.enums.VisabilityType;

@Entity
@Table(name = "laboratory", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
public class Laboratory {
	@Id
	@Column(name = "uuid", unique = true, nullable = false)
	private String uuid;
	@Column(name = "name")
	@NotEmpty(message = "The name of the Laboratory cannot be empty.")
	private String name;
	@Column(name = "description")
	private String description;
	@Column(name = "visability")
	private VisabilityType visability;

	public Laboratory() {
	}

	public Laboratory(String name) {
		this(name, "", VisabilityType.PRIVATE);
	}

	public Laboratory(String name, String description) {
		this(name, description, VisabilityType.PRIVATE);
	}

	public Laboratory(String name, VisabilityType visability) {
		this(name, "", visability);
	}

	public Laboratory(String name, String description, VisabilityType visability) {
		this.uuid = UUID.randomUUID().toString();
		this.name = name;
		this.description = description;
		this.visability = visability;
	}

	public String getUuid() {
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

}
