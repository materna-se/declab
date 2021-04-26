package de.materna.dmn.tester.sockets.managers;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionManager {
	private static SessionManager instance;

	private final Map<String, List<Session>> workspaceSessions = new HashMap<>();

	private SessionManager() {
	}

	public void add(String workspace, Session session) {
		List<Session> sessions = workspaceSessions.get(workspace);
		if (sessions == null) {
			sessions = new ArrayList<>();
			workspaceSessions.put(workspace, sessions);
		}

		sessions.add(session);
	}

	public void remove(String workspace, Session session) {
		List<Session> sessions = workspaceSessions.get(workspace);
		if (sessions == null) {
			return;
		}

		sessions.remove(session);
	}

	public void notify(String workspace, String message) {
		List<Session> sessions = workspaceSessions.get(workspace);
		if (sessions == null) {
			return;
		}

		for (Session session : sessions) {
			session.getAsyncRemote().sendText(message);
		}
	}

	public int listeners(String workspace) {
		List<Session> sessions = workspaceSessions.get(workspace);
		if (sessions == null) {
			return 0;
		}

		return sessions.size();
	}

	public static SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}

		return instance;
	}
}
