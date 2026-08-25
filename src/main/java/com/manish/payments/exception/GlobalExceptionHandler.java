package com.manish.payments.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.manish.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@ExceptionHandler(PaypalProviderException.class)
    public ResponseEntity<ErrorResponse> handlePaypalProviderException(
            PaypalProviderException ex) {
		
        log.error("PayPal Provider Exception | errorCode: {} | message: {}",
        	    ex.getErrorCode(),
        	    ex.getErrorMessage(),
        	    ex
        	);

//        return ResponseEntity
//                .status(ex.getHttpStatus())
//                .body(ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
				ex.getErrorCode(),
				ex.getErrorMessage()
		);
        
//        return errorResponse;
        
        return ResponseEntity
				.status(ex.getHttpStatus())
				.body(errorResponse);
    }
	
}
