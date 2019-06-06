package de.materna.dmn.tester.drools;

import de.materna.dmn.tester.drools.helpers.DroolsHelper;
import de.materna.dmn.tester.servlets.model.beans.ComplexModelInput;
import de.materna.dmn.tester.servlets.model.beans.ModelInput;
import de.materna.jdec.DecisionSession;
import org.apache.log4j.Logger;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.InputDataNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DroolsAnalyzer {
	private static final Logger log = Logger.getLogger(DroolsAnalyzer.class);

	private DroolsAnalyzer() {
	}

	/**
	 * Uses getInputs() to convert all inputs into our own class hierarchy
	 */
	public static ComplexModelInput getInputs(DecisionSession decisionSession) {
		ComplexModelInput modelInput = new ComplexModelInput("object", false);

		Map<String, ModelInput> inputs = new HashMap<>();
		for (InputDataNode inputDataNode : decisionSession.getModel().getInputs()) {
			inputs.put(inputDataNode.getName(), getInput(inputDataNode.getType()));
		}
		modelInput.setValue(inputs);

		return modelInput;
	}

	private static ModelInput getInput(DMNType type) {
		// In order to decide if the input is complex, we get the number of child inputs
		// If the input contains child inputs, we consider it complex
		if (type.getFields().size() == 0) {
			if (type.getAllowedValues().size() == 0) {
				if (type.isCollection()) {
					// It's a collection, we wrap the child inputs in an array
					LinkedList<ModelInput> inputs = new LinkedList<>();
					inputs.add(new ModelInput(type.getBaseType().getName(), type.isCollection()));
					return new ComplexModelInput(type.getName(), type.isCollection(), inputs);
				}

				return new ModelInput(type.getName(), type.isCollection());
			}

			// It is a normal input, in addition it contains a list of allowed values
			// FIX: By explicitly using getBaseType, we avoid the wrong type resolution of the drools engine
			return new ModelInput(type.getBaseType().getName(), type.isCollection(), DroolsHelper.convertOptions(type.getBaseType().getName(), type.getAllowedValues()));
		}

		if (type.isCollection()) {
			// It's a collection, we wrap the child inputs in an array
			LinkedList<ComplexModelInput> inputs = new LinkedList<>();
			inputs.add(new ComplexModelInput(type.getBaseType().getName(), false, getChildInputs(type.getFields())));
			return new ComplexModelInput(type.getName(), type.isCollection(), inputs);
		}

		return new ComplexModelInput(type.getName(), type.isCollection(), getChildInputs(type.getFields()));
	}

	/**
	 * Creates a list of all child inputs.
	 * If they have a complex type, they are resolved by getInput().
	 *
	 * @param fields Child Inputs
	 */
	private static Map<String, ModelInput> getChildInputs(Map<String, DMNType> fields) {
		Map<String, ModelInput> inputs = new HashMap<>();

		for (Map.Entry<String, DMNType> entry : fields.entrySet()) {
			inputs.put(entry.getKey(), getInput(entry.getValue()));
		}

		return inputs;
	}
}
