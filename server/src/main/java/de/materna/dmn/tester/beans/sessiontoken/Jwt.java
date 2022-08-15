package de.materna.dmn.tester.beans.sessiontoken;

import java.security.Key;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public abstract class Jwt {

	private static Key signingKey = new SecretKeySpec(DatatypeConverter.parseBase64Binary(
			"secretKeyForSigningIn@Declab3.0WhichShouldBeLongEnoughAfterIAddedAFewMoreLettersAndNumbersLike1234567890AndSoOn.AndWellLetsSimplyRepeat:secretKeyForSigningIn@Declab3.0WhichShouldBeLongEnoughAfterIAddedAFewMoreLettersAndNumbersLike1234567890AndSoOn"),
			SignatureAlgorithm.HS256.getJcaName());

	protected static String create(SessionToken sessionToken) {

		final Date initiation = Date.from(sessionToken.getInitiation().atZone(ZoneId.systemDefault()).toInstant());
		final Date expiration = Date.from(sessionToken.getExpiration().atZone(ZoneId.systemDefault()).toInstant());

		return Jwts.builder().setSubject(sessionToken.getUuid()).setIssuedAt(initiation).setExpiration(expiration)
				.signWith(Jwt.signingKey, SignatureAlgorithm.HS256).compact();
	}

	protected static Claims verify(String jwt) throws JwtException {
		return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(jwt).getBody();
	}
}
