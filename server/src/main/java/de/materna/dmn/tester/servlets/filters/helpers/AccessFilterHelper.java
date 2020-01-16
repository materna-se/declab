package de.materna.dmn.tester.servlets.filters.helpers;

import de.materna.dmn.tester.helpers.ByteHelper;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;

import javax.ws.rs.container.ContainerRequestContext;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessFilterHelper {
	private static final String AUTHENTICATION_SCHEME = "Bearer";

	private static Pattern pattern = Pattern.compile("/workspaces/(.*?)/");

	public static String matchPath(ContainerRequestContext requestContext) throws MalformedURLException {
		Matcher matcher = pattern.matcher(requestContext.getUriInfo().getRequestUri().toURL().getPath());
		if (!matcher.find()) {
			throw new RuntimeException();
		}

		return matcher.group(1);
	}

	/**
	 * Checks if the Authorization header is valid.
	 * It must not be null and must be prefixed with "Bearer ".
	 *
	 * @param authorizationHeader Authorization Header
	 */
	public static void validateAuthorizationHeader(Workspace workspace, String authorizationHeader) throws NoSuchAlgorithmException {
		if (authorizationHeader == null) {
			throw new RuntimeException("The authorization header is not set.");
		}

		// The authentication scheme comparison must be case-insensitive.
		if (!authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ")) {
			throw new RuntimeException("The authorization header doesn't include a bearer token.");
		}

		MessageDigest messageDigest = MessageDigest.getInstance("SHA3-512");

		String authorizationToken = authorizationHeader.substring(AUTHENTICATION_SCHEME.length() + 1);
		String tokenHash = ByteHelper.byteArrayToHexString(messageDigest.digest(authorizationToken.getBytes(StandardCharsets.UTF_8)));
		if (!tokenHash.equals(workspace.getConfig().getToken())) {
			throw new RuntimeException("The authorization token is not valid.");
		}
	}
}
