package de.materna.dmn.tester.servlets.merger;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.materna.dmn.tester.servlets.Merger;
import de.materna.dmn.tester.servlets.merger.dto.ComparisonRequest;
import de.materna.dmn.tester.servlets.merger.dto.MergeRequest;
import de.materna.dmn.tester.servlets.merger.entities.DifferenceError;
import de.materna.jdec.serialization.SerializationHelper;

@Path("/merger")
public class MergerServlet {

	@POST
	@Path("/compare")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response compare(String body) {
		final ComparisonRequest comparationRequest = (ComparisonRequest) SerializationHelper.getInstance().toClass(body,
				ComparisonRequest.class);
		final String specContent = comparationRequest.getDmnSpec();
		final String implContent = comparationRequest.getDmnImpl();

		if (specContent != null && specContent.length() > 0 && implContent != null && implContent.length() > 0) {
			final List<DifferenceError> errors = Merger.compare(specContent, implContent);
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(errors)).build();
		}
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@POST
	@Path("/merge")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response merge(String body) {
		final MergeRequest mergeRequest = (MergeRequest) SerializationHelper.getInstance().toClass(body,
				MergeRequest.class);
		final String oldContent = mergeRequest.getDmnOld();
		final String newContent = mergeRequest.getDmnNew();

		if (oldContent != null && oldContent.length() > 0 && newContent != null && newContent.length() > 0) {
			final String mergedContent = Merger.merge(oldContent, newContent);
			if (mergedContent != null && mergedContent.length() > 0) {
				return Response.status(Response.Status.OK)
						.entity(SerializationHelper.getInstance().toJSON(mergedContent)).build();
			}
			return Response.status(Response.Status.NOT_MODIFIED)
					.entity(SerializationHelper.getInstance().toJSON(oldContent)).build();
		}
		return Response.status(Response.Status.NO_CONTENT).build();
	}
}