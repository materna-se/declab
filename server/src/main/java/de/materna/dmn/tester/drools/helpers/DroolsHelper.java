package de.materna.dmn.tester.drools.helpers;

import de.materna.dmn.tester.servlets.workspace.beans.Workspace;
import de.materna.jdec.model.ImportResult;
import de.materna.jdec.model.ModelImportException;
import de.materna.jdec.model.ModelNotFoundException;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;

import java.io.IOException;
import java.util.List;

public class DroolsHelper extends de.materna.jdec.dmn.DroolsHelper {
	/**
	 * TODO-JAVA-SUPPORT: Is this required?
	 */
	public static DMNModel getModel(Workspace workspace) throws ModelNotFoundException {
		try {
			List<DMNModel> dmnModels = workspace.getDecisionSession().getDMNDecisionSession().getRuntime().getModels();
			if (dmnModels.size() != 0) {
				return dmnModels.get(0);
			}

			if (workspace.getModelManager().fileExists()) {
				workspace.getDecisionSession().getDMNDecisionSession().importModel("main", "main", workspace.getModelManager().getFile());
				return workspace.getDecisionSession().getDMNDecisionSession().getRuntime().getModels().get(0);
			}

			throw new ModelNotFoundException();
		}
		catch (IOException e) {
			throw new ModelNotFoundException();
		}
	}
}
