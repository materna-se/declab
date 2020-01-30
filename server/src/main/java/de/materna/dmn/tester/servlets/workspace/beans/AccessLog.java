package de.materna.dmn.tester.servlets.workspace.beans;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.jdec.serialization.SerializationHelper;

import java.io.IOException;
import java.util.ArrayList;

public class AccessLog extends Serializable {
	private PersistenceFileManager fileManager;
	private ArrayList<AccessLogEntry> log = new ArrayList<>();
	private final int logLength = 1000;

	public AccessLog() {
	}

	public AccessLog(PersistenceFileManager fileManager) throws IOException {
		this.fileManager = fileManager;

		// Import previous access log if it exists.
		if (fileManager.fileExists()) {
			deserialize(fileManager.getFile());
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
		try {
			fileManager.persistFile(SerializationHelper.getInstance().toJSON(this));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deserialize(String body) {
		AccessLog input = (AccessLog) SerializationHelper.getInstance().toClass(body, AccessLog.class);

		ArrayList<AccessLogEntry> logTemp = input.getLog();
		if (logTemp == null) {
			throw new RuntimeException();
		}
		else {
			//If previous log is bigger than currently allowed size, trim it down
			if (logTemp.size() > logLength) {
				trim(logTemp);
			}
			this.setLog(logTemp);
		}
	}
	
	public String print() {
		return SerializationHelper.getInstance().toJSON(this);
	}
	
	public ArrayList<AccessLogEntry> getLog() {
		return log;
	}

	public void setLog(ArrayList<AccessLogEntry> log) {
		this.log = log;
	}
	
	public void fromJson(String json) {
		AccessLog temp = (AccessLog) SerializationHelper.getInstance().toClass(json, AccessLog.class);
		this.log = temp.getLog();
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
