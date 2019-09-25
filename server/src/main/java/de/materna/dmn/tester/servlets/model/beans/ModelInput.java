package de.materna.dmn.tester.servlets.model.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class ModelInput {
	private String type;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Object> options;

	public ModelInput(String type) {
		this.type = type;
	}

	public ModelInput(String type, List<Object> options) {
		this.type = type;
		this.options = options;
	}

	public String getType() {
		return type;
	}

	public List<Object> getOptions() {
		return options;
	}
}