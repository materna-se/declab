package de.materna.dmn.tester.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;

public class SerializationHelper {
	private static SerializationHelper instance;

	private ObjectMapper objectMapper = new ObjectMapper().registerModules(new ParameterNamesModule(), new JavaTimeModule(), new Jdk8Module());

	private SerializationHelper() {
	}

	public static synchronized SerializationHelper getInstance() {
		if (instance == null) {
			instance = new SerializationHelper();
		}
		return instance;
	}

	public Object toClass(String json, Class<?> clazz) throws RuntimeException {
		try {
			return objectMapper.readValue(json, clazz);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T toClass(String json, TypeReference<T> typeReference) throws RuntimeException {
		try {
			return objectMapper.readValue(json, typeReference);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String toJSON(Object object) throws RuntimeException {
		try {
			return objectMapper.writeValueAsString(object);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
