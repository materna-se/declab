package de.materna.dmn.tester.persistence;

import de.materna.jdec.serialization.SerializationHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class PersistenceDirectoryManager<T> {
	private static final Logger log = Logger.getLogger(PersistenceDirectoryManager.class);

	private Path directory;
	private Class<T> entityClass;
	private String extension;

	public PersistenceDirectoryManager(String workspace, String entity, Class<T> entityClass, String extension) throws IOException {
		directory = Paths.get(System.getProperty("jboss.server.data.dir"), "dmn", "workspaces", workspace, entity);

		// We can't identify the generic type at runtime, so we need to save it for serialization
		this.entityClass = entityClass;
		this.extension = extension;
	}

	/**
	 * @todo Sort map by name.
	 */
	public Map<String, T> getFiles() throws IOException {
		Map<String, T> files = new HashMap<>();

		if (Files.exists(directory)) {
			try (Stream<Path> stream = Files.list(directory)) {
				Iterator<Path> iterator = stream.iterator();
				while (iterator.hasNext()) {
					Path path = iterator.next();

					// We need to remove the file extension.
					String key = path.getFileName().toString().split("\\.")[0];
					if(entityClass == String.class) {
						String value = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
						files.put(key, (T) value);
					} else {
						T value = (T) SerializationHelper.getInstance().toClass(new String(Files.readAllBytes(path), StandardCharsets.UTF_8), entityClass);
						files.put(key, value);
					}
				}
			}
		}

		return files;
	}
	
	public String getFile(String key) throws IOException {
		Path file = Paths.get(directory.toString(), key + "." + extension);
		return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
	}
	
	public boolean fileExists(String key) throws IOException {
		if(Files.exists(Paths.get(directory.toString(), key + "." + extension))) {
			return true;
		} else {
			return false;
		}
	}

	public void persistFile(String key, T value) throws IOException {
		if(entityClass == String.class) {
			Files.createDirectories(directory);
			Files.write(Paths.get(directory.toString(), key + "." + extension), ((String) value).getBytes(StandardCharsets.UTF_8));
		}
		else {
			Files.createDirectories(directory);
			Files.write(Paths.get(directory.toString(), key + "." + extension), SerializationHelper.getInstance().toJSON(value).getBytes(StandardCharsets.UTF_8));
		}
	}

	public void removeFile(String key) throws IOException {
		if(Files.exists(Paths.get(directory.toString(), key + "." + extension))) {
			Files.delete(Paths.get(directory.toString(), key + "." + extension));
		}
	}
	
	public void removeAllFiles() throws IOException {
		if(!Files.exists(directory)) {
			return;
		}
		File dir = directory.toFile();
		for(File file: dir.listFiles()) {
		    if (!file.isDirectory()) {
		        file.delete();
		    }
		}
	}

	public Path getDirectory() {
		return directory;
	}
}
