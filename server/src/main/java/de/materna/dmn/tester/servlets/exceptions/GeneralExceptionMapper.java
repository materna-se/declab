package de.materna.dmn.tester.servlets.exceptions;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.github.javaparser.utils.Log;

import de.materna.jdec.model.ModelNotFoundException;

@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception>{
	private static final Logger log = Logger.getLogger(GeneralExceptionMapper.class);
	
	@Override
    public Response toResponse(Exception e) {
		
		//Default exceptions
		if(e instanceof NotAllowedException)
			return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
		
		if(e instanceof NotFoundException || e instanceof NoSuchFileException)
	        return Response.status(Response.Status.NOT_FOUND).build();
		
		if(e instanceof NotAuthorizedException)
	        return Response.status(Response.Status.UNAUTHORIZED).build();

		if(e instanceof NotSupportedException)
	        return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();

		if(e instanceof ModelNotFoundException) 
	        return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		
		//Serialization exceptions
		if(		e instanceof BadRequestException 			||
				e instanceof IllegalArgumentException 		||
				e instanceof JsonMappingException 			||
				e instanceof JsonParseException 			||
				e instanceof UnrecognizedPropertyException 	||
				e instanceof StringIndexOutOfBoundsException)  {
	        return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		
		//Serious exceptions
		if(e instanceof IOException) {
			Log.error(e);
	    	e.printStackTrace();
	    	
	        return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
			
		if(e instanceof RuntimeException) {
			if(e.getCause() != null) {
				//Catch embedded serialization exceptions thrown by SerializationHelper
				Throwable cause = e.getCause();
				
				if(cause instanceof JsonMappingException || cause instanceof JsonParseException || cause instanceof UnrecognizedPropertyException) {
					log.error(e);
					return Response.status(Response.Status.BAD_REQUEST).build();
				}
			}
			
			log.error(e);
			e.printStackTrace();
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
		}
		
		log.error(e);
		e.printStackTrace();
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
