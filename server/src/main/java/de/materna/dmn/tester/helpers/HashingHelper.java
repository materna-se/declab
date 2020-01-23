package de.materna.dmn.tester.helpers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingHelper {
	private static HashingHelper instance;
	private MessageDigest messageDigest;

	private HashingHelper() throws NoSuchAlgorithmException {
		messageDigest = MessageDigest.getInstance("SHA3-512");
	}

	public static synchronized HashingHelper getInstance() throws NoSuchAlgorithmException {
		if (instance == null) {
			instance = new HashingHelper();
		}
		return instance;
	}

	public String getHash(String token) {
		return byteArrayToHexString(messageDigest.digest(token.getBytes(StandardCharsets.UTF_8)));
	}

	private String byteArrayToHexString(byte[] byteArray) {
		StringBuilder stringBuilder = new StringBuilder();
		for (byte bxte : byteArray) {
			stringBuilder.append(String.format("%02X", bxte));
		}
		return stringBuilder.toString();
	}

	private byte[] hexStringToByteArray(String hexString) {
		int hexStringLength = hexString.length();

		byte[] byteArray = new byte[hexStringLength / 2];
		for (int i = 0; i < hexStringLength; i += 2) {
			byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
		}
		return byteArray;
	}
}
