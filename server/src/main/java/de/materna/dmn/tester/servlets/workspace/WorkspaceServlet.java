package de.materna.dmn.tester.servlets.workspace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.materna.dmn.tester.helpers.HashingHelper;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration.Access;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.serialization.SerializationHelper;

@Path("/workspaces/{workspace}")
public class WorkspaceServlet {

	@GET
	@Path("/public")
	@Produces("application/json")
	public Response getWorkspacePublicConfig(@PathParam("workspace") String workspaceUUID)
			throws RuntimeException, IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		Configuration configuration = workspace.getConfig();

		return Response.status(Response.Status.OK).entity(configuration.getPublicConfig().toJSON()).build();
	}

	@GET
	@WriteAccess
	@Path("/config")
	@Produces("application/json")
	public Response getWorkspaceConfig(@PathParam("workspace") String workspaceUUID)
			throws RuntimeException, IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		Configuration configuration = workspace.getConfig();

		return Response.status(Response.Status.OK).entity(configuration.toJSON()).build();
	}

	@POST
	@WriteAccess
	@Path("/config")
	@Consumes("application/json")
	public Response editWorkspaceConfig(@PathParam("workspace") String workspaceUUID, String body)
			throws RuntimeException, IOException {
		HashMap<String, String> params = SerializationHelper.getInstance().toClass(body,
				new TypeReference<HashMap<String, String>>() {
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
			if (access != Access.PUBLIC && (tempConfiguration.getToken() == null && configuration.getToken() == null)) {
				throw new BadRequestException();
			}
			tempConfiguration.setAccess(access);
		}

		if (tempConfiguration.getName() != null) {
			configuration.setName(tempConfiguration.getName());
		}
		if (tempConfiguration.getDescription() != null) {
			configuration.setDescription(tempConfiguration.getDescription());
		}
		if (tempConfiguration.getToken() != null) {
			configuration.setToken(tempConfiguration.getToken());
		}
		if (tempConfiguration.getAccess() != null) {
			configuration.setAccess(tempConfiguration.getAccess());
		}

		configuration.setModifiedDate(System.currentTimeMillis());
		configuration.serialize();

		workspace.getAccessLog().writeMessage("Edited configuration", System.currentTimeMillis());

		return Response.status(Response.Status.OK).build();
	}

	@GET
	@WriteAccess
	@Path("/log")
	@Produces("application/json")
	public Response getWorkspaceAccessLog(@PathParam("workspace") String workspaceUUID)
			throws RuntimeException, IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

		workspace.getAccessLog().writeMessage("Accessed log", System.currentTimeMillis());

		return Response.status(Response.Status.OK).entity(workspace.getAccessLog().toJSON()).build();
	}

	@DELETE
	@WriteAccess
	public Response deleteWorkspace(@PathParam("workspace") String workspaceUUID) throws IOException {
		WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
		if (!workspaceManager.has(workspaceUUID)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		workspaceManager.remove(workspaceUUID);
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@GET
	@ReadAccess
	@Path("/backup")
	@Produces("application/zip")
	public Response exportWorkspace(@PathParam("workspace") String workspaceUUID) {
		StreamingOutput streamingOutput = (OutputStream outputStream) -> {
			try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
				Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);

				Files.walk(Paths.get(workspace.getTestManager().getDirectory().getParent().toString()))
						.filter(path -> !Files.isDirectory(path)).forEach(path -> {
							try {
								if (path.endsWith("access.log")) {
									return;
								}

								if (path.endsWith("configuration.json")) {
									Configuration clonedConfiguration = (Configuration) SerializationHelper
											.getInstance()
											.toClass(SerializationHelper.getInstance().toJSON(workspace.getConfig()),
													Configuration.class);
									// Trim cloned configuration.
									clonedConfiguration.setAccess(Access.PUBLIC);
									clonedConfiguration.setToken(null);
									clonedConfiguration.setSalt(null);
									clonedConfiguration.setCreatedDate(0L);
									clonedConfiguration.setModifiedDate(0L);

									// Write trimmed copy of configuration
									zipOutputStream.putNextEntry(new ZipEntry(getRelativePath(path, workspaceUUID)));
									zipOutputStream.write(SerializationHelper.getInstance().toJSON(clonedConfiguration)
											.getBytes(StandardCharsets.UTF_8));
									zipOutputStream.closeEntry();
									return;
								}

								zipOutputStream.putNextEntry(new ZipEntry(getRelativePath(path, workspaceUUID)));
								zipOutputStream.write(Files.readAllBytes(path));
								zipOutputStream.closeEntry();
							} catch (Exception e) {
								System.err.println(e.getMessage() + " " + e);
							}
						});
			}
		};

		return Response.status(Response.Status.OK)
				.header("Content-Disposition", "attachment; filename=\"" + workspaceUUID + ".dtar\"")
				.entity(streamingOutput).build();
	}

	@PUT
	@WriteAccess
	@Path("/backup")
	@Consumes("multipart/form-data")
	public Response importWorkspace(@PathParam("workspace") String workspaceUUID,
			MultipartFormDataInput multipartFormDataInput) throws Exception {
		WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
		Workspace workspace = workspaceManager.get(workspaceUUID);

		java.nio.file.Path rootPath = workspace.getTestManager().getDirectory().getParent();

		// Recursively delete all files in workspace folder except for the configuration
		// file and the access log.
		Files.walk(Paths.get(rootPath.toString())).sorted(Comparator.reverseOrder())
				.filter(path -> !path.endsWith("configuration.json") && !path.endsWith("access.log"))
				.map(java.nio.file.Path::toFile).forEach(File::delete);

		// Iterate over all entries in the .zip archive and add them to the workspace.
		try (InputStream inputStream = multipartFormDataInput.getFormDataMap().get("backup").get(0)
				.getBody(InputStream.class, null)) {
			try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
				while (true) {
					ZipEntry zipEntry = zipInputStream.getNextEntry();
					if (zipEntry == null) {
						break;
					}

					// If the directory for the entity does not exist yet, it will be created.
					java.nio.file.Path entityPath = rootPath.resolve(zipEntry.getName());
					Files.createDirectories(entityPath.getParent());

					if (zipEntry.getName().endsWith("configuration.json")) {
						Configuration currentConfiguration = workspace.getConfig();
						Configuration importConfiguration = (Configuration) SerializationHelper.getInstance().toClass(
								new String(IOUtils.toByteArray(zipInputStream), StandardCharsets.UTF_8),
								Configuration.class);
						// Model import order needs to be merged with the current configuration.
						if (importConfiguration.getVersion() == 2) {
							currentConfiguration.setModels(importConfiguration.getModels());
							currentConfiguration.setModifiedDate(System.currentTimeMillis());
							currentConfiguration.serialize();
						}
						continue;
					}

					Files.write(entityPath, IOUtils.toByteArray(zipInputStream));
				}
			} catch (JsonMappingException e) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
		}

		workspace.getAccessLog().writeMessage("Imported workspace", System.currentTimeMillis());

		// If the workspace is cached, we need to flush it by indexing.
		workspaceManager.index(workspaceUUID);

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
