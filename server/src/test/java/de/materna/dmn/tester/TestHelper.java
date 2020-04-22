package de.materna.dmn.tester;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class TestHelper {
	public static Path getRootPath() throws URISyntaxException {
		return new File(Thread.currentThread().getContextClassLoader().getResource("log4j.properties").toURI()).getParentFile().toPath();
	}

	private static void applyScenario(String scenario) throws URISyntaxException, IOException {
		Path rootPath = getRootPath();

		Path scenarioPath = rootPath.resolve("scenario");

		FileUtils.copyDirectory(
				rootPath.resolve("scenarios").resolve(scenario).toFile(),
				scenarioPath.resolve("dmn").resolve("workspaces").toFile()
		);

		System.setProperty("jboss.server.data.dir", scenarioPath.toString());
	}
}
