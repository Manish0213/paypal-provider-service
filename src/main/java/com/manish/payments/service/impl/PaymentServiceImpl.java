package com.manish.payments.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.manish.payments.http.HttpRequest;
import com.manish.payments.http.HttpServiceEngine;
import com.manish.payments.paypal.res.CreateOrderResponse;
import com.manish.payments.pojo.CreateOrderRequest;
import com.manish.payments.pojo.OrderResponse;
import com.manish.payments.service.TokenService;
import com.manish.payments.service.helper.CreateOrderHelper;
import com.manish.payments.service.interfaces.PaymentService;
import com.manish.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
	
	private final TokenService tokenService;
	private final HttpServiceEngine httpServiceEngine;
	private final CreateOrderHelper createOrderHelper;
	private final JsonUtil jsonUtil;

	@Override
	public OrderResponse createOrder(CreateOrderRequest createOrderRequest) {
		log.info("Creating order with request: {}", createOrderRequest);
		
		String accessToken = tokenService.getAccessToken();
		log.info("Access token received: {}", accessToken);
		
		HttpRequest httpRequest = createOrderHelper.prepareCreateOrderHttpRequest(createOrderRequest, accessToken);
		log.info("HttpRequest prepared for create order: {}", httpRequest);
		
		ResponseEntity<String> successResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("Create order response received: {}", successResponse);
		
		CreateOrderResponse response = jsonUtil.fromJson(successResponse.getBody(), CreateOrderResponse.class);
		log.info("Create order response parsed: {}", response);
		
		OrderResponse orderResponse = createOrderHelper.convertToOrderResponse(response);
		log.info("OrderResponse created: {}", orderResponse);
		
		return orderResponse;
	}

}
