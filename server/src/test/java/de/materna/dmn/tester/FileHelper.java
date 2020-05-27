package de.materna.dmn.tester;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;

public class FileHelper {
	public static String readFile(String ... args) throws URISyntaxException, IOException {
		String ret = new String(readFileRaw(args), "UTF-8");
		return ret;
	}
	
	public static byte[] readFileRaw(String ... args) throws URISyntaxException, IOException {
		Path rootPath = TestHelper.getRootPath();
		
		Path filePath = rootPath.resolve("scenarios");
		for (String part : args) {
			filePath = filePath.resolve(part);
		}
		File file = filePath.toFile();
		
		Assertions.assertTrue(file.exists() && file.isFile() && file.canRead());
		return Files.readAllBytes(file.toPath());
	}
}
