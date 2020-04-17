package de.materna.dmn.tester.servlets.model.beans;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

import java.util.Set;

public class Model extends Serializable {
	private String namespace;
	private String name;
	private String source;
	private Set<String> decisions;
	private Set<String> inputs;
	private Set<String> knowledgeModels;
	private Set<String> decisionServices;

	public Model() {
	}

	public Model(String namespace, String name, String source, Set<String> decisions, Set<String> inputs, Set<String> knowledgeModels, Set<String> decisionServices) {
		this.namespace = namespace;
		this.name = name;
		this.source = source;
		this.decisions = decisions;
		this.inputs = inputs;
		this.knowledgeModels = knowledgeModels;
		this.decisionServices = decisionServices;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getName() {
		return name;
	}

	public String getSource() {
		return source;
	}

	public Set<String> getDecisions() {
		return decisions;
	}

	public Set<String> getInputs() {
		return inputs;
	}

	public Set<String> getKnowledgeModels() {
		return knowledgeModels;
	}

	public Set<String> getDecisionServices() {
		return decisionServices;
	}

	public void fromJson(String json) {
		Model temp = (Model) SerializationHelper.getInstance().toClass(json, Model.class);
		this.namespace = temp.getNamespace();
		this.name = temp.getName();
		this.source = temp.getSource();
		this.decisions = temp.getDecisions();
		this.inputs = temp.getInputs();
		this.knowledgeModels = temp.getKnowledgeModels();
	}
}
