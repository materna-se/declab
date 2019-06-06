package de.materna.dmn.tester.servlets.model.beans;

public class ComplexModelInput extends ModelInput {
	private Object value;

	public ComplexModelInput(String type, boolean collection) {
		super(type, collection);
	}

	public ComplexModelInput(String type, boolean collection, Object value) {
		super(type, collection);

		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}