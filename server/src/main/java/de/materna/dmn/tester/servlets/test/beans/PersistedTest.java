package de.materna.dmn.tester.servlets.test.beans;

import java.util.List;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

public class PersistedTest extends Serializable {
	private String name;
	private String description;

	private String input;
	private List<String> outputs;

	public PersistedTest() {
	}

	public PersistedTest(String name, String description, String input, List<String> outputs) {
		this.name = name;
		this.description = description;
		this.input = input;
		this.outputs = outputs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public List<String> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}
	
	public void fromJson(String json) {
		PersistedTest temp = (PersistedTest) SerializationHelper.getInstance().toClass(json, PersistedTest.class);
		this.name = temp.getName();
		this.description = temp.getDescription();
		this.input = temp.getInput();
		this.outputs = temp.getOutputs();
	}
}