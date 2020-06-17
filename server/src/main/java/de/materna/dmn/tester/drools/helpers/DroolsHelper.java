package de.materna.dmn.tester.drools.helpers;

import de.materna.dmn.tester.servlets.workspace.beans.Workspace;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DroolsHelper extends de.materna.jdec.dmn.DroolsHelper {
	public static String getMainModelNamespace(Workspace workspace) {
		return workspace.getConfig().getModels().get(workspace.getConfig().getModels().size() - 1).get("namespace");
	}

	public static void importModels(Workspace workspace) throws IOException {
		Map<String, String> modelFiles = workspace.getModelManager().getFiles();

		List<Map<String, String>> models = workspace.getConfig().getModels();
		for (Map<String, String> model : models) {
			workspace.getDecisionSession().importModel(model.get("namespace"), modelFiles.get(model.get("uuid")));
		}
	}
}
