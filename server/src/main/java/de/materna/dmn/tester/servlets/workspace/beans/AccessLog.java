package de.materna.dmn.tester.servlets.workspace.beans;

import com.fasterxml.jackson.core.type.TypeReference;
import de.materna.dmn.tester.helpers.Serializable;
import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.jdec.serialization.SerializationHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AccessLog extends Serializable {
	private PersistenceFileManager fileManager;
	private ArrayList<AccessLogEntry> log = new ArrayList<>();
	private final int logLength = 1000;

	public AccessLog() {
	}

	public AccessLog(PersistenceFileManager fileManager) throws IOException {
		this.fileManager = fileManager;

		fromJson(fileManager.getFile());
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
		try {
			fileManager.persistFile(SerializationHelper.getInstance().toJSON(this));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<AccessLogEntry> getLog() {
		return log;
	}

	public void setLog(ArrayList<AccessLogEntry> log) {
		this.log = log;
	}

	@Override
	public String toJson() {
		return SerializationHelper.getInstance().toJSON(log);
	}

	@Override
	public void fromJson(String json) {
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
