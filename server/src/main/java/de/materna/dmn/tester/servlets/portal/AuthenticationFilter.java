package de.materna.dmn.tester.servlets.portal;

import java.io.IOException;
import java.time.LocalDate;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import de.materna.dmn.tester.beans.sessiontoken.SessionToken;
import de.materna.dmn.tester.beans.sessiontoken.repository.SessionTokenHibernateH2RepositoryImpl;
import de.materna.dmn.tester.interfaces.Secured;
import de.materna.dmn.tester.servlets.exceptions.authorization.SessionTokenExpiredException;
import de.materna.dmn.tester.servlets.exceptions.database.SessionTokenNotFoundException;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	private static final String REALM = "declab";
	private static final String AUTHENTICATION_SCHEME = "Bearer";

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
		} catch (SessionTokenNotFoundException | SessionTokenExpiredException e) {
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
		SessionToken token = new SessionTokenHibernateH2RepositoryImpl().findBySessionToken(tokenString);
		if (token == null)
			throw new SessionTokenNotFoundException("SessionToken not found : " + tokenString);
		if (token.getExpiration().isBefore(LocalDate.now())) {
			throw new SessionTokenExpiredException("SessionToken expired : " + tokenString);
		}
	}
}