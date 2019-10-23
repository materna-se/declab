package de.materna.dmn.tester.servlets.model.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExecutionKnowledgeModel {
	private String name;
	private ExecutionContext context;
	private int contextLevel;

	public ExecutionKnowledgeModel() {
	}

	public ExecutionKnowledgeModel(String name, ExecutionContext context, int contextLevel) {
		this.name = name;
		this.context = context;
		this.contextLevel = contextLevel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ExecutionContext getContext() {
		return context;
	}

	public void setContext(ExecutionContext context) {
		this.context = context;
	}

	@JsonIgnore
	public int getContextLevel() {
		return contextLevel;
	}

	public int decrementContextLevel() {
		return --contextLevel;
	}

	public int incrementContextLevel() {
		return ++contextLevel;
	}

	public void setContextLevel(int contextLevel) {
		this.contextLevel = contextLevel;
	}
}
