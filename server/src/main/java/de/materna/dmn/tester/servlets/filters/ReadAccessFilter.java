package de.materna.dmn.tester.servlets.filters;

import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.helpers.AccessFilterHelper;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration.Access;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import org.apache.log4j.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@ReadAccess
@Provider
@Priority(Priorities.AUTHENTICATION)
public class ReadAccessFilter implements ContainerRequestFilter {
	private static final Logger log = Logger.getLogger(ReadAccessFilter.class);
	private static final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();

	@Override
	public void filter(ContainerRequestContext requestContext) {
		try {
			Workspace workspace = workspaceManager.get(AccessFilterHelper.matchPath(requestContext));
			if (workspace == null) {
				requestContext.abortWith(Response.status(Response.Status.NOT_FOUND).build());
				return;
			}
			if (workspace.getConfig().getAccess() != Access.PRIVATE) {
				return;
			}

			AccessFilterHelper.validateAuthorizationHeader(workspace, requestContext.getHeaderString(HttpHeaders.AUTHORIZATION));
		}
		catch (Exception e) {
			log.error(e);

			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}
}
