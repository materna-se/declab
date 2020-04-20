package de.materna.dmn.tester.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HashingHelperTest {
	private static HashingHelper hashingHelper;

	@BeforeAll
	static void beforeAll() {
		hashingHelper = HashingHelper.getInstance();
	}

	@Test
	void saltHash() {
		String saltedHash = "FB9B521F6E1E09B9BC0C0E5614FE28A13DE17292ADF3B7B0FA953447494C2FA29B45C643730F5426B6BE1ED6707F94F693E6A39C57C85D8CE0E5C08AD3F9BB57";
		String salt = "716922C2EDDF33BD5638136A8F565C276C4849CBCD689E05B61265DF72342361F14F7B1D4700D53778B1A03CC21B5185D5706240EC97A01CF7F417BD523D9217";
		Assertions.assertEquals(saltedHash, hashingHelper.getSaltedHash("token", salt));
	}
}