package de.materna.dmn.tester.servlets.filters;

import de.materna.dmn.tester.helpers.ByteHelper;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.helpers.AccessFilterHelper;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration.Access;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import org.apache.log4j.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.MessageDigest;

@WriteAccess
@Provider
@Priority(Priorities.AUTHENTICATION)
public class WriteAccessFilter implements ContainerRequestFilter {
    private static final Logger log = Logger.getLogger(ReadAccessFilter.class);
    private static final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        try {
            if (workspace.getConfig().getMode() != Access.PRIVATE && workspace.getConfig().getMode() != Access.PROTECTED) {
            Workspace workspace = workspaceManager.getByUUID(AccessFilterHelper.matchPath(requestContext));
            if(workspace == null) {
            	requestContext.abortWith(Response.status(Response.Status.NOT_FOUND).build());
            	return;
            }
                return;
            }

            AccessFilterHelper.validateAuthorizationHeader(workspace, requestContext.getHeaderString(HttpHeaders.AUTHORIZATION));
        }
        catch (Exception e) {
            log.error(e.getMessage());

            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
