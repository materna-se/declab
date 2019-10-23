package de.materna.dmn.tester.servlets.model.beans;

import de.materna.dmn.tester.servlets.output.beans.Output;

import java.util.Map;

public class ExecutionResult {
	private Map<String, Output> outputs;
	private Map<String, ExecutionContext> context;

	public ExecutionResult() {
	}

	public ExecutionResult(Map<String, Output> outputs, Map<String, ExecutionContext> context) {
		this.outputs = outputs;
		this.context = context;
	}

	public Map<String, Output> getOutputs() {
		return outputs;
	}

	public Map<String, ExecutionContext> getContext() {
		return context;
	}
}
