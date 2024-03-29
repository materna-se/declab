package de.materna.dmn.tester.servlets.workspace;

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
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
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

@Path("/workspaces/{workspace}")
public class WorkspaceServlet {
	private static final Logger log = LoggerFactory.getLogger(WorkspaceServlet.class);

	@GET
	@Path("/public")
	@Produces("application/json")
	public Response getWorkspacePublicConfig(@PathParam("workspace") String workspaceUUID) throws RuntimeException, IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		Configuration configuration = workspace.getConfig();

		return Response.status(Response.Status.OK).entity(configuration.getPublicConfig().toJSON()).build();
	}

	@GET
	@WriteAccess
	@Path("/config")
	@Produces("application/json")
	public Response getWorkspaceConfig(@PathParam("workspace") String workspaceUUID) throws RuntimeException, IOException {
		Workspace workspace = WorkspaceManager.getInstance().get(workspaceUUID);
		Configuration configuration = workspace.getConfig();

		return Response.status(Response.Status.OK).entity(configuration.toJSON()).build();
	}

	@POST
	@WriteAccess
	@Path("/config")
	@Consumes("application/json")
	public Response editWorkspaceConfig(@PathParam("workspace") String workspaceUUID, String body) throws RuntimeException, IOException {
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
	public Response getWorkspaceAccessLog(@PathParam("workspace") String workspaceUUID) throws RuntimeException, IOException {
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

				Files.walk(Paths.get(workspace.getTestManager().getDirectory().getParent().toString())).forEach(path -> {
					try {
						String relativePath = getRelativePath(path, workspaceUUID);
						if (relativePath == null) {
							return;
						}

						if (Files.isDirectory(path)) {
							zipOutputStream.putNextEntry(new ZipEntry(relativePath + "/"));
							zipOutputStream.closeEntry();
							return;
						}

						if (path.endsWith("access.log")) {
							// We don't want to export the access log.
							return;
						}

						if (path.endsWith("configuration.json")) {
							// We don't want to export all key-value pairs of the configuration.

							Configuration clonedConfiguration = (Configuration) SerializationHelper.getInstance().toClass(SerializationHelper.getInstance().toJSON(workspace.getConfig()), Configuration.class);
							clonedConfiguration.setAccess(Access.PUBLIC);
							clonedConfiguration.setToken(null);
							clonedConfiguration.setSalt(null);
							clonedConfiguration.setCreatedDate(0L);
							clonedConfiguration.setModifiedDate(0L);

							zipOutputStream.putNextEntry(new ZipEntry(relativePath));
							zipOutputStream.write(SerializationHelper.getInstance().toJSON(clonedConfiguration).getBytes(StandardCharsets.UTF_8));
							zipOutputStream.closeEntry();
							return;
						}

						zipOutputStream.putNextEntry(new ZipEntry(relativePath));
						zipOutputStream.write(Files.readAllBytes(path));
						zipOutputStream.closeEntry();
					}
					catch (Exception e) {
						log.error(e.getMessage(), e);
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
	public Response importWorkspace(@PathParam("workspace") String workspaceUUID, FormDataMultiPart multiPart) throws Exception {
		WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
		Workspace workspace = workspaceManager.get(workspaceUUID);

		java.nio.file.Path rootPath = workspace.getTestManager().getDirectory().getParent();

		// Recursively delete all files in workspace folder except for the configuration file and the access log.
		Files.walk(Paths.get(rootPath.toString())).sorted(Comparator.reverseOrder()).filter(path -> !path.endsWith("configuration.json") && !path.endsWith("access.log")).map(java.nio.file.Path::toFile).forEach(File::delete);

		// Iterate over all entries in the .zip archive and add them to the workspace.
		try (InputStream inputStream = multiPart.getField("backup").getValueAs(InputStream.class)) {
			try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
				while (true) {
					ZipEntry zipEntry = zipInputStream.getNextEntry();
					if (zipEntry == null) {
						break;
					}

					String relativePath = zipEntry.getName();
					// The .zip archive always contains separators of the operating system on which they were created.
					// We need to normalize them.
					if(File.separator.equals("/")) {
						relativePath = relativePath.replace("\\", "/");
					}
					else {
						relativePath = relativePath.replace("/", "\\");
					}

					// If the directory for the entity does not exist yet, it will be created.
					java.nio.file.Path absolutePath = rootPath.resolve(relativePath);
					if (zipEntry.isDirectory()) {
						Files.createDirectories(absolutePath);
						continue;
					}

					// Normally, the parent directory should already exist. We'll check it anyway.
					Files.createDirectories(absolutePath.getParent());

					if (relativePath.endsWith("configuration.json")) {
						Configuration currentConfiguration = workspace.getConfig();
						Configuration importConfiguration = (Configuration) SerializationHelper.getInstance().toClass(new String(IOUtils.toByteArray(zipInputStream), StandardCharsets.UTF_8), Configuration.class);
						// Model import order needs to be merged with the current configuration.
						if (importConfiguration.getVersion() == 2) {
							currentConfiguration.setModels(importConfiguration.getModels());
							currentConfiguration.setModifiedDate(System.currentTimeMillis());
							currentConfiguration.serialize();
						}
						continue;
					}

					Files.write(absolutePath, IOUtils.toByteArray(zipInputStream));
				}
			}
			catch (JsonMappingException e) {
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
		// If we walk through the workspace directory, one entry is the workspace directory itself.
		// We don't want to add an entry for that.
		if (pathName.equals(workspaceName)) {
			return null;
		}

		String parentPathName = path.getParent().getFileName().toString();
		// We've reached the destination and can return the path.
		if (parentPathName.equals(workspaceName)) {
			return pathName;
		}
		return getRelativePath(path.getParent(), workspaceName) + File.separator + pathName;
	}
}