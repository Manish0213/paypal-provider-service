package com.manish.payments.service.impl;

import org.springframework.stereotype.Service;

import com.manish.payments.service.TokenService;
import com.manish.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
	
	private final TokenService tokenService;

	@Override
	public String createOrder() {
		
		String tokenResponse = tokenService.getAccessToken();
		log.info("Access token received: {}", tokenResponse);
		
		/* TODO
		 1. Call Paypal Oauth API to get access token
		 2. finalize the Request and Response for Create Order API
		 3. Call Paypal Create Order API to create order
		 4. Return the response to the client
		 */
		return "Order created successfully! " + tokenResponse;
	}

}
