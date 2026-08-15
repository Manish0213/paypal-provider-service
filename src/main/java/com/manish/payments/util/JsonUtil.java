package com.manish.payments.util;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class JsonUtil {
	private final ObjectMapper objectMapper;
	
	// create a class ton convert json string to object and object to json string
	public <T> T fromJson(String json, Class<T> clazz) {
		try {
			return objectMapper.readValue(json, clazz);
			
		} catch (Exception e) {
			throw new RuntimeException("Failed to convert JSON to object", e);
		}
	}
	
	public String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			throw new RuntimeException("Failed to convert object to JSON", e);
		}
	}
	
}
