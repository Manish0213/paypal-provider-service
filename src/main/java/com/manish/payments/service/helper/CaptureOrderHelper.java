package com.manish.payments.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.manish.payments.constant.Constant;
import com.manish.payments.constant.ErrorCodeEnum;
import com.manish.payments.exception.PaypalProviderException;
import com.manish.payments.http.HttpRequest;
import com.manish.payments.paypal.res.PaypalOrderResponse;
import com.manish.payments.paypal.res.error.PaypalErrorResponse;
import com.manish.payments.pojo.OrderResponse;
import com.manish.payments.util.JsonUtil;
import com.manish.payments.util.PaypalOrderUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaptureOrderHelper {
	
	@Value("${paypal.capture.order.url}")
	private String captureOrderUrl;
	
	private final JsonUtil jsonUtil;

	public HttpRequest prepareCaptureOrderHttpRequest(String orderId, String accessToken) {
		log.info("Preparing HttpRequest for create order... || orderId: {}, AccessToken: {}", orderId, accessToken);
		
		// prepare headers for the request
		HttpHeaders headers = prepareHeader(accessToken);
		
		// prepare request body
		String requestBody = ""; // Capture order request does not require a body
		
		// prepare the URL for capturing the order
		String url = captureOrderUrl.replace("{id}", orderId);
		
		// prepare the HttpRequest object with the URL, method, headers, and body
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setMethod(HttpMethod.POST);
		httpRequest.setUrl(url);
		httpRequest.setHeaders(headers);
		httpRequest.setBody(requestBody);
		log.info("HttpRequest prepared for capture order: {}", httpRequest);
		
		return httpRequest;
	}
	
	private HttpHeaders prepareHeader(String tokenResponse) {
		// prepare headers for the request
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(tokenResponse); 
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		// prepare header for payment-requst-id => UUID for idempotency
		String uuid = java.util.UUID.randomUUID().toString();
		log.info("Generated UUID for PayPal-Request-Id header: {}", uuid);
		headers.set(Constant.PAYPAL_REQUEST_ID, uuid);
		
		return headers;
	}

	public OrderResponse processResponse(ResponseEntity<String> httpResponse) {
		log.info("Processing PayPal response in PaymentServiceImpl "+ "httpResponse:{}", httpResponse);
		
		if(httpResponse.getStatusCode().is2xxSuccessful()) {
			log.info("HTTP call successful with status code: {}", httpResponse.getStatusCode());
			
			PaypalOrderResponse successResponse = jsonUtil.fromJson(httpResponse.getBody(), PaypalOrderResponse.class);
			log.info("Create order response parsed: {}", successResponse);
			
			OrderResponse orderResponse = convertToOrderResponse(successResponse);
			log.info("OrderResponse created: {}", orderResponse);
			
			if(orderResponse != null
					&& orderResponse.getOrderId() != null
					&& !orderResponse.getOrderId().isEmpty()
					&& orderResponse.getPaypalStatus() != null
					&& orderResponse.getPaypalStatus().equalsIgnoreCase(Constant.COMPLETED)) {
				
				log.info("Valid OrderResponse received: {}", orderResponse);
				return orderResponse;
			}
			
			log.error("Invalid OrderResponse received: {}", orderResponse);
		}
		
		if(httpResponse.getStatusCode().is4xxClientError() 
				|| httpResponse.getStatusCode().is5xxServerError()) {
			log.error("Recieved 4xx, 5xx error response from PayPal service");
			
			PaypalErrorResponse errorResponse = jsonUtil.fromJson(httpResponse.getBody(), PaypalErrorResponse.class);
			log.info("Converted error response JSON to PaypalErrorResponse object: {}", errorResponse);
			
			String errorCode = ErrorCodeEnum.PAYPAL_ERROR.getErrorCode();
			String errorMessage = PaypalOrderUtil.buildPaypalErrorMessage(errorResponse);
			log.info("build error message: {}", errorMessage);
			
			int statusCode = httpResponse.getStatusCode().value();
			HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
			
			throw new PaypalProviderException(
					errorCode,
					errorMessage,
					HttpStatus.valueOf(httpResponse.getStatusCode().value())
					);
		}
		
		log.error("Unexpected response from PayPal service. "
				+ "httpResponse: {}", httpResponse);
		
		throw new PaypalProviderException(
				ErrorCodeEnum.PAYPAL_UNKNOWN_ERROR.getErrorCode(),
				ErrorCodeEnum.PAYPAL_UNKNOWN_ERROR.getErrorMessage(),
				HttpStatus.BAD_GATEWAY
				);
	}

	private OrderResponse convertToOrderResponse(PaypalOrderResponse successResponse) {
		log.info("Converting PaypalOrderResponse to OrderResponse");
		
		OrderResponse orderResponse = new OrderResponse();
		orderResponse.setOrderId(successResponse.getId());
		orderResponse.setPaypalStatus(successResponse.getStatus());
		
		log.info("PaypalOrderResponse is converted to OrderResponse: {}", orderResponse);
		return orderResponse;
	}

}
