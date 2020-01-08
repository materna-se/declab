package de.materna.dmn.tester.servlets.workspace;

import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.filters.WriteAccess;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

//TODO Add log messages

@Path("/workspaces/{workspace}")
public class WorkspaceServlet {
	private static final Logger log = Logger.getLogger(WorkspaceServlet.class);

	@GET
	@ReadAccess
	@Path("")
	@Produces("application/zip")
	public Response exportWorkspace(@PathParam("workspace") String workspaceName) {
		StreamingOutput streamingOutput = (OutputStream outputStream) -> {
			try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
				Workspace workspace = WorkspaceManager.getInstance().get(workspaceName);

				Files.walk(Paths.get(workspace.getTestManager().getDirectory().getParent().toString())).filter(path -> !Files.isDirectory(path)).forEach(path -> {
					try {
						zipOutputStream.putNextEntry(new ZipEntry(getRelativePath(path, workspaceName)));
						zipOutputStream.write(Files.readAllBytes(path));
						zipOutputStream.closeEntry();
					}
					catch (Exception e) {
						log.error(e);
					}
				});
			}
		};

		return Response.status(Response.Status.OK).header("Content-Disposition", "attachment; filename=\"" + workspaceName + ".dtar\"").entity(streamingOutput).build();
	}

	@PUT
	@WriteAccess
	@Path("")
	@Consumes("multipart/form-data")
	public Response importWorkspace(@PathParam("workspace") String workspaceName, MultipartFormDataInput multipartFormDataInput) throws IOException {
		WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
		Workspace workspace = workspaceManager.get(workspaceName);

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
		workspaceManager.invalidate(workspaceName);

		try {
			workspace.getDecisionSession().importModel("main", "main", workspace.getModelManager().getFile());
		}
		catch (ModelImportException exception) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(SerializationHelper.getInstance().toJSON(exception.getResult())).build();
		}
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@DELETE
	@WriteAccess
	@Path("")
	public Response deleteWorkspace(@PathParam("workspace") String workspaceName) throws IOException {
		WorkspaceManager.getInstance().remove(workspaceName);

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