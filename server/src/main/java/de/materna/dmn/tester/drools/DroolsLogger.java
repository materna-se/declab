package de.materna.dmn.tester.drools;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.LinkedList;
import java.util.List;

public class DroolsLogger extends AppenderSkeleton {
	private static List<String> warnings = new LinkedList<>();
	private static boolean enabled = false;

	public void append(LoggingEvent loggingEvent) {
		if (!enabled) {
			return;
		}

		warnings.add(loggingEvent.getMessage().toString());
	}

	public static void start() {
		// Remove all entries.
		warnings.clear();
		enabled = true;
	}

	public static List<String> stop() {
		enabled = false;
		return warnings;
	}

	public boolean requiresLayout() {
		return false;
	}

	public void close() {
		warnings.clear();
	}
}
