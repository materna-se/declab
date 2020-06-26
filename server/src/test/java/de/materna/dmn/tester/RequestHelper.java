package de.materna.dmn.tester;

import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.jupiter.api.Assertions;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

public class RequestHelper {
	public static void emitRequest(String url, String method, String token, int expectedStatus) {
		try {
			Client client = ClientBuilder.newBuilder().build();
			WebTarget target = client.target(url);
			Builder requestBuilder = target.request();

			if (token != null) {
				requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			}

			Response response = requestBuilder.method(method);

			response.close();
			client.close();

			Assertions.assertEquals(expectedStatus, response.getStatus());
		}
		catch (Exception e) {
			Assertions.fail(e);
		}
	}

	public static String emitRequest(String url, String method, String token, String entity, String mediaType, int expectedStatus, boolean trimExcessQuotesFromResponseBody) {
		try {
			Client client = ClientBuilder.newBuilder().build();
			WebTarget target = client.target(url);
			Builder requestBuilder = target.request();

			if (token != null) {
				requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			}

			Response response;

			if (entity == null || mediaType == null) {
				response = requestBuilder.method(method);
			}
			else {
				response = requestBuilder.method(method, Entity.entity(entity, mediaType), Response.class);
			}

			Assertions.assertEquals(expectedStatus, response.getStatus());

			String body = response.readEntity(String.class);

			if (trimExcessQuotesFromResponseBody) {
				body = body.replaceAll("^\"|\"$", "");
			}

			response.close();
			client.close();

			return body;
		}
		catch (Exception e) {
			Assertions.fail(e);
			return null;
		}
	}

	public static String emitRequest(String url, String method, String token, int expectedStatus, boolean trimExcessQuotesFromResponseBody) {
		try {
			Client client = ClientBuilder.newBuilder().build();
			WebTarget target = client.target(url);
			Builder requestBuilder = target.request();

			if (token != null) {
				requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			}

			Response response = requestBuilder.method(method);

			Assertions.assertEquals(expectedStatus, response.getStatus());

			String body = response.readEntity(String.class);

			if (trimExcessQuotesFromResponseBody) {
				body = body.replaceAll("^\"|\"$", "");
			}

			response.close();
			client.close();

			return body;
		}
		catch (Exception e) {
			Assertions.fail(e);
			return null;
		}
	}

	public static Object emitRequestRaw(String url, String method, String token, Object entity, String mediaType, int expectedStatus) {
		try {
			Client client = ClientBuilder.newBuilder().build();
			WebTarget target = client.target(url);
			Builder requestBuilder = target.request();

			if (token != null) {
				requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			}

			Response response;

			if (entity == null || mediaType == null) {
				response = requestBuilder.method(method);
			}
			else {
				response = requestBuilder.method(method, Entity.entity(entity, mediaType), Response.class);
			}

			Assertions.assertEquals(expectedStatus, response.getStatus());

			Object body = response.readEntity(Object.class);

			response.close();
			client.close();

			return body;
		}
		catch (Exception e) {
			Assertions.fail(e);
			return null;
		}
	}

	public static byte[] emitRequestRawResponse(String url, String method, String token, int expectedStatus) {
		try {
			Client client = ClientBuilder.newBuilder().build();
			WebTarget target = client.target(url);
			Builder requestBuilder = target.request();

			if (token != null) {
				requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			}

			Response response = requestBuilder.method(method);

			Assertions.assertEquals(expectedStatus, response.getStatus());

			byte[] body = response.readEntity(byte[].class);

			response.close();
			client.close();

			return body;
		}
		catch (Exception e) {
			Assertions.fail(e);
			return null;
		}
	}

	public static void emitRequestMultipartFormData(String url, String method, String token, String fileName, File file, int expectedStatus) {
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
		WebTarget target = client.target(url);
		Builder requestBuilder = target.request();

		if (token != null) {
			requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		}

		MultiPart multiPart = new MultiPart();
		multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
		FileDataBodyPart fileDataBodyPart = new FileDataBodyPart(fileName, file, MediaType.APPLICATION_OCTET_STREAM_TYPE);
		multiPart.bodyPart(fileDataBodyPart);

		Response response = requestBuilder.method(method, Entity.entity(multiPart, multiPart.getMediaType()));

		Assertions.assertEquals(expectedStatus, response.getStatus());

		response.close();
		client.close();
	}
}