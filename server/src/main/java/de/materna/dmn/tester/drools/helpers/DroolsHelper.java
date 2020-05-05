package de.materna.dmn.tester.drools.helpers;

import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.model.ModelNotFoundException;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DroolsHelper extends de.materna.jdec.dmn.DroolsHelper {
	public static List<DMNModel> getModels(Workspace workspace) {
		DMNRuntime runtime = workspace.getDecisionSession().getRuntime();

		List<DMNModel> models = new LinkedList<>();

		List<Map<String, String>> importedModels = workspace.getConfig().getModels();
		for (Map<String, String> importedModel : importedModels) {
			models.add(runtime.getModel(importedModel.get("namespace"), importedModel.get("name")));
		}

		return models;
	}

	public static DMNModel getModel(Workspace workspace) throws IOException {
		if (workspace.getConfig().getModels().size() == 0) {
			throw new ModelNotFoundException();
		}

		List<Map<String, String>> importedModels = workspace.getConfig().getModels();
		Map<String, String> mainImportedModel = importedModels.get(importedModels.size() - 1); // TODO: The main model should be marked explicitly!
		return workspace.getDecisionSession().getRuntime().getModel(mainImportedModel.get("namespace"), mainImportedModel.get("name"));
	}

	public static void initModels(Workspace workspace) throws IOException {
		Map<String, String> modelFiles = workspace.getModelManager().getFiles();

		List<Map<String, String>> models = workspace.getConfig().getModels();
		for (Map<String, String> model : models) {
			workspace.getDecisionSession().importModel(model.get("namespace"), modelFiles.get(model.get("uuid")));
		}
	}
}
