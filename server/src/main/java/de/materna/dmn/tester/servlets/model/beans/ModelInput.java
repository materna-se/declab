package de.materna.dmn.tester.servlets.model.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class ModelInput {
	private String type;
	private boolean collection;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Object> options;

	public ModelInput(String type, boolean collection) {
		this.type = type;
		this.collection = collection;
	}

	public ModelInput(String type, boolean collection, List<Object> options) {
		this.type = type;
		this.collection = collection;
		this.options = options;
	}

	public String getType() {
		return type;
	}

	public boolean isCollection() {
		return collection;
	}

	public List<Object> getOptions() {
		return options;
	}
}