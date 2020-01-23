package de.materna.dmn.tester.servlets.workspace;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import de.materna.dmn.tester.helpers.HashingHelper;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration.Access;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.serialization.SerializationHelper;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Path("/workspaces")
public class MetaWorkspaceServlet {
	private static final Logger log = Logger.getLogger(MetaWorkspaceServlet.class);

	@GET
	@Path("")
	@Produces("application/json")
	public Response getWorkspaces(@QueryParam("query") String query) {
		try {
			Map<String, PublicConfiguration> configurations = new LinkedHashMap<>();

			Map<String, Workspace> workspaces = query == null ? WorkspaceManager.getInstance().getWorkspaces() : WorkspaceManager.getInstance().getByName(query);
			for (Map.Entry<String, Workspace> entry : workspaces.entrySet()) {
				configurations.put(entry.getKey(), entry.getValue().getConfig().getPublicConfig());
			}

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(configurations)).build();
		}
		catch (StringIndexOutOfBoundsException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@POST
	@Path("")
	@Consumes("application/json")
	@Produces("application/json")
	public Response createWorkspace(String body) {
		try {
			HashMap<String, String> params = SerializationHelper.getInstance().toClass(body, new TypeReference<HashMap<String, String>>() {
			});

			String uuid = UUID.randomUUID().toString();

			Workspace workspace = new Workspace(uuid);
			Configuration configuration = workspace.getConfig();

			// If required fields are not set, we will reject it.
			String name = params.get("name");
			if (name == null || name.length() == 0) {
				throw new BadRequestException();
			}
			configuration.setName(name);

			String description = params.get("description");
			if (description == null || description.length() == 0) {
				throw new BadRequestException();
			}
			configuration.setDescription(description);

			// Invalid combinations of access mode and token are not allowed.
			{
				Access access = null;
				String token = null;

				if (params.containsKey("access")) {
					access = Access.valueOf(params.get("access"));
				}

				if (params.containsKey("token")) {
					token = params.get("token");
					if (token == null || token.length() == 0) {
						throw new BadRequestException();
					}
				}

				// If the access mode does not match the token value, we will reject it.
				if ((access == null && token != null) || ((access == Access.PROTECTED || access == Access.PRIVATE) && token == null)) {
					throw new BadRequestException();
				}

				if (access != null) {
					configuration.setAccess(access);
				}
				if (token != null) {
					configuration.setToken(HashingHelper.getInstance().getHash(token));
				}
			}

			configuration.setCreatedDate(System.currentTimeMillis());
			configuration.setModifiedDate(configuration.getCreatedDate());
			configuration.serialize();

			WorkspaceManager.getInstance().index();

			return Response.status(Response.Status.OK).entity(uuid).build();
		}
		catch (JsonParseException | JsonMappingException | IllegalArgumentException | BadRequestException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		catch (IOException | NoSuchAlgorithmException e) {
			log.error(e);
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
}
