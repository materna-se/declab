package de.materna.dmn.tester.servlets.workspace.beans;

import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.materna.jdec.serialization.SerializationHelper;
import de.materna.dmn.tester.persistence.PersistenceFileManager;

public class Configuration {
	
	private PersistenceFileManager fileManager;
	//TODO Change to integer
	private String version = "";
	private String name = "";
	private String description = "";
	//private String model;
	
	private long createdDate = Long.MIN_VALUE;
	private long modifiedDate = Long.MIN_VALUE;
	
	public enum Access {
		PUBLIC, PROTECTED, PRIVATE;
	}
	private Access mode = Access.PUBLIC;
	private String token = "";
	
	public Configuration() {
		
	}
	
	public Configuration(String name, PersistenceFileManager fileManager) {
		this.fileManager = fileManager;
		this.version = "1";
		this.name = name;
		this.createdDate = Long.MIN_VALUE;
		this.modifiedDate = Long.MIN_VALUE;
		this.mode = Access.PUBLIC;
		this.token = "";
	}
	
	public String printAsJson() {
		String json = (String) SerializationHelper.getInstance().toJSON(this);
		return json;
	}
	
	public void serializeToJson() {
		try {
			fileManager.persistFile(printAsJson());
			modifiedDate = System.currentTimeMillis();
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	public boolean deserializeFromJson(String body) {
		String versionTemp = null, nameTemp = null, descTemp = null, tokenTemp = null;
		long createdTemp = Long.MIN_VALUE, modifiedTemp = Long.MIN_VALUE;
		Access modeTemp = Access.PRIVATE;

		
		Configuration input = (Configuration) SerializationHelper.getInstance().toClass(body, Configuration.class);
		
		versionTemp = input.getVersion();
		nameTemp = input.getName();
		descTemp = input.getDescription();
		createdTemp = input.getCreatedDate();
		modifiedTemp = input.getModifiedDate();
		modeTemp = input.getMode();
		tokenTemp = input.getToken();

		//Only apply changes if import was successful and imported configuration is valid
		if(versionTemp == null || nameTemp == null || modeTemp == null) {
			return false;
		} else {
			this.setVersion(versionTemp);
			this.setName(nameTemp);
			if(descTemp == null) {
				this.setDescription("");
			} else {
				this.setDescription(descTemp);
			}
			this.setCreatedDate(createdTemp);
			this.setModifiedDate(modifiedTemp);
			this.setMode(modeTemp);
			if(tokenTemp == null) {
				this.setToken("");
			} else {
				this.setToken(tokenTemp);
			}
			return true;
		}
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
		//TODO What to do here?
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

	@JsonIgnore
	protected PersistenceFileManager getFileManager() {
		return fileManager;
	}

	@JsonIgnore
	private void setFileManager(PersistenceFileManager fileManager) {
		this.fileManager = fileManager;
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
}
