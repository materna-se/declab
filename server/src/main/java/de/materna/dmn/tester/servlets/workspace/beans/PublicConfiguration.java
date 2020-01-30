package de.materna.dmn.tester.servlets.workspace.beans;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicConfiguration extends Serializable {

	public int version;
	public String name;
	public String description;
	public Access access = Access.PUBLIC;
	
	public PublicConfiguration() {
		
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
	
	public void fromJson(String json) {
		PublicConfiguration temp = (PublicConfiguration) SerializationHelper.getInstance().toClass(json, PublicConfiguration.class); 
		this.version = temp.getVersion();
		this.name = temp.getName();
		this.description = temp.getDescription();
		this.access = temp.getAccess();
	}
	
	public enum Access {
		PUBLIC, PROTECTED, PRIVATE;
	}
}
