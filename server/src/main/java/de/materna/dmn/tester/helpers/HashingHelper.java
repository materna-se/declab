package de.materna.dmn.tester.helpers;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3;

public class HashingHelper {
	private static HashingHelper instance;
	private SHA3.Digest512 messageDigest;

	private HashingHelper() {
		messageDigest = new SHA3.Digest512();
	}

	public static synchronized HashingHelper getInstance() {
		if (instance == null) {
			instance = new HashingHelper();
		}
		return instance;
	}

	public String generateSalt() {
		byte[] saltBytes = new byte[64];
		new SecureRandom().nextBytes(saltBytes);
		return byteArrayToHexString(saltBytes);
	}

	public String getSaltedHash(String token, String salt) {
		return byteArrayToHexString(messageDigest
				.digest(ArrayUtils.addAll(token.getBytes(StandardCharsets.UTF_8), hexStringToByteArray(salt))));
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
			byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
					+ Character.digit(hexString.charAt(i + 1), 16));
		}
		return byteArray;
	}
}
