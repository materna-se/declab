package de.materna.dmn.tester.servlets.output.beans;

import com.fasterxml.jackson.databind.JsonNode;

public class PersistedOutput extends Output {
	private String name;
	private String decision;

	public PersistedOutput() {
		super();
	}

	public PersistedOutput(String name, String decision, JsonNode value) {
		super(value);

		this.name = name;
		this.decision = decision;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}
}