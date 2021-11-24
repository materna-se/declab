package de.materna.dmn.tester.servlets.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import de.materna.jdec.model.ModelNotFoundException;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {
	private static final Logger log = Logger.getLogger(GeneralExceptionMapper.class);

	@Override
	public Response toResponse(Exception e) {
		e.printStackTrace();
		log.error(e);

		//Default exceptions
		if (e instanceof NotAllowedException)
			return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();

		if (e instanceof NotFoundException || e instanceof NoSuchFileException)
			return Response.status(Response.Status.NOT_FOUND).build();

		if (e instanceof NotAuthorizedException)
			return Response.status(Response.Status.UNAUTHORIZED).build();

		if (e instanceof NotSupportedException)
			return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();

		if (e instanceof ModelNotFoundException)
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();

		//Serialization exceptions
		if (e instanceof BadRequestException || e instanceof IllegalArgumentException || e instanceof JsonParseException) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

		//Serious exceptions
		if (e instanceof IOException) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}

		if (e instanceof RuntimeException) {
			if (e.getCause() != null) {
				//Catch embedded serialization exceptions thrown by SerializationHelper
				Throwable cause = e.getCause();

				if (cause instanceof JsonParseException) {
					return Response.status(Response.Status.BAD_REQUEST).build();
				}
			}

			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}

		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
}
