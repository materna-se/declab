package de.materna.dmn.tester.servlets.output.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

public class Output extends Serializable {
	protected JsonNode value;

	public Output() {
	}

	@JsonCreator
	public Output(@JsonProperty(value = "value", required = true) JsonNode value) {
		this.value = value;
	}

	public JsonNode getValue() {
		return value;
	}

	public void setValue(JsonNode value) {
		this.value = value;
	}

	@Override
	public void fromJSON(String json) {
		Output temp = (Output) SerializationHelper.getInstance().toClass(json, Output.class);
		this.value = temp.getValue();
	}
}