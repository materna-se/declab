package de.materna.dmn.tester.drools;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.materna.dmn.tester.servlets.output.beans.Output;
import de.materna.jdec.DecisionSession;
import de.materna.jdec.serialization.SerializationHelper;
import org.apache.log4j.Logger;
import org.kie.dmn.api.core.DMNModel;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DroolsExecutor {
	private static final Logger log = Logger.getLogger(DroolsExecutor.class);

	private DroolsExecutor() {
	}

	/**
	 * Uses evaluateInputs to convert all calculated results into our own class hierarchy.
	 */
	public static Map<String, Output> getOutputs(DecisionSession decisionSession, Map<String, ?> inputs) {
		ObjectMapper objectMapper = SerializationHelper.getInstance().getJSONMapper();

		Map<String, Output> outputs = new LinkedHashMap<>();

		DMNModel model = decisionSession.getRuntime().getModels().get(0);
		for (Map.Entry<String, Object> entry : decisionSession.executeModel(model.getNamespace(), model.getName(), inputs).entrySet()) {
			outputs.put(entry.getKey(), new Output(objectMapper.valueToTree(entry.getValue())));
		}

		return outputs;
	}
}
