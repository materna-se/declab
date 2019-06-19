package de.materna.dmn.tester.servlets.model;

import com.fasterxml.jackson.core.type.TypeReference;
import de.materna.dmn.tester.drools.DroolsAnalyzer;
import de.materna.dmn.tester.drools.DroolsExecutor;
import de.materna.dmn.tester.helpers.SerializationHelper;
import de.materna.dmn.tester.persistence.WorkspaceManager;
import de.materna.dmn.tester.servlets.input.beans.RawInput;
import de.materna.dmn.tester.servlets.model.beans.Model;
import de.materna.dmn.tester.servlets.model.beans.Workspace;
import de.materna.dmn.tester.servlets.output.beans.Output;
import de.materna.jdec.beans.ImportResult;
import de.materna.jdec.exceptions.ImportException;
import org.apache.log4j.Logger;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.parser.feel11.profiles.KieExtendedFEELProfile;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/workspaces/{workspace}")
public class ModelServlet {
	private static final Logger log = Logger.getLogger(ModelServlet.class);

	@GET
	@Path("/model")
	@Produces("application/json")
	public Response getModel(@PathParam("workspace") String workspaceName) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().getWorkspace(workspaceName);

			DMNModel dmnModel = workspace.getDecisionSession().getModel();

			Model model = new Model(dmnModel.getName(), dmnModel.getDecisions(), dmnModel.getBusinessKnowledgeModels(), dmnModel.getDecisionServices());
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(model)).build();
		}
		catch (IOException exception) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@PUT
	@Path("/model")
	@Consumes("text/xml")
	public Response importModel(@PathParam("workspace") String workspaceName, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().getWorkspace(workspaceName);

			ImportResult importResult = workspace.getDecisionSession().importModel(body);

			workspace.getModelManager().persistFile(body);

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(importResult.getMessages())).build();

		}
		catch (ImportException exception) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(SerializationHelper.getInstance().toJSON(exception.getResult())).build();
		}
		catch (Exception exception) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@GET
	@Path("/model/inputs")
	@Produces("application/json")
	public Response getInputs(@PathParam("workspace") String workspaceName) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().getWorkspace(workspaceName);

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(DroolsAnalyzer.getInputs(workspace.getDecisionSession()))).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@POST
	@Path("/model/inputs")
	@Consumes("application/json")
	@Produces("application/json")
	public Response calculateOutputs(@PathParam("workspace") String workspaceName, String body) {
		try {
			Workspace workspace = WorkspaceManager.getInstance().getWorkspace(workspaceName);

			Map<String, Object> inputs = SerializationHelper.getInstance().toClass(body, new TypeReference<HashMap<String, Object>>() {
			});
			Map<String, Output> outputs = DroolsExecutor.getOutputs(workspace.getDecisionSession(), inputs);

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(outputs)).build();
		}
		catch (IOException exception) {
			log.error(exception);

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
	}

	@POST
	@Path("/model/inputs/raw")
	@Consumes("application/json")
	@Produces("text/plain")
	public Response calculateRawOutputs(String body) {
		RawInput rawInput = (RawInput) SerializationHelper.getInstance().toClass(body, RawInput.class);

		try {
			List<FEELProfile> profiles = new ArrayList<>();
			profiles.add(new KieExtendedFEELProfile());
			FEEL feel = FEEL.newInstance(profiles);

			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(feel.evaluate(rawInput.getExpression(), rawInput.getContext()))).build();
		}
		catch (Exception exception) {
			exception.printStackTrace();
			log.error(exception);

			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
}