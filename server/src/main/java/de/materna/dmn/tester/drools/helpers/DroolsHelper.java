package de.materna.dmn.tester.drools.helpers;

import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.model.ModelImportException;
import de.materna.jdec.model.ModelNotFoundException;
import org.kie.dmn.api.core.DMNModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DroolsHelper extends de.materna.jdec.dmn.DroolsHelper {
	private static final Logger log = LoggerFactory.getLogger(DroolsHelper.class);

	public static String getMainModelNamespace(Workspace workspace) throws ModelNotFoundException {
		List<Map<String, String>> models = workspace.getConfig().getModels();
		if (models.size() == 0) {
			throw new ModelNotFoundException();
		}

		return models.get(models.size() - 1).get("namespace");
	}

	public static DMNModel getMainModel(Workspace workspace) throws ModelNotFoundException {
		String mainModelNamespace = getMainModelNamespace(workspace);

		Optional<DMNModel> optionalModel = workspace.getDecisionSession().getDMNDecisionSession().getRuntime().getModels().stream().filter(dmnModel -> dmnModel.getNamespace().equals(mainModelNamespace)).findAny();
		if (!optionalModel.isPresent()) {
			throw new ModelNotFoundException();
		}

		return optionalModel.get();
	}

	public static void importModels(Workspace workspace) throws IOException {
		Map<String, String> modelFiles = workspace.getModelManager().getFiles();

		List<Map<String, String>> models = workspace.getConfig().getModels();
		for (Map<String, String> model : models) {
			try {
				workspace.getDecisionSession().importModel(model.get("namespace"), modelFiles.get(model.get("uuid")));
			}
			catch (Exception e) {
				if (e instanceof ModelImportException) {
					log.warn("Import of model " + model.get("namespace") + " failed!" + ((ModelImportException) e).getResult().getMessages());
				}
				else {
					log.warn("Import of model " + model.get("namespace") + " failed!", e);
				}
			}
		}
	}
}
