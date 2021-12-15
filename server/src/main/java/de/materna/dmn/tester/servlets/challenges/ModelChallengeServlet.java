package de.materna.dmn.tester.servlets.challenges;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.type.TypeReference;

import de.materna.dmn.tester.servlets.filters.ReadAccess;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration.DecisionService;
import de.materna.jdec.DMNDecisionSession;
import de.materna.jdec.model.ExecutionResult;
import de.materna.jdec.model.ImportResult;
import de.materna.jdec.model.Model;
import de.materna.jdec.serialization.SerializationHelper;


@Path("/workspaces/{workspace}/")
public class ModelChallengeServlet {
	@POST
	@ReadAccess
	@Path("/challenges/execute_dmn_list")
	@Consumes("application/json")
	@Produces("application/json")
	public Response calculateModelChallengeList(@PathParam("workspace") String workspaceUUID, String body) throws IOException {
		// Calculate a list of scenario outputs using a list of models to import
		// It's important to calculate all scenarios in one request because the overhead of 
		// importing all models for each scenario is significant
		
		try {
			Map<String, Object> params = SerializationHelper.getInstance().toClass(body, new TypeReference<HashMap<String, Object>>() {
			});
			
			if (params.get("models") == null || params.get("inputs") == null) {
				throw new IllegalArgumentException();
			}
			
			DMNDecisionSession dS = new DMNDecisionSession();
			
			LinkedList<ExecutionResult> results = new LinkedList<ExecutionResult>();
			
			// Import models (defined by name (unused), namespace and source XML)
			ArrayList<Map<String, Object>> modelMaps = (ArrayList<Map<String, Object>>) params.get("models");
			
			for (Map<String, Object> modelMap : modelMaps) {
				String modelNamespace = (String) modelMap.get("namespace");
				String modelSource = (String) modelMap.get("source");
				
				dS.importModel(modelNamespace, modelSource);
			}
			
			// Get parsed models from decision session
			List<Model> models = dS.getModels();

			// Get scenario inputs sent by client
			ArrayList<Map<String, Object>> inputs = (ArrayList<Map<String, Object>>) params.get("inputs");
			
			// Execute each scenario and store its results
			
			DecisionService decisionService = (DecisionService) SerializationHelper.getInstance().toClass(SerializationHelper.getInstance().toJSON(params.get("decisionService")), DecisionService.class);
			
			for (Map<String, Object> input : inputs) {
				ExecutionResult result;				
				
				if (decisionService == null || decisionService.getName() == null || decisionService.getName().equals("")) {
					String ns = models.get(models.size() - 1).getNamespace();
					result = dS.executeModel(ns, input);
				} else {
					result = dS.executeModel(decisionService.getNamespace(), decisionService.getName(), input);
				}
				
				results.add(result);
			}
			
			return Response.status(Response.Status.OK).entity(SerializationHelper.getInstance().toJSON(results)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
}
