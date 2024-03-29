package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.FileHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.NoSuchFileException;

class PersistenceFileManagerTest {
	private static PersistenceFileManager persistenceFileManager;

	@BeforeAll
	static void beforeAll() throws URISyntaxException {
		System.setProperty("jboss.server.data.dir", FileHelper.getRootPath().toString());

		persistenceFileManager = new PersistenceFileManager("test", "test.json");
	}

	@Test
	void fileExistsWithoutFile() {
		Assertions.assertFalse(persistenceFileManager.fileExists());
	}

	@Test
	void fileExistsWithFile() throws IOException {
		persistenceFileManager.persistFile("{}");
		Assertions.assertTrue(persistenceFileManager.fileExists());
		persistenceFileManager.removeFile();
	}

	@Test
	void getFileWithoutFile() {
		Assertions.assertThrows(NoSuchFileException.class, () -> {
			persistenceFileManager.getFile();
		});
	}

	@Test
	void getFileWithFile() throws IOException {
		persistenceFileManager.persistFile("{}");
		Assertions.assertEquals("{}", persistenceFileManager.getFile());
		persistenceFileManager.removeFile();
	}

	@Test
	void removeFileWithFile() throws IOException {
		persistenceFileManager.persistFile("{}");
		persistenceFileManager.removeFile();
		Assertions.assertFalse(persistenceFileManager.fileExists());
	}

	@Test
	void removeFileWithoutFile() throws IOException {
		persistenceFileManager.removeFile();
	}
}