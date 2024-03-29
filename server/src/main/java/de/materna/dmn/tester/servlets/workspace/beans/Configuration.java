package de.materna.dmn.tester.servlets.workspace.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.jdec.serialization.SerializationHelper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Configuration extends PublicConfiguration {
	private PersistenceFileManager fileManager;

	private String token = null;
	private String salt = null;
	private long createdDate = Long.MIN_VALUE;
	private long modifiedDate = Long.MIN_VALUE;
	private List<Map<String, String>> models = new LinkedList<>();
	private DecisionService decisionService = null;

	public Configuration() {
	}

	public Configuration(PersistenceFileManager fileManager) throws IOException {
		this.fileManager = fileManager;

		if (fileManager.fileExists()) {
			fromJSON(fileManager.getFile());
		}
	}

	public void serialize() throws IOException {
		fileManager.persistFile(toJSON());
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
	public List<Map<String, String>> getModels() {
		return models;
	}

	@JsonProperty
	public void setModels(List<Map<String, String>> models) {
		this.models = models;
	}

	public DecisionService getDecisionService() {
		return decisionService;
	}

	public void setDecisionService(DecisionService decisionService) {
		this.decisionService = decisionService;
	}

	@JsonIgnore
	public PublicConfiguration getPublicConfig() {
		PublicConfiguration publicConfiguration = new PublicConfiguration();
		publicConfiguration.setVersion(this.version);
		publicConfiguration.setName(this.name);
		publicConfiguration.setDescription(this.description);
		publicConfiguration.setAccess(this.access);
		return publicConfiguration;
	}

	@Override
	public void fromJSON(String body) {
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

	public static class DecisionService {
		private String name;
		private String namespace;

		public DecisionService() {
		}

		public String getName() {
			return name;
		}

		public String getNamespace() {
			return namespace;
		}
	}
}
