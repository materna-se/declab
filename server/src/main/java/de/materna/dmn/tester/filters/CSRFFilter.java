package de.materna.dmn.tester.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class CSRFFilter implements ContainerResponseFilter {
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
		MultivaluedMap<String, Object> responseHeaders = responseContext.getHeaders();
		MultivaluedMap<String, String> requestHeaders = requestContext.getHeaders();

		responseHeaders.putSingle("Access-Control-Allow-Origin", "*");

		if (requestContext.getMethod().equals("OPTIONS")) {
			String requestedMethods = requestHeaders.getFirst("Access-Control-Request-Method");
			if (requestedMethods != null) {
				responseHeaders.putSingle("Access-Control-Allow-Methods", requestedMethods);
			}

			String requestedHeaders = requestHeaders.getFirst("Access-Control-Request-Headers");
			if (requestedHeaders != null) {
				responseHeaders.putSingle("Access-Control-Allow-Headers", requestedHeaders);
			}
		}
	}
}