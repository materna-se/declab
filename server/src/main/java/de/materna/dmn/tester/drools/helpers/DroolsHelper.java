package de.materna.dmn.tester.drools.helpers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kie.dmn.api.core.DMNModel;

import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.model.ModelImportException;

public class DroolsHelper extends de.materna.jdec.dmn.DroolsHelper {
	private static final Logger log = LoggerFactory.getLogger(DroolsHelper.class);

	public static String getMainModelNamespace(Workspace workspace) {
		return workspace.getConfig().getModels().get(workspace.getConfig().getModels().size() - 1).get("namespace");
	}

	public static DMNModel getMainModel(Workspace workspace) {
		String mainModelNamespace = getMainModelNamespace(workspace);
		return workspace.getDecisionSession().getDMNDecisionSession().getRuntime().getModels().stream()
				.filter(dmnModel -> dmnModel.getNamespace().equals(mainModelNamespace)).findAny().get();
	}

	public static void importModels(Workspace workspace) throws IOException {
		Map<String, String> modelFiles = workspace.getModelManager().getFiles();

		List<Map<String, String>> models = workspace.getConfig().getModels();
		for (Map<String, String> model : models) {
			try {
				workspace.getDecisionSession().importModel(model.get("namespace"), modelFiles.get(model.get("uuid")));
			}
			catch (Exception e) {
				if(e instanceof ModelImportException) {
					log.warn("Import of model " + model.get("namespace") + " failed!" + ((ModelImportException) e).getResult().getMessages());
				}
				else {
					log.warn("Import of model " + model.get("namespace") + " failed!", e);
				}
			}
		}
	}
}
