package de.materna.dmn.tester.servlets.input.beans;

import java.util.Map;

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

	public String getParent() {
		return parent;
	}
}