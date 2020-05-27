package de.materna.dmn.tester.servlets.input.beans;

import java.util.Map;

import de.materna.jdec.serialization.SerializationHelper;

public class PersistedInput extends Input {
	private String name;
	private String parent;

	public PersistedInput() {
	}

	public PersistedInput(String name, String parent, Map<String, ?> value) {
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
	public void fromJson(String json) {
		PersistedInput temp = (PersistedInput) SerializationHelper.getInstance().toClass(json, PersistedInput.class);
		this.value = temp.getValue();
		this.name = temp.getName();
		this.parent = temp.getParent();
	}
}