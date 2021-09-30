package de.materna.dmn.tester.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchronizationHelper {
	private static Map<String, ReadWriteLock> workspaceLocks = new HashMap<>();

	public static ReadWriteLock getWorkspaceLock(String workspace) {
		ReadWriteLock workspaceLock = workspaceLocks.get(workspace);
		if (workspaceLock == null) {
			workspaceLock = new ReentrantReadWriteLock();
			workspaceLocks.put(workspace, workspaceLock);
		}

		return workspaceLock;
	}
}
