package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.TestExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;

@ExtendWith({TestExtension.class})
class MapPersistenceDirectoryManagerTest {
	private static PersistenceDirectoryManager<Object> persistenceDirectoryManager;
	private static Map<String, String> content;

	@BeforeAll
	static void beforeAll() throws IOException {
		persistenceDirectoryManager = new PersistenceDirectoryManager<>("test", "test", Object.class, "json");
		content = new HashMap<>();
		content.put("test", "test");
	}

	@Test
	void fileExistsWithFile() throws IOException {
		persistenceDirectoryManager.persistFile("test", content);
		Assertions.assertTrue(persistenceDirectoryManager.fileExists("test"));
		persistenceDirectoryManager.removeFile("test");
	}

	@Test
	void fileExistsWithoutFile() {
		Assertions.assertFalse(persistenceDirectoryManager.fileExists("test"));
	}

	@Test
	void getFileWithoutFile() {
		Assertions.assertThrows(NoSuchFileException.class, () -> {
			persistenceDirectoryManager.getFile("test");
		});
	}

	@Test
	void getFileWithFile() throws IOException {
		persistenceDirectoryManager.persistFile("test", content);
		Assertions.assertEquals(content, persistenceDirectoryManager.getFile("test"));
		persistenceDirectoryManager.removeFile("test");
	}

	@Test
	void removeFileWithFile() throws IOException {
		persistenceDirectoryManager.persistFile("test", content);
		persistenceDirectoryManager.removeFile("test");
		Assertions.assertFalse(persistenceDirectoryManager.fileExists("test"));
	}

	@Test
	void removeFileWithoutFile() throws IOException {
		persistenceDirectoryManager.removeFile("test");
	}
}