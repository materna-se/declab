package de.materna.dmn.tester.persistence;

import de.materna.dmn.tester.TestExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

@ExtendWith({TestExtension.class})
class PersistenceFileManagerTest {
	private PersistenceFileManager persistenceFileManager = new PersistenceFileManager("test", "test.json");

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