package de.materna.dmn.tester.sockets.managers;

import de.materna.dmn.tester.helpers.SynchronizationHelper;
import de.materna.dmn.tester.servlets.model.ModelServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SessionManager {
	private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
	private static SessionManager instance;

	private final Map<String, List<Session>> workspaceSessions = new HashMap<>();

	private SessionManager() {
	}

	public void add(String workspaceUUID, Session session) throws InterruptedException {
		boolean lockAcquired = SynchronizationHelper.getWorkspaceLock(workspaceUUID).writeLock().tryLock(30, TimeUnit.SECONDS);
		if (!lockAcquired) {
			throw new InterruptedException(String.format("Could not acquire lock for workspace %s!", workspaceUUID));
		}

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

	public void remove(String workspaceUUID, Session session) throws InterruptedException {
		boolean lockAcquired = SynchronizationHelper.getWorkspaceLock(workspaceUUID).writeLock().tryLock(30, TimeUnit.SECONDS);
		if (!lockAcquired) {
			throw new InterruptedException(String.format("Could not acquire lock for workspace %s!", workspaceUUID));
		}

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

	public void notify(String workspaceUUID, String message) throws InterruptedException {
		boolean lockAcquired = SynchronizationHelper.getWorkspaceLock(workspaceUUID).readLock().tryLock(30, TimeUnit.SECONDS);
		if (!lockAcquired) {
			throw new InterruptedException(String.format("Could not acquire lock for workspace %s!", workspaceUUID));
		}

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

	public int listeners(String workspaceUUID) throws InterruptedException {
		boolean lockAcquired = SynchronizationHelper.getWorkspaceLock(workspaceUUID).readLock().tryLock(30, TimeUnit.SECONDS);
		if (!lockAcquired) {
			throw new InterruptedException(String.format("Could not acquire lock for workspace %s!", workspaceUUID));
		}

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
