package de.materna.dmn.tester.helpers;

import de.materna.jdec.serialization.SerializationHelper;

public abstract class Serializable {
	public String toJSON() {
		return SerializationHelper.getInstance().toJSON(this);
	}

	public abstract void fromJSON(String json);
}