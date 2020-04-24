package de.materna.dmn.tester;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class TestHelper {
	public static Path getRootPath() throws URISyntaxException {
		return new File(Thread.currentThread().getContextClassLoader().getResource("log4j.properties").toURI()).getParentFile().toPath();
	}

	public static void applyScenario(String scenario) throws URISyntaxException, IOException {
		Path rootPath = getRootPath();

		Path scenarioPath = rootPath.resolve("scenario");
		flushScenario(scenarioPath);

		FileUtils.copyDirectory(
				rootPath.resolve("scenarios").resolve(scenario).toFile(),
				scenarioPath.resolve("dmn").resolve("workspaces").toFile()
		);

		System.setProperty("jboss.server.data.dir", scenarioPath.toString());
	}

	public static void flushScenario(Path scenarioPath) throws IOException {
		Files.walk(scenarioPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	}
}
