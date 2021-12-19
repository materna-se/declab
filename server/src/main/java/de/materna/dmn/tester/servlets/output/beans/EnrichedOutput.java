package de.materna.dmn.tester.servlets.output.beans;

import de.materna.jdec.serialization.SerializationHelper;

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

	@Override
	public void fromJSON(String json) {
		EnrichedOutput temp = (EnrichedOutput) SerializationHelper.getInstance().toClass(json, EnrichedOutput.class);
		this.value = temp.getValue();
		this.name = temp.getName();
		this.decision = temp.getDecision();
		this.uuid = temp.getUUID();
	}
}