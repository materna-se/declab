package de.materna.dmn.tester.servlets.input.beans;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

@JsonIgnoreProperties(ignoreUnknown=true) //TODO Solve this in SerializationHelper
public class Input extends Serializable {
	protected Map<String, ?> value = new LinkedHashMap<>();

	public Input() {
	}

	@JsonCreator
	public Input(@JsonProperty(value="value", required = true) Map<String, ?> value) {
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