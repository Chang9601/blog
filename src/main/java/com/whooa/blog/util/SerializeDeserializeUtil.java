package com.whooa.blog.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SerializeDeserializeUtil {
	private ObjectMapper objectMapper;
	
	public SerializeDeserializeUtil(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	public String serialize(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException error) {
			error.printStackTrace();
			throw new InternalError();
		}
	}
}
