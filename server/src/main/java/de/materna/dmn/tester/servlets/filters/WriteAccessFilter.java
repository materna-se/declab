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
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@WriteAccess
@Provider
@Priority(Priorities.AUTHENTICATION)
public class WriteAccessFilter implements ContainerRequestFilter {
	private static final Logger log = Logger.getLogger(WriteAccessFilter.class);
	private static WorkspaceManager workspaceManager;

	public WriteAccessFilter() throws IOException {
		workspaceManager = WorkspaceManager.getInstance();
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {
		Workspace workspace = workspaceManager.get(AccessFilterHelper.matchPath(requestContext));
		if (workspace.getConfig().getAccess() == Access.PUBLIC) {
			return;
		}

		AccessFilterHelper.validateAuthorizationHeader(workspace, requestContext.getHeaderString(HttpHeaders.AUTHORIZATION));
	}
}
