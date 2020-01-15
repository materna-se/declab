package de.materna.dmn.tester.servlets.workspace.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.jdec.serialization.SerializationHelper;

import java.io.IOException;

public class Configuration {
	private PersistenceFileManager fileManager;

	private String version;
	private String name;
	private String description;

	private long createdDate = Long.MIN_VALUE;
	private long modifiedDate = Long.MIN_VALUE;

	private Access mode = Access.PUBLIC;
	private String token = "";

	public Configuration() {
	}

	public Configuration(PersistenceFileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void serialize() {
		try {
			fileManager.persistFile(SerializationHelper.getInstance().toJSON(this));
			modifiedDate = System.currentTimeMillis();
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public void deserialize(String body) {
		Configuration configuration = (Configuration) SerializationHelper.getInstance().toClass(body, Configuration.class);

		version = configuration.getVersion();
		name = configuration.getName();
		description = configuration.getDescription();
		createdDate = configuration.getCreatedDate();
		modifiedDate = configuration.getModifiedDate();
		mode = configuration.getMode();
		token = configuration.getToken();
	}


	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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
	
	public Access getMode() {
		return mode;
	}

	public void setMode(Access mode) {
		this.mode = mode;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@JsonProperty
	protected long getCreatedDate() {
		return createdDate;
	}

	@JsonProperty
	protected void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	@JsonProperty
	protected long getModifiedDate() {
		return modifiedDate;
	}

	@JsonProperty
	protected void setModifiedDate(long modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public enum Access {
		PUBLIC, PROTECTED, PRIVATE;
	}
}
