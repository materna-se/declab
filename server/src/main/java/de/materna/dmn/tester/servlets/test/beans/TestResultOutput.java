package de.materna.dmn.tester.servlets.test.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

public class TestResultOutput extends Serializable {

	private String uuid;
	private String name;
	private String decision;
	private JsonNode expected;
	private JsonNode calculated;

	public TestResultOutput() {
	}

	@JsonCreator
	public TestResultOutput(@JsonProperty(value = "uuid", required = true) String uuid,
			@JsonProperty(value = "name", required = true) String name,
			@JsonProperty(value = "decision", required = true) String decision,
			@JsonProperty(value = "expected", required = true) JsonNode expected,
			@JsonProperty(value = "calculated", required = true) JsonNode calculated) {
		this.uuid = uuid;
		this.name = name;
		this.decision = decision;
		this.expected = expected;
		this.calculated = calculated;
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getDecision() {
		return decision;
	}

	public JsonNode getExpected() {
		return expected;
	}

	public JsonNode getCalculated() {
		return calculated;
	}

	@JsonProperty
	public boolean isEqual() {
		if (expected instanceof NullNode && calculated instanceof NullNode) {
			return true;
		}

		return expected.equals((expected, calculated) -> {
			// JSON has only one data type for numbers.
			// Because of this, we need to make sure that 1 and 1.0 are equal.
			if (expected.isNumber() && calculated.isNumber()) {
				return expected.asDouble() == calculated.asDouble() ? 0 : 1;
			}

			return expected.equals(calculated) ? 0 : 1;
		}, calculated);
	}

	@Override
	public void fromJSON(String json) {
		TestResultOutput temp = (TestResultOutput) SerializationHelper.getInstance().toClass(json,
				TestResultOutput.class);
		this.expected = temp.getExpected();
		this.calculated = temp.getCalculated();
	}
}
