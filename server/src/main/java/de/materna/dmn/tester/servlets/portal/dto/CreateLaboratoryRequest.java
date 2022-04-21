package de.materna.dmn.tester.servlets.portal.dto;

import java.util.UUID;

import de.materna.dmn.tester.enums.VisabilityType;

public class CreateLaboratoryRequest {
	private UUID tokenUuid;
	private String name;
	private String description;
	private VisabilityType visability;

	public CreateLaboratoryRequest() {
	}

	public CreateLaboratoryRequest(String name, String description, UUID tokenUuid, VisabilityType visability) {
		this.name = name;
		this.description = description;
		this.tokenUuid = tokenUuid;
		this.visability = visability;
	}

	public UUID getTokenUuid() {
		return tokenUuid;
	}

	public void setTokenUuid(UUID tokenUuid) {
		this.tokenUuid = tokenUuid;
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
