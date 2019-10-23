package de.materna.dmn.tester.servlets.model.beans;

import java.util.List;
import java.util.Map;

public class ExecutionContext {
	private Map<String, Object> context;
	private List<ExecutionKnowledgeModel> knowledgeModels;

	public ExecutionContext() {
	}

	public ExecutionContext(Map<String, Object> context, List<ExecutionKnowledgeModel> knowledgeModels) {
		this.context = context;
		this.knowledgeModels = knowledgeModels;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	public List<ExecutionKnowledgeModel> getKnowledgeModels() {
		return knowledgeModels;
	}

	public void setKnowledgeModels(List<ExecutionKnowledgeModel> knowledgeModels) {
		this.knowledgeModels = knowledgeModels;
	}
}
