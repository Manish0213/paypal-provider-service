package com.manish.payments.util;

import com.manish.payments.paypal.res.error.ErrorDetail;
import com.manish.payments.paypal.res.error.PaypalErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaypalOrderUtil {
	
	private PaypalOrderUtil() {
		// Private constructor to prevent instantiation
	}
	
	public static String buildPaypalErrorMessage(PaypalErrorResponse errorResponse) {
		log.info("Extracting PayPal error summary from response: {}", errorResponse);

	    StringBuilder message = new StringBuilder();

	    append(message, errorResponse.getName());
	    append(message, errorResponse.getMessage());
	    append(message, errorResponse.getError());
	    append(message, errorResponse.getErrorDescription());

	    if (errorResponse.getDetails() != null 
	    		&& !errorResponse.getDetails().isEmpty()) {
	    	ErrorDetail detail = errorResponse.getDetails().get(0);
	    	if(detail != null) {
	            append(message, detail.getField());
	            append(message, detail.getIssue());
	            append(message, detail.getDescription());
	        }
	    }
	    
	    log.info("Constructed PayPal error summary: {}", message);
	    return message.toString();
	}

	private static void append(StringBuilder message, String value) {

	    if (value != null && !value.isBlank()) {
	        if (!message.isEmpty()) {
	            message.append(" | ");
	        }
	        message.append(value);
	    }
	}
}