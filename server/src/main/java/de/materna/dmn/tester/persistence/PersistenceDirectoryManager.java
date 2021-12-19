package de.materna.dmn.tester.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.materna.jdec.serialization.SerializationHelper;

public class PersistenceDirectoryManager<T> {
	private static final Logger log = LoggerFactory.getLogger(PersistenceDirectoryManager.class);

	private Path directory;
	private Class<T> entityClass;
	private String extension;

	public PersistenceDirectoryManager(String workspace, String entity, Class<T> entityClass, String extension)
			throws IOException {
		directory = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", workspace, entity);

		// We can't identify the generic type at runtime, so we need to save it for
		// serialization
		this.entityClass = entityClass;
		this.extension = extension;
	}

	public Map<String, T> getFiles() throws IOException {
		Map<String, T> files = new LinkedHashMap<>();

		if (Files.exists(directory)) {
			try (Stream<Path> stream = Files.list(directory)) {
				stream.forEach(path -> {
					// We need to remove the file extension.
					String key = path.getFileName().toString().split("\\.")[0];
					try {
						files.put(key, getFile(key));
					} catch (IOException e) {
						log.error(path.getFileName() + " cannot be read: ", e);
					}
				});
			}
		}

		return files;
	}

	@SuppressWarnings("unchecked")
	public T getFile(String key) throws IOException {
		String content = new String(Files.readAllBytes(Paths.get(directory.toString(), key + "." + extension)),
				StandardCharsets.UTF_8);
		if (entityClass == String.class) {
			return (T) content;
		} else {
			return (T) SerializationHelper.getInstance().toClass(content, entityClass);
		}
	}

	public boolean fileExists(String key) {
		return Files.exists(Paths.get(directory.toString(), key + "." + extension));
	}

	public void persistFile(String key, T value) throws IOException {
		Files.createDirectories(directory);
		if (entityClass == String.class) {
			Files.write(Paths.get(directory.toString(), key + "." + extension),
					((String) value).getBytes(StandardCharsets.UTF_8));
		} else {
			Files.write(Paths.get(directory.toString(), key + "." + extension),
					SerializationHelper.getInstance().toJSON(value).getBytes(StandardCharsets.UTF_8));
		}
	}

	public void removeFile(String key) throws IOException {
		if (Files.exists(Paths.get(directory.toString(), key + "." + extension))) {
			Files.delete(Paths.get(directory.toString(), key + "." + extension));
		}
	}

	public void removeAllFiles() throws IOException {
		if (Files.exists(directory)) {
			try (Stream<Path> stream = Files.list(directory)) {
				stream.forEach(path -> {
					try {
						// We need to remove the file extension.
						removeFile(path.getFileName().toString().split("\\.")[0]);
					} catch (IOException e) {
						log.error(path.getFileName() + " cannot be deleted: ", e);
					}
				});
			}
		}
	}

	public void verifyAllFiles() throws IOException {
		if (Files.exists(directory)) {
			try (Stream<Path> stream = Files.list(directory)) {
				Iterator<Path> iterator = stream.iterator();
				while (iterator.hasNext()) {
					Path path = iterator.next();
					try {
						getFile(path.getFileName().toString().split("\\.")[0]);
					} catch (Exception e) {
						throw new IOException(path.getFileName() + " cannot be verified: ", e);
					}
				}
			}
		}
	}

	public Path getDirectory() {
		return directory;
	}
}