package com.manish.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manish.payments.pojo.CreateOrderRequest;
import com.manish.payments.pojo.OrderResponse;
import com.manish.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;
	
	//finilize request endpoint as RestAPI Standards
	@PostMapping("/createOrder")
	public OrderResponse createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
		log.info("PaymentController: createOrder() method called");
		
		OrderResponse response = paymentService.createOrder(createOrderRequest);
		log.info("PaymentController: createOrder() method completed with response: {}", response);
		
		return response;
	}

}
