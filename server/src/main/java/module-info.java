module server {
	requires java.ws.rs;
	requires resteasy.multipart.provider;

	requires jdec;
	// TODO: org.kie.dmn should be required by jDEC.
	requires org.kie.dmn.api;
	requires org.kie.dmn.core;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;
	requires com.fasterxml.jackson.module.paramnames;

	requires org.apache.commons.io;

	requires log4j;
}