package de.materna.dmn.tester.servlets.backup;

import de.materna.dmn.tester.servlets.model.beans.Workspace;
import de.materna.dmn.tester.persistence.WorkspaceManager;
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

@Path("/workspaces/{workspace}")
public class BackupServlet {
	private static final Logger log = Logger.getLogger(BackupServlet.class);

	@GET
	@Path("/backup")
	@Produces("application/zip")
	public Response getBackup(@PathParam("workspace") String workspaceName) {
		StreamingOutput streamingOutput = (OutputStream outputStream) -> {
			try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
				Workspace workspace = WorkspaceManager.getInstance().getWorkspace(workspaceName);

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

		return Response.status(Response.Status.OK).header("Content-Disposition", "attachment; filename=\"backup.dtar\"").entity(streamingOutput).build();
	}

	@PUT
	@Path("/backup")
	@Consumes("multipart/form-data")
	public Response importBackup(@PathParam("workspace") String workspaceName, MultipartFormDataInput multipartFormDataInput) throws IOException {
		InputPart inputPart = multipartFormDataInput.getFormDataMap().get("backup").get(0);

		try (InputStream inputStream = inputPart.getBody(InputStream.class, null)) {
			try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
				Workspace workspace = WorkspaceManager.getInstance().getWorkspace(workspaceName);

				while (true) {
					ZipEntry zipEntry = zipInputStream.getNextEntry();
					if (zipEntry == null) {
						break;
					}

					Files.write(Paths.get(workspace.getTestManager().getDirectory().getParent().toString(), zipEntry.getName()), IOUtils.toByteArray(zipInputStream));
				}
			}
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