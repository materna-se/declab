package de.materna.dmn.tester.servlets.model.beans;

public class ComplexModelInput extends ModelInput {
	private Object value;

	public ComplexModelInput(String type) {
		super(type);
	}

	public ComplexModelInput(String type, Object value) {
		super(type);

		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}