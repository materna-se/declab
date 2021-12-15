package de.materna.dmn.tester.beans;

import javax.xml.registry.JAXRException;
import javax.xml.registry.infomodel.EmailAddress;

public class Email implements EmailAddress {
	private String address;
	private String type;

	@Override
	public String getAddress() throws JAXRException {
		return address;
	}

	@Override
	public void setAddress(String address) throws JAXRException {
		this.address = address;
	}

	@Override
	public String getType() throws JAXRException {
		return type;
	}

	@Override
	public void setType(String type) throws JAXRException {
		this.type = type;
	}
}