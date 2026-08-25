package com.manish.payments.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.manish.payments.constant.ErrorCodeEnum;
import com.manish.payments.exception.PaypalProviderException;
import com.manish.payments.pojo.CreateOrderRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentValidator {
	
	public void validateCreateOrderRequest(CreateOrderRequest createOrderRequest) {
		log.info("Validating CreateOrderRequest: {}", createOrderRequest);
		
		if(createOrderRequest == null) {
			log.error("CreateOrderRequest is null");
			
			throw new PaypalProviderException(
					ErrorCodeEnum.INVALID_REQUEST.getErrorCode(),
					ErrorCodeEnum.INVALID_REQUEST.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		if(createOrderRequest.getAmount() == null 
				|| createOrderRequest.getAmount() <= 0) {
			log.error("Invalid amount in CreateOrderRequest: {}", createOrderRequest);
			
			throw new PaypalProviderException(
					ErrorCodeEnum.INVALID_AMOUNT.getErrorCode(),
					ErrorCodeEnum.INVALID_AMOUNT.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		if(createOrderRequest.getCurrencyCode() == null 
				|| createOrderRequest.getCurrencyCode().isBlank()) {
			log.error("Invalid currency code in CreateOrderRequest: {}", createOrderRequest);
			
			throw new PaypalProviderException(
					ErrorCodeEnum.INVALID_CURRENCY.getErrorCode(),
					ErrorCodeEnum.INVALID_CURRENCY.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		if(createOrderRequest.getSuccessUrl() == null 
				|| createOrderRequest.getSuccessUrl().isBlank()) {
			log.error("Invalid success URL in CreateOrderRequest: {}", createOrderRequest);
			
			throw new PaypalProviderException(
					ErrorCodeEnum.INVALID_RETURN_URL.getErrorCode(),
					ErrorCodeEnum.INVALID_RETURN_URL.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		if(createOrderRequest.getCancelUrl() == null 
				|| createOrderRequest.getCancelUrl().isBlank()) {
			log.error("Invalid cancel URL in CreateOrderRequest: {}", createOrderRequest);
			
			throw new PaypalProviderException(
					ErrorCodeEnum.INVALID_CANCEL_URL.getErrorCode(),
					ErrorCodeEnum.INVALID_CANCEL_URL.getErrorMessage(),
					HttpStatus.BAD_REQUEST);
		}
		
		log.info("CreateOrderRequest validation passed: {}", createOrderRequest);
		
	}
}
