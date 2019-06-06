package de.materna.dmn.tester.servlets.test.beans;

import java.util.Map;

public class TestResult {
	private Map<String, TestResultOutput> outputs;

	public TestResult(Map<String, TestResultOutput> outputs) {
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
}
