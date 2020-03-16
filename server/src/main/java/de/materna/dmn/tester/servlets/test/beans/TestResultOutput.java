package de.materna.dmn.tester.servlets.test.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.dmn.tester.servlets.output.beans.EnrichedOutput;
import de.materna.dmn.tester.servlets.output.beans.Output;
import de.materna.jdec.serialization.SerializationHelper;

import org.apache.log4j.Logger;

public class TestResultOutput extends Serializable {
	private JsonNode expected;
	private JsonNode calculated;

	public TestResultOutput() {
	}

	public TestResultOutput(JsonNode expected, JsonNode calculated) {
		this.expected = expected;
		this.calculated = calculated;
	}

	public JsonNode getExpected() {
		return expected;
	}

	public void setExpected(JsonNode expected) {
		this.expected = expected;
	}

	public JsonNode getCalculated() {
		return calculated;
	}

	public void setCalculated(JsonNode calculated) {
		this.calculated = calculated;
	}

	@JsonProperty
	public boolean isEqual() {
		return expected.equals((expected, calculated) -> {
			// JSON has only one data type for numbers.
			// Because of this, we need to make sure that 1 and 1.0 are equal.
			if (expected.isNumber() && calculated.isNumber()) {
				return expected.asDouble() == calculated.asDouble() ? 0 : 1;
			}

			return expected.equals(calculated) ? 0 : 1;
		}, calculated);
	}

	public void fromJson(String json) {
		TestResultOutput temp = (TestResultOutput) SerializationHelper.getInstance().toClass(json, TestResultOutput.class);
		this.expected = temp.getExpected();
		this.calculated = temp.getCalculated();
	}
}
