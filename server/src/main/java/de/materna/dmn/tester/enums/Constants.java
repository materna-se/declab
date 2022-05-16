package de.materna.dmn.tester.enums;

public enum Constants {

	TEMP("please ignore");

	public final String VALUE;

	Constants(String value) {
		this.VALUE = value;
	}

	public enum EMAIL {

		AUTH("true"), CONFIRMED("CONFIRMED!"), HOST("localhost" /* or IP address */), PASSWORD("pass"), PORT("587"),
		SENDER("declab@materna.de"), STARTTLS("true");

		public final String VALUE;

		EMAIL(String value) {
			this.VALUE = value;
		}
	}
}
