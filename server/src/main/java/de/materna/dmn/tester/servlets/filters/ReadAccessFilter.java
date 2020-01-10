package de.materna.dmn.tester.servlets.filters;

import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration.Access;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import de.materna.dmn.tester.helpers.ByteHelper;
import de.materna.dmn.tester.persistence.WorkspaceManager;

@ReadAccess
@Provider
@Priority(Priorities.AUTHENTICATION)
public class ReadAccessFilter implements ContainerRequestFilter {
    private static final String REALM = "example";
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private static final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the workspace name from the request
    	String urlString = requestContext.getUriInfo().getRequestUri().toURL().getPath();
        Pattern pattern = Pattern.compile("api/workspaces/(.*?)/");
        Matcher matcher = pattern.matcher(urlString);
        if(!matcher.find()) {
        	throw new RuntimeException();
        }
        String workspaceName = matcher.group(1);
        
        //Get workspace object
        Workspace workspace = workspaceManager.get(workspaceName);
        
        //Check if authorization is necessary
        if(workspace.getConfig().getMode() == Access.PRIVATE) {
        	
            // Get the Authorization header from the request
            String authorizationHeader =
                    requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

            // Validate the Authorization header
            if (!isTokenBasedAuthentication(authorizationHeader)) {
                abortWithUnauthorized(requestContext);
                return;
            }

            // Extract the token from the Authorization header
            String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

            try {
                // Validate the token
                validateToken(token, workspace);
            } catch (Exception e) {
                abortWithUnauthorized(requestContext);
            }
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                    .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE, 
                                AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                        .build());
    }

    private void validateToken(String token, Workspace workspace) throws Exception {
    	MessageDigest md = MessageDigest.getInstance("SHA-512");
    	String tokenHash = ByteHelper.byteArrayToHexString(md.digest(token.getBytes("UTF-8")));
    	if(!tokenHash.contentEquals(workspace.getConfig().getToken())) {
    		throw new RuntimeException();
    	}
    }

}
