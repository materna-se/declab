package de.materna.dmn.tester.servlets.input.beans;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.materna.jdec.serialization.SerializationHelper;

public class PersistedInput extends Input {
	private String name;
	private String parent;

	public PersistedInput() {
	}

	@JsonCreator
	public PersistedInput(@JsonProperty(value = "name", required = true) String name,
			@JsonProperty(value = "parent", required = false) String parent,
			@JsonProperty(value = "value", required = true) Map<String, Object> value) {
		super(value);

		this.name = name;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	@Override
	public void fromJSON(String json) {
		final PersistedInput temp = (PersistedInput) SerializationHelper.getInstance().toClass(json,
				PersistedInput.class);
		this.value = temp.getValue();
		this.name = temp.getName();
		this.parent = temp.getParent();
	}
}