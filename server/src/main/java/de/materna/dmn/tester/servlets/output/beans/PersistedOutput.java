package de.materna.dmn.tester.servlets.output.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import de.materna.jdec.serialization.SerializationHelper;

public class PersistedOutput extends Output {
	protected String name;
	protected String decision;

	public PersistedOutput() {
	}

	@JsonCreator
	public PersistedOutput(@JsonProperty(value = "name", required = true) String name,
			@JsonProperty(value = "decision", required = true) String decision,
			@JsonProperty(value = "value", required = true) JsonNode value) {
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

	@Override
	public void fromJSON(String json) {
		final PersistedOutput temp = (PersistedOutput) SerializationHelper.getInstance().toClass(json,
				PersistedOutput.class);
		this.value = temp.getValue();
		this.name = temp.getName();
		this.decision = temp.getDecision();
	}
}