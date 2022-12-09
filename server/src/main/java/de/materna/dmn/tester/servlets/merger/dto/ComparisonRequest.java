package de.materna.dmn.tester.servlets.merger.dto;

public class ComparisonRequest {
	private String dmnSpec;
	private String dmnImpl;

	public ComparisonRequest() {
	}

	public ComparisonRequest(String dmnSpec, String dmnImpl) {
		setDmnSpec(dmnSpec);
		setDmnImpl(dmnImpl);
	}

	public String getDmnSpec() {
		return dmnSpec;
	}

	public void setDmnSpec(String dmnSpec) {
		this.dmnSpec = dmnSpec;
	}

	public String getDmnImpl() {
		return dmnImpl;
	}

	public void setDmnImpl(String dmnImpl) {
		this.dmnImpl = dmnImpl;
	}
}
