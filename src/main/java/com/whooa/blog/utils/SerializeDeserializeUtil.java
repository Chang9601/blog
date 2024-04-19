package com.whooa.blog.utils;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SerializeDeserializeUtil {
	private final ObjectMapper objectMapper;
	
	public SerializeDeserializeUtil(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	public String serialize(final Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException error) {
			error.printStackTrace();
			throw new InternalError();
		}
	}
}
