package de.materna.dmn.tester.servlets.workspace.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.materna.dmn.tester.helpers.HashingHelper;
import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.jdec.serialization.SerializationHelper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;

public class Configuration extends PublicConfiguration {
	private PersistenceFileManager fileManager;

	private String token = null;
	private String salt = null;
	private long createdDate = Long.MIN_VALUE;
	private long modifiedDate = Long.MIN_VALUE;
	private LinkedList<HashMap<String, String>> models = new LinkedList<HashMap<String, String>>();

	public Configuration() {
	}

	public Configuration(PersistenceFileManager fileManager) throws IOException {
		this.fileManager = fileManager;

		if(fileManager.fileExists()) {
			fromJson(fileManager.getFile());
		}
	}

	public void serialize() {
		try {
			fileManager.persistFile(toJson());
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	
	@JsonIgnore
	public PublicConfiguration getPublicConfig() {
		PublicConfiguration pubconfig = new PublicConfiguration();
		pubconfig.setVersion(this.version);
		pubconfig.setName(this.name);
		pubconfig.setDescription(this.description);
		pubconfig.setAccess(this.access);
		return pubconfig;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	@JsonProperty
	public String getSalt() {
		return salt;
	}

	@JsonProperty
	public void setSalt(String salt) {
		this.salt = salt;
	}

	@JsonProperty
	public long getCreatedDate() {
		return createdDate;
	}

	@JsonProperty
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	@JsonProperty
	public long getModifiedDate() {
		return modifiedDate;
	}

	@JsonProperty
	public void setModifiedDate(long modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	@JsonProperty
	public LinkedList<HashMap<String, String>> getModels() {
		return models;
	}

	@JsonProperty
	public void setModels(LinkedList<HashMap<String, String>> modelImportOrder) {
		this.models = modelImportOrder;
	}
	
	@Override
	public void fromJson(String body) {
		Configuration temp = (Configuration) SerializationHelper.getInstance().toClass(body, Configuration.class);
		this.version = temp.getVersion();
		this.name = temp.getName();
		this.description = temp.getDescription();
		this.createdDate = temp.getCreatedDate();
		this.modifiedDate = temp.getModifiedDate();
		this.access = temp.getAccess();
		this.token = temp.getToken();
		this.salt = temp.getSalt();
		this.models = temp.getModels();
	}
}
