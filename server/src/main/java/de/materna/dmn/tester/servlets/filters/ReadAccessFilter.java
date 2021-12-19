package de.materna.dmn.tester.servlets.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.helpers.AccessFilterHelper;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration.Access;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;

@ReadAccess
@Provider
@Priority(Priorities.AUTHENTICATION)
public class ReadAccessFilter implements ContainerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(ReadAccessFilter.class);
	private static WorkspaceManager workspaceManager;

	public ReadAccessFilter() throws IOException {
		workspaceManager = WorkspaceManager.getInstance();
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {
		Workspace workspace = workspaceManager.get(AccessFilterHelper.matchPath(requestContext));
		if (workspace.getConfig().getAccess() != Access.PRIVATE) {
			return;
		}

		AccessFilterHelper.validateAuthorizationHeader(workspace,
				requestContext.getHeaderString(HttpHeaders.AUTHORIZATION));
	}
}