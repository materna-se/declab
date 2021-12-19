package de.materna.dmn.tester.servlets.challenges.beans;

import javax.ws.rs.BadRequestException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.materna.dmn.tester.servlets.input.beans.Input;
import de.materna.dmn.tester.servlets.output.beans.Output;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Scenario {
	public String name;
	public Input input;
	public Output output;

	public Scenario() {

	}

	@JsonCreator
	public Scenario(@JsonProperty(value = "name", required = true) String name,
			@JsonProperty(value = "input", required = true) Input input,
			@JsonProperty(value = "output", required = true) Output output) {
		if (name == null)
			throw new BadRequestException();

		this.name = name;
		this.input = input;
		this.output = output;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Input getInput() {
		return input;
	}

	public void setInput(Input input) {
		this.input = input;
	}

	public Output getOutput() {
		return output;
	}

	public void setOutput(Output output) {
		this.output = output;
	}
}