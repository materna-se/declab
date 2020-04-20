package de.materna.dmn.tester;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;

public class TestExtension implements BeforeAllCallback {
	private static boolean executed = false;

	@Override
	public void beforeAll(ExtensionContext extensionContext) throws Exception {
		if(executed) {
			return;
		}

		System.setProperty("jboss.server.data.dir", new File(Thread.currentThread().getContextClassLoader().getResource("log4j.properties").toURI()).getParent());
	}
}
