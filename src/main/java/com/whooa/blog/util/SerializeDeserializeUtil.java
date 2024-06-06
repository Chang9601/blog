package com.whooa.blog.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializeDeserializeUtil {
	private static Logger logger = LoggerFactory.getLogger(SerializeDeserializeUtil.class);

	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static ObjectMapper objectMapper() {
		return objectMapper;
	}
	
	public static String serialize(Object object) {
		try {
			return objectMapper().writeValueAsString(object);
		} catch (JsonProcessingException exception) {
			logger.error("SerializeDeserializeUtil.serialize(): {}", exception.getMessage());
			throw new InternalError();
		}
	}
	
	public static <T> T deserialize(String json, Class<T> className) {
		try {
			return objectMapper().readValue(json, className);
		} catch (JsonProcessingException exception) {
			logger.error("SerializeDeserializeUtil.deserialize(): {}", exception.getMessage());
			throw new InternalError();
		}
	}
}