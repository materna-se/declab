package de.materna.dmn.tester.servlets.input.beans;

import java.util.HashMap;
import java.util.Map;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

public class Input extends Serializable {
	protected Map<String, ?> value = new HashMap<>();

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
	
	public void fromJson(String json) {
		Input temp = (Input) SerializationHelper.getInstance().toClass(json, Input.class);
		this.value = temp.getValue();
	}
}