package de.materna.dmn.tester.servlets.model.beans;

import de.materna.dmn.tester.servlets.output.beans.Output;

import java.util.List;
import java.util.Map;

public class ModelResult {
	private Map<String, Output> outputs;
	private Map<String, Map<String, Object>> context;
	private List<String> messages;

	public ModelResult() {
	}

	public ModelResult(Map<String, Output> outputs, Map<String, Map<String, Object>> context, List<String> messages) {
		this.outputs = outputs;
		this.context = context;
		this.messages = messages;
	}

	public Map<String, Output> getOutputs() {
		return outputs;
	}

	public Map<String, Map<String, Object>> getContext() {
		return context;
	}

	public List<String> getMessages() {
		return messages;
	}
}
