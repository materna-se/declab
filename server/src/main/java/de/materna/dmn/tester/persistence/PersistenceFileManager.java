package de.materna.dmn.tester.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PersistenceFileManager {
	private Path file;

	public PersistenceFileManager(String workspace, String entity) throws IOException {
		file = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", workspace, entity);
		Files.createDirectories(file.getParent());
	}

	public String getFile() throws IOException {
		return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
	}

	public void persistFile(String value) throws IOException {
		Files.write(file, value.getBytes(StandardCharsets.UTF_8));
	}

	public void removeFile() throws IOException {
		Files.delete(file);
	}
}
