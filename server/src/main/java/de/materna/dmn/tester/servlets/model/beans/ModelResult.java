package de.materna.dmn.tester.servlets.model.beans;

import de.materna.dmn.tester.servlets.output.beans.Output;

import java.util.Map;

public class ModelResult {
	private Map<String, Output> outputs;
	private Map<String, Map<String, Object>> context;

	public ModelResult() {

	}

	public ModelResult(Map<String, Output> outputs, Map<String, Map<String, Object>> context) {
		this.outputs = outputs;
		this.context = context;
	}

	public Map<String, Output> getOutputs() {
		return outputs;
	}

	public Map<String, Map<String, Object>> getContext() {
		return context;
	}
}
