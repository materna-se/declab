package de.materna.dmn.tester.servlets.playground.beans;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.materna.dmn.tester.helpers.Serializable;
import de.materna.jdec.serialization.SerializationHelper;

public class Playground extends Serializable {
	public String name;
	public String description;
	public String expression;
	protected Map<String, ?> context = new LinkedHashMap<>();
	
	public Playground() {
		
	}

	@JsonCreator
	public Playground(@JsonProperty(value = "name", required = true) String name,
					  @JsonProperty(value = "description", required = true) String description,
					  @JsonProperty(value = "expression", required = true) String expression,
					  @JsonProperty(value = "context", required = true) Map<String, ?> context) {
		if(name == null) name = "";
		
		this.name = name;
		this.description = description;
		this.expression = expression;
		this.context = context;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public Map<String, ?> getContext() {
		return context;
	}

	public void setContext(Map<String, ?> context) {
		this.context = context;
	}
	
	public void fromJSON(String json) {
		Playground temp = (Playground) SerializationHelper.getInstance().toClass(json, Playground.class);
		this.name = temp.getName();
		this.description = temp.getDescription();
		this.expression = temp.getExpression();
		this.context = temp.getContext();
	}
}
