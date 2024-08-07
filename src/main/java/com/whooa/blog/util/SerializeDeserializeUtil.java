package com.whooa.blog.util;

import java.io.IOException;

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
	
	public static String serializeToString(Object object) {
		try {
			return objectMapper().writeValueAsString(object);
		} catch (JsonProcessingException exception) {
			logger.error("SerializeDeserializeUtil.serializeToString(): {}", exception.getMessage());
			throw new InternalError();
		}
	}
	
	public static byte[] serializeToByte(Object object) {
		try {
			return objectMapper().writeValueAsBytes(object);
		} catch (JsonProcessingException exception) {
			logger.error("SerializeDeserializeUtil.serializeToByte(): {}", exception.getMessage());
			throw new InternalError();
		}
	}
	
	public static <T> T deserializeFromJson(String json, Class<T> clazz) {
		try {
			return objectMapper().readValue(json, clazz);
		} catch (JsonProcessingException exception) {
			logger.error("SerializeDeserializeUtil.deserialize(): {}", exception.getMessage());
			throw new InternalError();
		}
	}
	
	public static <T> T deserializeFromByte(byte[] bytes, Class<T> clazz) {
		try {
			return objectMapper().readValue(bytes, clazz);
		} catch (IOException exception) {
			logger.error("SerializeDeserializeUtil.deserialize(): {}", exception.getMessage());
			throw new InternalError();
		}
	}
}