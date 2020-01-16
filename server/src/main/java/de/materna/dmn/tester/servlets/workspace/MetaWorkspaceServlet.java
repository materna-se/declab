package de.materna.dmn.tester.servlets.workspace;

import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.serialization.SerializationHelper;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration.Access;
import de.materna.dmn.tester.helpers.ByteHelper;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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
			Map<String, Workspace> workspaces = new HashMap<String, Workspace>();
			
			if(query == null || query.length() == 0) {
				workspaces = WorkspaceManager.getInstance().getWorkspaces();
			} else {
				workspaces = WorkspaceManager.getInstance().getByName(query);
			}
			
			Map<String, PublicConfiguration> pubconfigs = new HashMap<String, PublicConfiguration>();
			
			//Get public configurations
			for (Map.Entry<String, Workspace> entry : workspaces.entrySet()) {
				Configuration config = entry.getValue().getConfig();
				pubconfigs.put(entry.getKey(), config.getPublicConfig());
			}
			
			String ret = SerializationHelper.getInstance().toJSON(pubconfigs);
			
			return Response.status(Response.Status.OK).entity(ret).build();
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
			HashMap<String, String> params = SerializationHelper.getInstance().toClass(body, new TypeReference<HashMap<String, String>>() {});

			if(params == null || params.size() == 0 || !params.containsKey("name") || params.get("name").length() == 0
					|| !params.containsKey("description") || !params.containsKey("access") || !params.containsKey("token")) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

			String name = params.get("name");
			String description = params.get("description");
			Access access = Access.valueOf(params.get("access"));
			String token = params.get("token");

			if(name == null || description == null || access == null || (token != null && token.length() == 0)) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			
			if((access == Access.PROTECTED && token == null) || (access == Access.PRIVATE && token == null) || (access == Access.PUBLIC && token != null)) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

			if(token != null) {
				MessageDigest md = MessageDigest.getInstance("SHA3-512");
				token = ByteHelper.byteArrayToHexString(md.digest(token.getBytes("UTF-8")));
			}

			//Assign UUID
			String workspaceUUID = UUID.randomUUID().toString();

			//Create workspace
			Workspace newWorkspace = new Workspace(workspaceUUID);
			Configuration newConfiguration = newWorkspace.getConfig();
			newConfiguration.setName(name);
			newConfiguration.setDescription(description);
			newConfiguration.setAccess(access);
			newConfiguration.setToken(token);
			newConfiguration.setCreatedDate(System.currentTimeMillis());
			newConfiguration.setModifiedDate(newWorkspace.getConfig().getCreatedDate());
			newConfiguration.serialize();

			//Index workspace
			WorkspaceManager.getInstance().getWorkspaces().put(workspaceUUID, newWorkspace);

			//Return UUID
			return Response.status(Response.Status.OK).entity(workspaceUUID).build();
		} catch (JsonParseException  | JsonMappingException | IllegalArgumentException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		} catch (IOException  | NoSuchAlgorithmException e) {
			log.error(e);
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
}
