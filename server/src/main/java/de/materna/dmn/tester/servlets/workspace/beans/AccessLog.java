package de.materna.dmn.tester.servlets.workspace.beans;

import com.fasterxml.jackson.core.type.TypeReference;
import de.materna.dmn.tester.helpers.Serializable;
import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.jdec.serialization.SerializationHelper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

public class AccessLog extends Serializable {
	private static final Logger logger = Logger.getLogger(AccessLog.class);

	private final int logLength = 1000;
	private PersistenceFileManager fileManager;
	private ArrayList<AccessLogEntry> log = new ArrayList<>();

	public AccessLog() {
	}

	public AccessLog(PersistenceFileManager fileManager) throws IOException {
		this.fileManager = fileManager;

		if (fileManager.fileExists()) {
			fromJSON(fileManager.getFile());
		}
	}

	public void writeMessage(String message, long timestamp) {
		if (log.size() > logLength) {
			trim(log);
		}
		if (log.size() == logLength) {
			log.remove(0);
		}

		log.add(new AccessLogEntry(timestamp, message));
		serialize();
	}

	public void trim(ArrayList<AccessLogEntry> logTemp) {
		int diff = logTemp.size() - logLength;
		if (diff > 0) {
			logTemp.subList(0, diff).clear();
		}
	}

	public void serialize() {
		// Serializing the access log is not so important, we'll catch io exceptions.
		try {
			fileManager.persistFile(toJSON());
		}
		catch (IOException exception) {
			logger.error("Serialization failed: ", exception);
		}
	}

	public ArrayList<AccessLogEntry> getLog() {
		return log;
	}

	public void setLog(ArrayList<AccessLogEntry> log) {
		this.log = log;
	}

	@Override
	public String toJSON() {
		return SerializationHelper.getInstance().toJSON(log);
	}

	@Override
	public void fromJSON(String json) {
		this.log = SerializationHelper.getInstance().toClass(json, new TypeReference<ArrayList<AccessLogEntry>>() {
		});
	}

	public static class AccessLogEntry {
		private long timestamp;
		private String message;
		// TODO: String data (for metadata or changes)?

		public AccessLogEntry() {
		}

		public AccessLogEntry(long timestamp, String message) {
			this.timestamp = timestamp;
			this.message = message;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
