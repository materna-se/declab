package de.materna.dmn.tester.servlets.workspace;

import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration;
import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.dmn.tester.servlets.workspace.beans.PublicConfiguration.Access;
import de.materna.jdec.model.ModelImportException;
import de.materna.jdec.serialization.SerializationHelper;
import de.materna.dmn.tester.helpers.ByteHelper;
import de.materna.dmn.tester.persistence.WorkspaceManager;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Path("/workspaces/{workspace}")
public class WorkspaceServlet {
	private static final Logger log = Logger.getLogger(WorkspaceServlet.class);
	
	@GET
	@Path("/public")
	@Produces("application/json")
	public Response getWorkspacePublicConfig(@PathParam("workspace") String workspaceUUID) throws RuntimeException, IOException {
		try {
			Workspace workspace = WorkspaceManager.getInstance().getByUUID(workspaceUUID);
			Configuration configuration = workspace.getConfig();
			
			return Response.status(Response.Status.OK).entity(configuration.getPublicConfig().printAsJson()).build();
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
	public Response getWorkspaceConfig(@PathParam("workspace") String workspaceUUID) throws RuntimeException, IOException {
		try {
			Workspace workspace = WorkspaceManager.getInstance().getByUUID(workspaceUUID);
			Configuration configuration = workspace.getConfig();
			
			return Response.status(Response.Status.OK).entity(configuration.printAsJson()).build();
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
			HashMap<String,String> params = SerializationHelper.getInstance().toClass(body, new TypeReference<HashMap<String, String>>() {});
			
			//Reject request if no changes are requested
			if(params == null || params.size() == 0) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			
			Workspace workspace = WorkspaceManager.getInstance().getByUUID(workspaceUUID);
			
			Configuration config = workspace.getConfig();
			
			if(params.containsKey("name") && params.get("name") != null && params.get("name").length() > 0) {
				config.setName(params.get("name"));
			}
			if(params.containsKey("description") && params.get("description") != null) {
				config.setDescription(params.get("description"));
			}
			
			//Do not allow invalid combinations of access and token
			//If one parameter is passed, the other must be passed too
			Access accessTemp = null;
			String tokenTemp = null;
			
			if(params.containsKey("access")) {
				accessTemp = Access.valueOf(params.get("access"));
			}
			
			if(params.containsKey("token")) {
				if(params.get("token") != null && params.get("token").length() > 0) {
					MessageDigest md = MessageDigest.getInstance("SHA3-512");
					tokenTemp = ByteHelper.byteArrayToHexString(md.digest(params.get("token").getBytes("UTF-8")));
				} else {
					config.setModifiedDate(System.currentTimeMillis());
					config.serialize();
					return Response.status(Response.Status.BAD_REQUEST).build();
				}
			}
			
			if((accessTemp == null && tokenTemp != null) || (accessTemp != Access.PUBLIC && tokenTemp == null)) {
				config.setModifiedDate(System.currentTimeMillis());
				config.serialize();
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			
			config.setAccess(accessTemp);
			config.setToken(tokenTemp);
			config.setModifiedDate(System.currentTimeMillis());
			config.serialize();
			
			workspace.getAccessLog().writeMessage("Edited configuration", System.currentTimeMillis());
			
			return Response.status(Response.Status.OK).build();
		} catch (IllegalArgumentException | JsonParseException | JsonMappingException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		} catch (Exception e) {
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
			Workspace workspace = WorkspaceManager.getInstance().getByUUID(workspaceUUID);
			
			String ret = workspace.getAccessLog().print();
			
			workspace.getAccessLog().writeMessage("Accessed log", System.currentTimeMillis());
			
			return Response.status(Response.Status.OK).entity(ret).build();
		} catch (Exception e) {
			log.error(e);
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}
	
	@DELETE
	@WriteAccess
	@Path("")
	public Response deleteWorkspace(@PathParam("workspace") String workspaceUUID) throws IOException {
		try {
			WorkspaceManager wm = WorkspaceManager.getInstance();
			if(wm.workspaceExists(workspaceUUID)) {
				wm.remove(workspaceUUID);
				return Response.status(Response.Status.NO_CONTENT).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			
		} catch (Exception e) {
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
				Workspace workspace = WorkspaceManager.getInstance().getByUUID(workspaceUUID);

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
		Workspace workspace = workspaceManager.getByUUID(workspaceUUID);

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

		// If the workspace is cached, we need to invalidate it.
		workspaceManager.invalidate(workspaceUUID);

		try {
			workspace.getDecisionSession().importModel("main", "main", workspace.getModelManager().getFile());
		}
		catch (ModelImportException exception) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(SerializationHelper.getInstance().toJSON(exception.getResult())).build();
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