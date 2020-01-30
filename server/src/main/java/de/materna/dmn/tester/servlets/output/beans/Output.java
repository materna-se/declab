package de.materna.dmn.tester.servlets.output.beans;

import com.fasterxml.jackson.databind.JsonNode;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

public class Output extends Serializable {
	protected JsonNode value;

	public Output() {
	}

	public Output(JsonNode value) {
		this.value = value;
	}

	public JsonNode getValue() {
		return value;
	}

	public void setValue(JsonNode value) {
		this.value = value;
	}
	
	public void fromJson(String json) {
		Output temp = (Output) SerializationHelper.getInstance().toClass(json, Output.class);
		this.value = temp.getValue();
	}
}