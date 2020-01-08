package de.materna.dmn.tester.helpers;

public class ByteHelper {
	public static String byteArrayToHexString(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for(byte aa : b) {
			sb.append(String.format("%02X", aa));
		}
		String ret = sb.toString();
		sb.setLength(0);
		return ret;
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] ret = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        ret[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
								+ Character.digit(s.charAt(i+1), 16));
	    }
	    return ret;
	}
}
