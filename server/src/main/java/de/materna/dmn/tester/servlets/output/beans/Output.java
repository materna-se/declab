package de.materna.dmn.tester.servlets.output.beans;

import com.fasterxml.jackson.databind.JsonNode;

public class Output {
	private JsonNode value;

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
}