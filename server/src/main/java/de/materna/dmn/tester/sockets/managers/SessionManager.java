package de.materna.dmn.tester.sockets.managers;

import de.materna.dmn.tester.helpers.SynchronizationHelper;

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

	public void add(String workspaceUUID, Session session) {
		SynchronizationHelper.getWorkspaceLock(workspaceUUID).writeLock().lock();

		try {
			List<Session> sessions = workspaceSessions.get(workspaceUUID);
			if (sessions == null) {
				sessions = new ArrayList<>();
				workspaceSessions.put(workspaceUUID, sessions);
			}

			sessions.add(session);

		}
		finally {
			SynchronizationHelper.getWorkspaceLock(workspaceUUID).writeLock().unlock();
		}
	}

	public void remove(String workspaceUUID, Session session) {
		SynchronizationHelper.getWorkspaceLock(workspaceUUID).writeLock().lock();

		try {
			List<Session> sessions = workspaceSessions.get(workspaceUUID);
			if (sessions == null) {
				return;
			}

			sessions.remove(session);
		}
		finally {
			SynchronizationHelper.getWorkspaceLock(workspaceUUID).writeLock().unlock();
		}
	}

	public void notify(String workspaceUUID, String message) {
		SynchronizationHelper.getWorkspaceLock(workspaceUUID).readLock().lock();

		try {
			List<Session> sessions = workspaceSessions.get(workspaceUUID);
			if (sessions == null) {
				return;
			}

			for (Session session : sessions) {
				session.getAsyncRemote().sendText(message);
			}
		}
		finally {
			SynchronizationHelper.getWorkspaceLock(workspaceUUID).readLock().unlock();
		}
	}

	public int listeners(String workspaceUUID) {
		SynchronizationHelper.getWorkspaceLock(workspaceUUID).readLock().lock();

		try {
			List<Session> sessions = workspaceSessions.get(workspaceUUID);
			if (sessions == null) {
				return 0;
			}

			return sessions.size();
		}
		finally {
			SynchronizationHelper.getWorkspaceLock(workspaceUUID).readLock().unlock();
		}
	}

	public static SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}

		return instance;
	}
}
