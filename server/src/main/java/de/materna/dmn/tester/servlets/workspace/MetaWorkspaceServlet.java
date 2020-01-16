package de.materna.dmn.tester.servlets.workspace;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import de.materna.dmn.tester.helpers.ByteHelper;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
		} catch (StringIndexOutOfBoundsException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		} catch (Exception e) {
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

			// If required fields are not set, we will reject it.
			if (!params.containsKey("name") || !params.containsKey("description") || !params.containsKey("access") || !params.containsKey("token")) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

			String name = params.get("name");
			String description = params.get("description");
			Access access = Access.valueOf(params.get("access"));
			String token = params.get("token");
			if(name == null || description == null) {
				throw new BadRequestException();
			}
			// If the access mode does not match the token value, we will reject it.
			if ((access == Access.PROTECTED && token == null) || (access == Access.PRIVATE && token == null) || (access == Access.PUBLIC && token != null)) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			// If the token is set, we need to hash it.
			if (token != null) {
				MessageDigest md = MessageDigest.getInstance("SHA3-512");
				token = ByteHelper.byteArrayToHexString(md.digest(token.getBytes(StandardCharsets.UTF_8)));
			}

			String uuid = UUID.randomUUID().toString();

			Workspace workspace = new Workspace(uuid);
			Configuration configuration = workspace.getConfig();
			configuration.setName(name);
			configuration.setDescription(description);
			configuration.setAccess(access);
			configuration.setToken(token);
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
