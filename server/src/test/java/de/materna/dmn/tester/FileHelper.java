package de.materna.dmn.tester;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;

public class FileHelper {
	public static Path getRootPath() throws URISyntaxException {
		return new File(Thread.currentThread().getContextClassLoader().getResource("log4j.properties").toURI()).getParentFile().toPath();
	}

	public static String readFile(String ... args) throws URISyntaxException, IOException {
		return new String(readFileRaw(args), StandardCharsets.UTF_8);
	}
	
	public static byte[] readFileRaw(String ... args) throws URISyntaxException, IOException {
		Path filePath = getRootPath().resolve("scenarios");
		for (String part : args) {
			filePath = filePath.resolve(part);
		}
		File file = filePath.toFile();
		
		Assertions.assertTrue(file.exists() && file.isFile() && file.canRead());
		return Files.readAllBytes(file.toPath());
	}
}
