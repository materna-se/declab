package de.materna.dmn.tester.servlets.input.beans;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

public class Decision extends Serializable {
	private String expression;
	private Map<String, Object> context;

	public Decision() {
	}

	@JsonCreator
	public Decision(@JsonProperty(value = "expression", required = true) String expression,
			@JsonProperty(value = "context", required = true) Map<String, Object> context) {
		this.expression = expression;
		this.context = context;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	@Override
	public void fromJSON(String json) {
		Decision temp = (Decision) SerializationHelper.getInstance().toClass(json, Decision.class);
		this.expression = temp.getExpression();
		this.context = temp.getContext();
	}
}