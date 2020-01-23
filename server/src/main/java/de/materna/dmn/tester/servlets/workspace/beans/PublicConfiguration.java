package de.materna.dmn.tester.servlets.workspace.beans;

import de.materna.jdec.serialization.SerializationHelper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicConfiguration {

	public int version;
	public String name;
	public String description;
	public Access access = Access.PUBLIC;
	
	public PublicConfiguration() {
		
	}
	
	public String printAsJson() {
		return SerializationHelper.getInstance().toJSON(this);
	}
	
	@JsonProperty
	public int getVersion() {
		return version;
	}

	@JsonProperty
	public void setVersion(int version) {
		this.version = version;
	}

	@JsonProperty
	public String getName() {
		return name;
	}

	@JsonProperty
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty
	public String getDescription() {
		return description;
	}

	@JsonProperty
	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonProperty
	public Access getAccess() {
		return access;
	}

	@JsonProperty
	public void setAccess(Access mode) {
		this.access = mode;
	}
	
	public enum Access {
		PUBLIC, PROTECTED, PRIVATE;
	}
}
