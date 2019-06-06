package de.materna.dmn.tester.servlets.input.beans;

import java.util.HashMap;
import java.util.Map;

public class Input {
	private Map<String, ?> value = new HashMap<>();

	public Input() {
	}

	public Input(Map<String, ?> value) {
		this.value = value;
	}

	public Map<String, ?> getValue() {
		return value;
	}

	public void setValue(Map<String, ?> value) {
		this.value = value;
	}
}