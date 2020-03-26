package de.materna.dmn.tester.drools.helpers;

import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.model.ModelImportException;
import de.materna.jdec.model.ModelNotFoundException;
import org.kie.dmn.api.core.DMNModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DroolsHelper extends de.materna.jdec.dmn.DroolsHelper {
	public static List<DMNModel> getModels(Workspace workspace) throws IOException {
		List<DMNModel> dmnModels = workspace.getDecisionSession().getRuntime().getModels();
		if (dmnModels.size() != 0) {
			return dmnModels;
		} else {
			throw new ModelNotFoundException();
		}
	}

	public static DMNModel getModel(Workspace workspace) throws IOException {
		if(workspace.getConfig().getModels().size() > 0) {
			List<Map<String, String>> models = workspace.getConfig().getModels();

			Map<String, String> mainModel = models.get(models.size() - 1); // TODO: The main model should be marked explicitly!
			return workspace.getDecisionSession().getRuntime().getModel(mainModel.get("namespace"), mainModel.get("name"));
		}
		throw new ModelNotFoundException();
	}

	public static void initModels(Workspace workspace) throws IOException {
		//Respect model import order if applicable
		Map<String, String> modelFiles = workspace.getModelManager().getFiles();
		List<Map<String, String>> models = workspace.getConfig().getModels();
		if(modelFiles != null && modelFiles.size() > 0 && models != null && models.size() > 0) {
			for(Map<String, String> model : models) {
				workspace.getDecisionSession().importModel(model.get("namespace"), model.get("name"), modelFiles.get(model.get("uuid")));
			}
		}
	}
}
