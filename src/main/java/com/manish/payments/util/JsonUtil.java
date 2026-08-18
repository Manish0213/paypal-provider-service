package com.manish.payments.util;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonUtil {
	private final ObjectMapper objectMapper;
	
	public <T> T fromJson(String json, Class<T> clazz) {
		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			log.info("Failed to convert JSON to object: {}", e.getMessage());
			throw new RuntimeException("Failed to convert JSON to object", e);
		}
	}
	
	public String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			log.info("Failed to convert object to JSON: {}", e.getMessage());
			throw new RuntimeException("Failed to convert object to JSON", e);
		}
	}
	
}
