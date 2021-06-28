package de.materna.dmn.tester.servlets.challenges.beans;

import java.util.ArrayList;

import de.materna.dmn.tester.servlets.workspace.beans.Configuration.DecisionService;

public class DMNSolution {
	public ArrayList<ModelMap> models;
	public DecisionService decisionService;


	public ArrayList<ModelMap> getModels() {
		return models;
	}


	public void setModels(ArrayList<ModelMap> models) {
		this.models = models;
	}


	public DecisionService getDecisionService() {
		return decisionService;
	}


	public void setDecisionService(DecisionService decisionService) {
		this.decisionService = decisionService;
	}
}
