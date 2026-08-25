package com.manish.payments.http;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.manish.payments.constant.ErrorCodeEnum;
import com.manish.payments.exception.PaypalProviderException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpServiceEngine {
	
	private final RestClient restClient;
	
	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
		log.info("Making HTTP call in HttpServiceEngine...");
		
		try {
			ResponseEntity<String> response = restClient.method(httpRequest.getMethod())
				.uri(httpRequest.getUrl())
//			.headers(headers -> { httpRequest.getHeaders().forEach(headers::set);})
				.headers((HttpHeaders restHttpHeaders) -> restHttpHeaders.addAll(httpRequest.getHeaders()))
				.body(httpRequest.getBody())
				.retrieve()
				.toEntity(String.class);
//			.body(String.class);
			
			log.info("Received response from PayPal API: {}", response);
//			throw new SocketException("Connection reset");
//			throw new SocketTimeoutException("Connection reset");
			
			return response;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			
			log.error("HTTP error response received: {}", e.getMessage(), e);

		    HttpStatusCode statusCode = e.getStatusCode();

		    if (statusCode.equals(HttpStatus.SERVICE_UNAVAILABLE)
		            || statusCode.equals(HttpStatus.GATEWAY_TIMEOUT)) {

		        log.error("PayPal service is unavailable or gateway error occurred");

		        throw new PaypalProviderException(
		                ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getErrorCode(),
		                ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getErrorMessage(),
		                HttpStatus.SERVICE_UNAVAILABLE
		        );
		    }

		    String errorResponse = e.getResponseBodyAsString();
		    log.info("Error response body: {}", errorResponse);

		    return ResponseEntity
		            .status(statusCode)
		            .body(errorResponse);
			
		} catch (ResourceAccessException e) {

		    log.error("Unable to access PayPal API: {}", e.getMessage(), e);
		    
		    Throwable cause = e.getCause();

		    if (cause instanceof SocketTimeoutException) {
		        // socket problem
		    	throw new PaypalProviderException(
			            ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getErrorCode(),
			            ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getErrorMessage(),
			            HttpStatus.GATEWAY_TIMEOUT
			    );
		    } 

		    throw new PaypalProviderException(
		            ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getErrorCode(),
		            ErrorCodeEnum.PAYPAL_SERVICE_UNAVAILABLE.getErrorMessage(),
		            HttpStatus.SERVICE_UNAVAILABLE
		    );

		} catch(Exception e) {
			log.error("An error occurred while making HTTP call: {}", e.getMessage(), e);
			
			throw new PaypalProviderException(
					ErrorCodeEnum.UNEXPECTED_HTTP_SERVICE_ERROR.getErrorCode(),
					ErrorCodeEnum.UNEXPECTED_HTTP_SERVICE_ERROR.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR
					);
		}
	}

}

//class ConsumerHeaderObject implements Consumer<HttpHeaders> {
//HttpHeaders headers;
//ConsumerHeaderObject(HttpHeaders headers) {
//	this.headers = headers;
//}
//
//@Override
//public void accept(HttpHeaders restHttpHeaders) {
//	// TODO Auto-generated method stub
//	restHttpHeaders.addAll(headers);
//}
//}
