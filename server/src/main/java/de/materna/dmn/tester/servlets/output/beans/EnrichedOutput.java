package de.materna.dmn.tester.servlets.output.beans;

public class EnrichedOutput extends PersistedOutput {
	private String uuid;

	public EnrichedOutput() {
		super();
	}

	public EnrichedOutput(String uuid, PersistedOutput persistedOutput) {
		super(persistedOutput.getName(), persistedOutput.getDecision(), persistedOutput.getValue());

		this.uuid = uuid;
	}

	public String getUUID() {
		return uuid;
	}
}
