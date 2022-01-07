package de.materna.dmn.tester.servlets.challenges.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Challenge {
	public String name = "";
	public String description;
	public ChallengeType type;
	public List<String> hints;
	public Object solution;
	public List<Scenario> scenarios;

	public Challenge() {
	}

	@JsonCreator
	public Challenge(@JsonProperty(value = "name", required = true) String name,
			@JsonProperty(value = "description", required = true) String description,
			@JsonProperty(value = "type", required = false) ChallengeType type,
			@JsonProperty(value = "hints", required = true) List<String> hints,
			@JsonProperty(value = "solution", required = true) Object solution,
			@JsonProperty(value = "scenarios", required = true) List<Scenario> scenarios) {
		if (name == null)
			name = "";

		this.name = name;
		this.description = description;

		if (type == null) {
			this.type = ChallengeType.FEEL;
		} else {
			this.type = type;
		}

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

	public ChallengeType getType() {
		return type;
	}

	public void setType(ChallengeType type) {
		this.type = type;
	}

	public List<String> getHints() {
		return hints;
	}

	public void setHints(List<String> hints) {
		this.hints = hints;
	}

	public Object getSolution() {
		return solution;
	}

	public void setSolution(Object solution) {
		this.solution = solution;
	}

	public List<Scenario> getScenarios() {
		return scenarios;
	}

	public void setScenarios(List<Scenario> cases) {
		this.scenarios = cases;
	}

	public enum ChallengeType {
		FEEL, DMN_MODEL
	}
}
