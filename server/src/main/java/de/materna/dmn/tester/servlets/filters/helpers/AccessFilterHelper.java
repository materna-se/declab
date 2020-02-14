package de.materna.dmn.tester.servlets.filters.helpers;

import de.materna.dmn.tester.helpers.HashingHelper;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessFilterHelper {
	private static final String AUTHENTICATION_SCHEME = "Bearer";

	private static Pattern pattern = Pattern.compile("/workspaces/([a-z0-9\\-]+)");

	public static String matchPath(ContainerRequestContext requestContext) {
		try {
			Matcher matcher = pattern.matcher(requestContext.getUriInfo().getRequestUri().toURL().getPath());
			if (!matcher.find()) {
				throw new BadRequestException();
			}

			return matcher.group(1);
		}
		catch (MalformedURLException e) {
			throw new BadRequestException();
		}
	}

	/**
	 * Checks if the Authorization header is valid.
	 * It must not be null and must be prefixed with "Bearer ".
	 *
	 * @param authorizationHeader Authorization Header
	 */
	public static void validateAuthorizationHeader(Workspace workspace, String authorizationHeader) throws NoSuchAlgorithmException {
		if (authorizationHeader == null) {
			throw new NotAuthorizedException("The authorization header is not set.");
		}

		// The authentication scheme comparison must be case-insensitive.
		if (!authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ")) {
			throw new NotAuthorizedException("The authorization header doesn't include a bearer token.");
		}

		String authorizationToken = authorizationHeader.substring(AUTHENTICATION_SCHEME.length() + 1);
		String salt = workspace.getConfig().getSalt();
		String tokenHash = HashingHelper.getInstance().getSaltedHash(authorizationToken, salt);
		if (!tokenHash.equals(workspace.getConfig().getToken())) {
			throw new NotAuthorizedException("The authorization token is not valid.");
		}
	}
}
