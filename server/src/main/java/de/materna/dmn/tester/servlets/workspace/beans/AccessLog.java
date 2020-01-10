package de.materna.dmn.tester.servlets.workspace.beans;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.materna.dmn.tester.persistence.PersistenceFileManager;
import de.materna.jdec.serialization.SerializationHelper;

public class AccessLog {
	private ArrayList<AccessLogEntry> log = new ArrayList<AccessLogEntry>();
	private final int logLength = 1000;
	private PersistenceFileManager fileManager;

	//Empty constructor for SerializationHelper
	public AccessLog() {
		
	}
	
	public AccessLog(String name, PersistenceFileManager fileManager) throws IOException {
		this.fileManager = fileManager;
		
		//Import previous access log if it exists
		if(fileManager.fileExists()) {
			deserializeFromJson(fileManager.getFile());
		}
	}
	
	public void writeMessage(String message, long timestamp) {
		if(log.size() > logLength) {
			trimLog(log);
		}
		if(log.size() == logLength) {
			log.remove(0);
		}
		
		log.add(new AccessLogEntry(timestamp, message));
		serializeToJson();
	}
	
	public String printAsJson() {
		String json = (String) SerializationHelper.getInstance().toJSON(this);
		return json;
	}
	
	public void serializeToJson() {
		try {
			fileManager.persistFile(printAsJson());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deserializeFromJson(String body) {
		AccessLog input = (AccessLog) SerializationHelper.getInstance().toClass(body, AccessLog.class);
		
		ArrayList<AccessLogEntry> logTemp = input.getLog();
		if(logTemp == null) {
			throw new RuntimeException();
		} else {
			//If previous log is bigger than currently allowed size, trim it down
			if(logTemp.size() > logLength) {
				trimLog(logTemp);
			}
			this.setLog(logTemp);
		}
	}
	
	public void trimLog(ArrayList<AccessLogEntry> logTemp) {
		int diff = logTemp.size() - logLength;
		for(int i = 0; i < diff; i++) {
			logTemp.remove(0);
		}
	}
	
	public ArrayList<AccessLogEntry> getLog() {
		return log;
	}

	public void setLog(ArrayList<AccessLogEntry> log) {
		this.log = log;
	}
	
	@JsonIgnore
	public int getLogLength() {
		return logLength;
	}
	
	public static class AccessLogEntry {
		private long timestamp;
		private String message;
		//TODO String data (for metadata or changes)
		
		//Empty constructor for SerializationHelper
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

	@JsonIgnore
	public PersistenceFileManager getFileManager() {
		return fileManager;
	}

	@JsonIgnore
	public void setFileManager(PersistenceFileManager fileManager) {
		this.fileManager = fileManager;
	}
}
