package de.materna.dmn.tester.servlets.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.DefaultOptionsMethodException;

@Provider
public class DefaultOptionsMethodExceptionMapper implements ExceptionMapper<DefaultOptionsMethodException> {
	@Override
	public Response toResponse(DefaultOptionsMethodException e) {
		// Used by JAX-RS to return OPTIONS, leave this be.
		return e.getResponse();
	}
}