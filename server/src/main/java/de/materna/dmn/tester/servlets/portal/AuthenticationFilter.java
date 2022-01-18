package de.materna.dmn.tester.servlets.portal;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.sessiontoken.SessionTokenRepository;
import de.materna.dmn.tester.interfaces.Secured;
import de.materna.dmn.tester.servlets.exceptions.authorization.AuthorizationFailureException;
import de.materna.dmn.tester.servlets.exceptions.authorization.SessionTokenExpiredException;
import de.materna.dmn.tester.servlets.exceptions.authorization.SessionTokenNotFoundException;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	private static final String REALM = "example";
	private static final String AUTHENTICATION_SCHEME = "declab";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (!isTokenBasedAuthentication(authorizationHeader)) {
			abortWithUnauthorized(requestContext);
			return;
		}
		try {
			String tokenString = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
			validateToken(tokenString);
		} catch (AuthorizationFailureException e) {
			abortWithUnauthorized(requestContext);
		}
	}

	private boolean isTokenBasedAuthentication(String authorizationHeader) {
		return authorizationHeader != null
				&& authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
	}

	private void abortWithUnauthorized(ContainerRequestContext requestContext) {
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
				.header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"").build());
	}

	private void validateToken(String tokenString) throws SessionTokenNotFoundException, SessionTokenExpiredException {
		SessionToken token = new SessionTokenRepository().findByToken(tokenString);
		if (token == null)
			throw new SessionTokenNotFoundException("SessionToken not found : " + tokenString);
		if (token.getExpiration().isBefore(LocalDateTime.now())) {
			throw new SessionTokenExpiredException("SessionToken expired : " + tokenString);
		}
	}
}