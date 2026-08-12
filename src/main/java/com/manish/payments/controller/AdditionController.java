package com.manish.payments.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/addition")
@Slf4j
public class AdditionController {
	
	@Value("${mytestkey}")
	private String myKey;
	
	@PostMapping("/add")
	public int add(int a, int b) {
		log.info("Adding {} and {}", a, b);
		return a + b;
	}
	
	@PostConstruct
	private void init() {
	log.info("Value read from property myKey:{}", myKey);
	}
}