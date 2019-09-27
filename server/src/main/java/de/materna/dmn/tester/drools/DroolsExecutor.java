package de.materna.dmn.tester.drools;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.materna.dmn.tester.drools.helpers.DroolsHelper;
import de.materna.dmn.tester.helpers.SerializationHelper;
import de.materna.dmn.tester.servlets.output.beans.Output;
import de.materna.jdec.DecisionSession;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DroolsExecutor {
	private DroolsExecutor() {
	}

	/**
	 * Uses evaluateInputs to convert all calculated results into our own class hierarchy.
	 */
	public static Map<String, Output> getOutputs(DecisionSession decisionSession, Map<String, ?> inputs) throws DatatypeConfigurationException {
		ObjectMapper objectMapper = SerializationHelper.getInstance().getObjectMapper();

		Map<String, Output> outputs = new LinkedHashMap<>();
		for (Map.Entry<String, Object> entry : decisionSession.executeModel((Map<String, ?>) DroolsHelper.convertTimeValue(inputs)).entrySet()) {
			outputs.put(entry.getKey(), new Output(objectMapper.valueToTree(entry.getValue())));
		}
		return outputs;
	}
}
