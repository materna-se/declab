package de.materna.dmn.tester.helpers;

import de.materna.jdec.serialization.SerializationHelper;

public abstract class Serializable {
	public String toJson() {
		return SerializationHelper.getInstance().toJSON(this);
	}
	
	public abstract void fromJson(String json);
}
