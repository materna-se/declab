package de.materna.dmn.tester.servlets.model.beans;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.dmn.tester.servlets.output.beans.Output;
import de.materna.jdec.serialization.SerializationHelper;

import java.util.List;
import java.util.Map;

public class ModelResult extends Serializable {
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
	
	public void fromJson(String json) {
		ModelResult temp = (ModelResult) SerializationHelper.getInstance().toClass(json, ModelResult.class);
		this.outputs = temp.getOutputs();
		this.context = temp.getContext();
		this.messages = temp.getMessages();
	}
}
