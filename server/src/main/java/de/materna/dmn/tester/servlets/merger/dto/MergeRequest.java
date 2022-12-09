package de.materna.dmn.tester.servlets.merger.dto;

public class MergeRequest {
	private String dmnOld;
	private String dmnNew;

	public MergeRequest() {
	}

	public MergeRequest(String dmnOld, String dmnNew) {
		this.dmnOld = dmnOld;
		this.dmnNew = dmnNew;
	}

	public String getDmnOld() {
		return dmnOld;
	}

	public String getDmnNew() {
		return dmnNew;
	}

}
