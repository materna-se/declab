package de.materna.dmn.tester.servlets.workspace;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import de.materna.dmn.tester.helpers.HashingHelper;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration.Access;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.model.ModelImportException;
import de.materna.jdec.serialization.SerializationHelper;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Path("/workspaces/{workspace}")
public class WorkspaceServlet {
	private static final Logger log = Logger.getLogger(WorkspaceServlet.class);

	@GET
	@Path("/public")
	@Produces("application/json")
	public Response getWorkspacePublicConfig(@PathParam("workspace") String workspaceUUID) throws RuntimeException {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			Configuration configuration = workspace.getConfig();

			return Response.status(Response.Status.OK).entity(configuration.getPublicConfig().toJson()).build();
		}
		catch (Exception e) {
			log.error(e);
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@GET
	@WriteAccess
	@Path("/config")
	@Produces("application/json")
	public Response getWorkspaceConfig(@PathParam("workspace") String workspaceUUID) throws RuntimeException {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			Configuration configuration = workspace.getConfig();

			return Response.status(Response.Status.OK).entity(configuration.toJson()).build();
		}
		catch (Exception e) {
			log.error(e);
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@POST
	@WriteAccess
	@Path("/config")
	@Consumes("application/json")
	public Response editWorkspaceConfig(@PathParam("workspace") String workspaceUUID, String body) throws RuntimeException, IOException {
		try {
			HashMap<String, String> params = SerializationHelper.getInstance().toClass(body, new TypeReference<HashMap<String, String>>() {
			});

			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
			Configuration configuration = workspace.getConfig();
			
			// Store changes in temporary configuration to avoid situations where a change
			// to the configuration object has been made but not logged or serialized.
			Configuration tempConfiguration = new Configuration();

			// The name is optional, we will set it if the value is valid.
			if (params.containsKey("name")) {
				String name = params.get("name");
				if (name == null || name.length() == 0) {
					throw new BadRequestException();
				}
				tempConfiguration.setName(name);
			}

			// The description is optional, we will set it if the value is valid.
			if (params.containsKey("description")) {
				String description = params.get("description");
				if (description == null) {
					throw new BadRequestException();
				}
				tempConfiguration.setDescription(description);
			}

			// The description is optional, we will set it if the value is valid.
			if (params.containsKey("description")) {
				String description = params.get("description");
				if (description == null) {
					throw new BadRequestException();
				}
				tempConfiguration.setDescription(description);
			}

			// The token is optional if the access mode is set to public.
			if (params.containsKey("token")) {
				String token = params.get("token");
				if (token == null || token.length() == 0) {
					throw new BadRequestException();
				}
				tempConfiguration.setToken(HashingHelper.getInstance().getSaltedHash(token, configuration.getSalt()));
			}

			// The access mode is optional, we will set it if the value is valid.
			if (params.containsKey("access")) {
				Access access = Access.valueOf(params.get("access"));
				if(access != Access.PUBLIC && (tempConfiguration.getToken() == null && configuration.getToken() == null)) {
					throw new BadRequestException();
				}
				tempConfiguration.setAccess(access);
			}

			if(tempConfiguration.getName() != null) {
				configuration.setName(tempConfiguration.getName());
			}
			if(tempConfiguration.getDescription() != null) {
				configuration.setDescription(tempConfiguration.getDescription());
			}
			if(tempConfiguration.getToken() != null) {
				configuration.setToken(tempConfiguration.getToken());
			}
			if(tempConfiguration.getAccess() != null) {
				configuration.setAccess(tempConfiguration.getAccess());
			}

			configuration.setModifiedDate(System.currentTimeMillis());
			configuration.serialize();

			workspace.getAccessLog().writeMessage("Edited configuration", System.currentTimeMillis());

			return Response.status(Response.Status.OK).build();
		}
		catch (IllegalArgumentException | BadRequestException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		catch (Exception e) {
			log.error(e);
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@GET
	@WriteAccess
	@Path("/log")
	@Produces("application/json")
	public Response getWorkspaceAccessLog(@PathParam("workspace") String workspaceUUID) throws RuntimeException, IOException {
		try {
			Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

			workspace.getAccessLog().writeMessage("Accessed log", System.currentTimeMillis());

			return Response.status(Response.Status.OK).entity(workspace.getAccessLog().toJson()).build();
		}
		catch (Exception e) {
			log.error(e);
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@DELETE
	@WriteAccess
	@Path("")
	public Response deleteWorkspace(@PathParam("workspace") String workspaceUUID) {
		try {
			WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
			if (!workspaceManager.has(workspaceUUID)) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}

			workspaceManager.remove(workspaceUUID);
			return Response.status(Response.Status.NO_CONTENT).build();
		}
		catch (Exception e) {
			log.error(e);
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@GET
	@ReadAccess
	@Path("/backup")
	@Produces("application/zip")
	public Response exportWorkspace(@PathParam("workspace") String workspaceUUID) {
		StreamingOutput streamingOutput = (OutputStream outputStream) -> {
			try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
				Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

				Files.walk(Paths.get(workspace.getTestManager().getDirectory().getParent().toString())).filter(path -> !Files.isDirectory(path)).forEach(path -> {
					try {
						zipOutputStream.putNextEntry(new ZipEntry(getRelativePath(path, workspaceUUID)));
						zipOutputStream.write(Files.readAllBytes(path));
						zipOutputStream.closeEntry();
					}
					catch (Exception e) {
						log.error(e);
					}
				});
			}
		};

		return Response.status(Response.Status.OK).header("Content-Disposition", "attachment; filename=\"" + workspaceUUID + ".dtar\"").entity(streamingOutput).build();
	}

	@PUT
	@WriteAccess
	@Path("/backup")
	@Consumes("multipart/form-data")
	public Response importWorkspace(@PathParam("workspace") String workspaceUUID, MultipartFormDataInput multipartFormDataInput) throws IOException {
		WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
		Workspace workspace = workspaceManager.get(workspaceUUID);

		// If the workspace does not exist yet, it will be created.
		java.nio.file.Path path = workspace.getTestManager().getDirectory().getParent();
		Files.createDirectories(path);

		InputPart inputPart = multipartFormDataInput.getFormDataMap().get("backup").get(0);
		try (InputStream inputStream = inputPart.getBody(InputStream.class, null)) {
			try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

				while (true) {
					ZipEntry zipEntry = zipInputStream.getNextEntry();
					if (zipEntry == null) {
						break;
					}

					// If the directory for the entity does not exist yet, it will be created.
					java.nio.file.Path path1 = Paths.get(path.toString(), zipEntry.getName());
					Files.createDirectories(path1.getParent());

					Files.write(path1, IOUtils.toByteArray(zipInputStream));
				}
			}
		}

		// If the workspace is cached, we need to overwrite it by indexing.
		workspaceManager.index(workspaceUUID);

		try {
			workspace.getDecisionSession().importModel("main", "main", workspace.getModelManager().getFile());
		}
		catch (ModelImportException exception) {
			return Response.status(Response.Status.BAD_REQUEST).entity(SerializationHelper.getInstance().toJSON(exception.getResult())).build();
		}
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	private String getRelativePath(java.nio.file.Path path, String workspaceName) {
		String pathName = path.getFileName().toString();
		String parentPathName = path.getParent().getFileName().toString();
		if (parentPathName.equals(workspaceName)) {
			return pathName;
		}
		return getRelativePath(path.getParent(), workspaceName) + File.separator + pathName;
	}
}