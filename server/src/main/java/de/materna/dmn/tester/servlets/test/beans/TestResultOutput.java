package de.materna.dmn.tester.servlets.test.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.materna.dmn.tester.servlets.output.beans.EnrichedOutput;
import de.materna.dmn.tester.servlets.output.beans.Output;

public class TestResultOutput {
	private EnrichedOutput expected;
	private Output calculated;

	public TestResultOutput() {
	}

	public TestResultOutput(EnrichedOutput expected, Output calculated) {
		this.expected = expected;
		this.calculated = calculated;
	}

	public Output getExpected() {
		return expected;
	}

	public void setExpected(EnrichedOutput expected) {
		this.expected = expected;
	}

	public Output getCalculated() {
		return calculated;
	}

	public void setCalculated(Output calculated) {
		this.calculated = calculated;
	}

	@JsonProperty
	public boolean isEqual() {
		if (calculated == null) { // "expected" can't be null, so we can skip the check.
			return false;
		}

		if (expected.getValue() == null || calculated.getValue() == null) {
			return expected.getValue() == null && calculated.getValue() == null;
		}

		return expected.getValue().equals((expected, calculated) -> {
			// JSON has only one data type for numbers.
			// Because of this, we need to make sure that 1 and 1.0 are equal.
			if (expected.isNumber() && calculated.isNumber()) {
				return expected.asDouble() == calculated.asDouble() ? 0 : 1;
			}

			return expected.equals(calculated) ? 0 : 1;
		}, calculated.getValue());
	}
}
