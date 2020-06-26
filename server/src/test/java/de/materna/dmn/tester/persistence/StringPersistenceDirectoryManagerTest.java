package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.FileHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.NoSuchFileException;

class StringPersistenceDirectoryManagerTest {
	private static PersistenceDirectoryManager<String> persistenceDirectoryManager;

	@BeforeAll
	static void beforeAll() throws IOException, URISyntaxException {
		System.setProperty("jboss.server.data.dir", FileHelper.getRootPath().toString());

		persistenceDirectoryManager = new PersistenceDirectoryManager<>("test", "test", String.class, "json");
	}

	@Test
	void fileExistsWithFile() throws IOException {
		persistenceDirectoryManager.persistFile("test", "{}");
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
		persistenceDirectoryManager.persistFile("test", "{}");
		Assertions.assertEquals("{}", persistenceDirectoryManager.getFile("test"));
		persistenceDirectoryManager.removeFile("test");
	}

	@Test
	void removeFileWithFile() throws IOException {
		persistenceDirectoryManager.persistFile("test", "{}");
		persistenceDirectoryManager.removeFile("test");
		Assertions.assertFalse(persistenceDirectoryManager.fileExists("test"));
	}

	@Test
	void removeFileWithoutFile() throws IOException {
		persistenceDirectoryManager.removeFile("test");
	}
}