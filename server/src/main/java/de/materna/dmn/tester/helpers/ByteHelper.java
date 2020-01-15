package de.materna.dmn.tester.helpers;

public class ByteHelper {
	public static String byteArrayToHexString(byte[] byteArray) {
		StringBuilder stringBuilder = new StringBuilder();
		for (byte bxte : byteArray) {
			stringBuilder.append(String.format("%02X", bxte));
		}
		return stringBuilder.toString();
	}

	public static byte[] hexStringToByteArray(String hexString) {
		int hexStringLength = hexString.length();

		byte[] byteArray = new byte[hexStringLength / 2];
		for (int i = 0; i < hexStringLength; i += 2) {
			byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
		}
		return byteArray;
	}
}