package de.materna.dmn.tester.servlets.input.beans;

import java.util.Map;

public class Decision {
	private String expression;
	private Map<String, Object> context;

	public Decision() {
	}

	public Decision(String expression, Map<String, Object> context) {
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
}
