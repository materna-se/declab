package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.helpers.SerializationHelper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PersistenceDirectoryManager<T> {
	private static final Logger log = Logger.getLogger(PersistenceDirectoryManager.class);

	private Path directory;
	private Class<T> entityClass;

	public PersistenceDirectoryManager(String workspace, String entity, Class<T> entityClass) throws IOException {
		// TODO: Get the data directory in a different way so it can be used in other application servers
		directory = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", workspace, entity);
		Files.createDirectories(directory);

		// We can't identify the generic type at runtime, so we need to save it for serialization
		this.entityClass = entityClass;
	}

	/**
	 * @todo Sort map by name.
	 */
	public Map<String, T> getFiles() throws IOException {
		Map<String, T> values = new HashMap<>();

		Iterator<Path> iterator = Files.list(directory).iterator();
		while (iterator.hasNext()) {
			Path path = iterator.next();

			// We need to remove the file extension.
			String key = path.getFileName().toString().split("\\.")[0];
			T value = (T) SerializationHelper.getInstance().toClass(new String(Files.readAllBytes(path), StandardCharsets.UTF_8), entityClass);

			values.put(key, value);
		}

		return values;
	}

	public void persistFile(String key, T value) throws IOException {
		Files.write(Paths.get(directory.toString(), key + ".json"), SerializationHelper.getInstance().toJSON(value).getBytes(StandardCharsets.UTF_8));
	}

	public void removeFile(String key) throws IOException {
		Files.delete(Paths.get(directory.toString(), key + ".json"));
	}

	public Path getDirectory() {
		return directory;
	}
}
