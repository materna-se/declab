package de.materna.dmn.tester.drools.helpers;

import de.materna.jdec.DecisionSession;
import de.materna.jdec.model.ModelNotFoundException;
import org.kie.dmn.api.core.DMNModel;

import java.util.List;

public class DroolsHelper {
	public static DMNModel getModel(DecisionSession decisionSession) throws ModelNotFoundException {
		List<DMNModel> dmnModels = decisionSession.getRuntime().getModels();
		if (dmnModels.size() == 0) {
			throw new ModelNotFoundException();
		}

		return dmnModels.get(0);
	}
}
