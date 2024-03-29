package de.materna.dmn.tester.servlets.challenges.helpers;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.materna.dmn.tester.servlets.challenges.beans.ModelMap;
import de.materna.dmn.tester.servlets.challenges.beans.Scenario;
import de.materna.dmn.tester.servlets.input.beans.Decision;
import de.materna.dmn.tester.servlets.input.beans.Input;
import de.materna.dmn.tester.servlets.output.beans.Output;
import de.materna.dmn.tester.servlets.workspace.beans.Configuration.DecisionService;
import de.materna.jdec.DMNDecisionSession;
import de.materna.jdec.HybridDecisionSession;
import de.materna.jdec.model.ExecutionResult;
import de.materna.jdec.model.ImportResult;
import de.materna.jdec.model.Model;
import de.materna.jdec.model.ModelImportException;
import de.materna.jdec.model.ModelNotFoundException;
import de.materna.jdec.serialization.SerializationHelper;

public class ChallengeExecutionHelper {
	public static ArrayList<Scenario> calculateFEELExpression(String feelString, ArrayList<Scenario> scenarios) {
		// Calculate a list of scenario outputs using a FEEL expression
		
		try {
			Decision decision = new Decision();
			decision.setExpression(feelString);
		
			// Why does this throw a generic Exception?
			HybridDecisionSession decisionSession = new HybridDecisionSession();
			
			ObjectMapper mapper = new ObjectMapper();
			
			for (Scenario scenario : scenarios) {
				ExecutionResult executionResult = decisionSession.getDMNDecisionSession().executeExpression(decision.getExpression(), scenario.getInput().getValue());
				
				JsonNode x = mapper.valueToTree(executionResult.getOutputs().get("main"));
				
				scenario.setOutput(new Output(x));
			}
			
			return scenarios;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public static ArrayList<Scenario> calculateModels(ArrayList<ModelMap> modelMaps, ArrayList<Scenario> scenarios) throws ModelImportException, ModelNotFoundException {
		return calculateModels(modelMaps, scenarios, null);
	}
	
	public static ArrayList<Scenario> calculateModels(ArrayList<ModelMap> modelMaps, ArrayList<Scenario> scenarios, DecisionService decisionService) throws ModelNotFoundException, ModelImportException {
		// Calculate a list of scenario outputs using any number of imported models
		
		// Import
		
		DMNDecisionSession dS = new DMNDecisionSession();
		
		for (ModelMap modelMap : modelMaps) {
			String modelNamespace = modelMap.getNamespace();
			String modelSource = modelMap.getSource();
			
			dS.importModel(modelNamespace, modelSource);
		}
		
		List<Model> models = dS.getModels();
		
		// Execute
		
		ObjectMapper mapper = new ObjectMapper();
		
		for (Scenario scenario : scenarios) {
			// Inputs get modified by dS.executeModel().
			// We need to preserve the original, so we make a copy.
			Input input = scenario.getInput();
			Input inputCopy = (Input) SerializationHelper.getInstance().toClass(SerializationHelper.getInstance().toJSON(input), Input.class);
			
			ExecutionResult executionResult;
			
			if (decisionService != null && decisionService.getName() != null) {
				executionResult = dS.executeModel(decisionService.getNamespace(), decisionService.getName(), scenario.getInput().getValue());
			} else {
				String ns = models.get(models.size() - 1).getNamespace();
				executionResult = dS.executeModel(ns, scenario.getInput().getValue());	
			}
			
			JsonNode x = mapper.valueToTree(executionResult.getOutputs());
			
			scenario.setOutput(new Output(x));
			scenario.setInput(inputCopy);
		}
		
		return scenarios;
	}
}
