package com.manish.payments.constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
	
	INVALID_REQUEST("30005", "CreateOrderRequest cannot be null"),
	INVALID_AMOUNT("30006", "Amount must be greater than zero or cannot be null"),
	INVALID_CURRENCY("30007", "Currency cannot be null or empty"),
	INVALID_RETURN_URL("30008", "Return URL cannot be null or empty"),
	INVALID_CANCEL_URL("30009", "Cancel URL cannot be null or empty"),
	INVALID_CURRENCY_CODE("30010", "Currency code is invalid or not supported"),
	PAYPAL_SERVICE_UNAVAILABLE("30011", "PayPal service is currently unavailable"),
	UNEXPECTED_HTTP_SERVICE_ERROR("30012", "An unexpected error occurred while making the HTTP request"),
	PAYPAL_ERROR("30012", "<Error as Paypal>"),
	PAYPAL_UNKNOWN_ERROR("30009", "An unknown error occurred while processing the PayPal request");
	
	private final String errorCode;
	private final String errorMessage;
	
	ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}