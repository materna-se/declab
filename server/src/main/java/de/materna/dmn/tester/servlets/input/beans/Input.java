package de.materna.dmn.tester.servlets.input.beans;

import java.util.LinkedHashMap;
import java.util.Map;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

public class Input extends Serializable {
	protected Map<String, ?> value = new LinkedHashMap<>();

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
	
	public void fromJSON(String json) {
		Input temp = (Input) SerializationHelper.getInstance().toClass(json, Input.class);
		this.value = temp.getValue();
	}
}