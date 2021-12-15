package de.materna.dmn.tester.servlets.challenges.beans;

import java.util.List;

import javax.ws.rs.BadRequestException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Challenge {
	public String name = "";
	public String description;
	public List<String> hints;
	public String solution;
	public List<Scenario> scenarios;

	public Challenge() {

	}

	@JsonCreator
	public Challenge(@JsonProperty(value = "name", required = true) String name,
			@JsonProperty(value = "description", required = true) String description,
			@JsonProperty(value = "hints", required = true) List<String> hints,
			@JsonProperty(value = "solution", required = true) String solution,
			@JsonProperty(value = "scenarios", required = true) List<Scenario> scenarios) {
		if (name == null)
			throw new BadRequestException();

		this.name = name;
		this.description = description;
		this.hints = hints;
		this.solution = solution;
		this.scenarios = scenarios;
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

	public List<String> getHints() {
		return hints;
	}

	public void setHints(List<String> hints) {
		this.hints = hints;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public List<Scenario> getScenarios() {
		return scenarios;
	}

	public void setScenarios(List<Scenario> cases) {
		this.scenarios = cases;
	}
}
