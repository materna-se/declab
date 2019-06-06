package de.materna.dmn.tester.drools;

import de.materna.dmn.tester.helpers.SerializationHelper;
import de.materna.dmn.tester.servlets.output.beans.Output;
import de.materna.jdec.DecisionSession;

import java.util.LinkedHashMap;
import java.util.Map;

public class DroolsExecutor {
	private DroolsExecutor() {
	}

	/**
	 * Uses evaluateInputs to convert all calculated results into our own class hierarchy.
	 */
	public static Map<String, Output> getOutputs(DecisionSession decisionSession, Map<String, ?> inputs) {
		Map<String, Output> outputs = new LinkedHashMap<>();

		for (Map.Entry<String, Object> entry : decisionSession.executeModel(inputs).entrySet()) {
			outputs.put(entry.getKey(), new Output(SerializationHelper.getInstance().getObjectMapper().valueToTree(entry.getValue())));
		}

		return outputs;
	}
}
