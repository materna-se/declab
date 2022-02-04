package de.materna.dmn.tester.servlets.input.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class Input extends Serializable {
	protected Map<String, Object> value = new LinkedHashMap<>();

	public Input() {
	}

	@JsonCreator
	public Input(@JsonProperty(value = "value", required = true) Map<String, Object> value) {
		this.value = value;
	}

	public Map<String, Object> getValue() {
		return value;
	}

	public void setValue(Map<String, Object> value) {
		this.value = value;
	}

	@Override
	public void fromJSON(String json) {
		Input temp = (Input) SerializationHelper.getInstance().toClass(json, Input.class);
		this.value = temp.getValue();
	}
}