package de.materna.dmn.tester.servlets.model.beans;

import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.DecisionServiceNode;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Model extends Serializable {
	private String namespace;
	private String name;
	private List<String> decisions;
	private List<String> knowledgeModels;
	private List<String> decisionServices;
	private String source;

	public Model() {
		
	}
	
	public Model(String namespace, String name, Set<DecisionNode> decisionNodes, Set<BusinessKnowledgeModelNode> businessKnowledgeModelNodes, Collection<DecisionServiceNode> decisionServiceNodes, String source) {
		this.namespace = namespace;
		
		this.name = name;

		// At this moment, the name of the component is sufficient for us.
		// All other fields are filtered out.

		List<String> decisions = new LinkedList<>();
		for (DecisionNode node : decisionNodes) {
			decisions.add(node.getName());
		}
		this.decisions = decisions;

		List<String> businessKnowledgeModels = new LinkedList<>();
		for (BusinessKnowledgeModelNode node : businessKnowledgeModelNodes) {
			businessKnowledgeModels.add(node.getName());
		}
		this.knowledgeModels = businessKnowledgeModels;

		List<String> decisionServices = new LinkedList<>();
		for (DecisionServiceNode node : decisionServiceNodes) {
			decisionServices.add(node.getName());
		}
		this.decisionServices = decisionServices;
		
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public List<String> getDecisions() {
		return decisions;
	}

	public List<String> getKnowledgeModels() {
		return knowledgeModels;
	}

	public List<String> getDecisionServices() {
		return decisionServices;
	}
	
	public String getNamespace() {
		return namespace;
	}

	public String getSource() {
		return source;
	}
	
	public void fromJson(String json) {
		Model temp = (Model) SerializationHelper.getInstance().toClass(json, Model.class);
		this.namespace = temp.getNamespace();
		this.name = temp.getName();
		this.decisions = temp.getDecisions();
		this.knowledgeModels = temp.getKnowledgeModels();
		this.decisionServices = temp.getDecisionServices();
		this.source = temp.getSource();
	}
}
