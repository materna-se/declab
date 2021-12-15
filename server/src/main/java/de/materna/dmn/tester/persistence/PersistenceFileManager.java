package de.materna.dmn.tester.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PersistenceFileManager {
	private Path path;

	public PersistenceFileManager(String workspace, String entity) {
		path = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", workspace, entity);
	}

	public String getFile() throws IOException {
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}

	public void persistFile(String value) throws IOException {
		Files.createDirectories(path.getParent());
		Files.write(path, value.getBytes(StandardCharsets.UTF_8));
	}

	public void removeFile() throws IOException {
		if (fileExists()) {
			Files.delete(path);
		}
	}

	public boolean fileExists() {
		return Files.exists(path);
	}

	public Path getPath() {
		return path;
	}
}