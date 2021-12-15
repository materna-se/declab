package de.materna.dmn.tester.servlets.test.beans;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

public class TestResult extends Serializable {
	private Map<String, TestResultOutput> outputs;

	public TestResult() {

	}

	@JsonCreator
	public TestResult(@JsonProperty(value = "outputs", required = true) Map<String, TestResultOutput> outputs) {
		this.outputs = outputs;
	}

	public Map<String, TestResultOutput> getOutputs() {
		return outputs;
	}

	public boolean isEqual() {
		for (TestResultOutput output : outputs.values()) {
			if (!output.isEqual()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void fromJSON(String json) {
		TestResult temp = (TestResult) SerializationHelper.getInstance().toClass(json, TestResult.class);
		this.outputs = temp.getOutputs();
	}
}
