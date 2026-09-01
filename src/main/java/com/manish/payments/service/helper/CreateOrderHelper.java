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
import com.manish.payments.paypal.req.Amount;
import com.manish.payments.paypal.req.ExperienceContext;
import com.manish.payments.paypal.req.OrderRequest;
import com.manish.payments.paypal.req.PaymentSource;
import com.manish.payments.paypal.req.Paypal;
import com.manish.payments.paypal.req.PurchaseUnit;
import com.manish.payments.paypal.res.PaypalOrderResponse;
import com.manish.payments.paypal.res.error.PaypalErrorResponse;
import com.manish.payments.pojo.CreateOrderRequest;
import com.manish.payments.pojo.OrderResponse;
import com.manish.payments.util.JsonUtil;
import com.manish.payments.util.PaypalOrderUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrderHelper {

	private final JsonUtil jsonUtil;
	
	@Value("${paypal.create.order.url}")
	private String createOrderUrl;
	
	public HttpRequest prepareCreateOrderHttpRequest(CreateOrderRequest createOrderRequest, String accessToken) {
			log.info("Preparing HttpRequest for create order... || createOrderRequest: {}, AccessToken: {}", createOrderRequest, accessToken);
		
			// prepare headers for the request
			HttpHeaders headers = prepareHeader(accessToken);
			
			// prepare request body for the request
			String orderRequestJson = prepareRequestBodyAsJson(createOrderRequest);
			
			// prepare the HttpRequest object with the URL, method, headers, and body
			HttpRequest httpRequest = new HttpRequest();
			httpRequest.setMethod(HttpMethod.POST);
			httpRequest.setUrl(createOrderUrl);
			httpRequest.setHeaders(headers);
			httpRequest.setBody(orderRequestJson);
			log.info("HttpRequest prepared for create order: {}", httpRequest);
			
			return httpRequest;
	}

	private String prepareRequestBodyAsJson(CreateOrderRequest createOrderRequest) {
		log.info("Preparing request body for create order... || createOrderRequest: {}", createOrderRequest);
		
		// prepare form data for the request body This is wrong because we are sending JSON body, not form data. So we need to prepare JSON body instead of form data.
//		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//		formData.add("currencyCode", createOrderRequest.getCurrencyCode());
		
		Amount amount = new Amount();
		amount.setCurrencyCode(createOrderRequest.getCurrencyCode());
		
		// add switch case for currency code to handle different currency codes and their respective decimal places - JPY, USD, INR. For now we will handle only JPY, USD and INR. For other currency codes we will throw an exception.
		switch (createOrderRequest.getCurrencyCode()) {
			case Constant.CURRENCY_JPY:
				amount.setValue(String.format(Constant.DECIMAL_FORMAT_0F, createOrderRequest.getAmount()));
				break;
			case Constant.CURRENCY_USD:
			case Constant.CURRENCY_INR:
				amount.setValue(String.format(Constant.DECIMAL_FORMAT_2F, createOrderRequest.getAmount()));
				break;
			default:
				throw new PaypalProviderException(
						ErrorCodeEnum.INVALID_CURRENCY_CODE.getErrorCode(),
						ErrorCodeEnum.INVALID_CURRENCY_CODE.getErrorMessage() + ": " + createOrderRequest.getCurrencyCode(),
						HttpStatus.BAD_REQUEST
						);
		}
		
//		convert amount to string with 2 decimal places
//		String amountStr = String.format("%.2f", createOrderRequest.getAmount());
//		amount.setValue(amountStr);
		
		PurchaseUnit purchaseUnit = new PurchaseUnit();
		purchaseUnit.setAmount(amount);
		
		ExperienceContext experienceContext = new ExperienceContext();
		experienceContext.setPaymentMethodPreference(Constant.IMMEDIATE_PAYMENT_REQUIRED);
		experienceContext.setLandingPage(Constant.LANDING_PAGE_LOGIN);
		experienceContext.setShippingPreference(Constant.SHIPPING_REF_NO_SHIPPING);
		experienceContext.setUserAction(Constant.USER_ACTION_PAY_NOW);
		experienceContext.setReturnUrl(createOrderRequest.getSuccessUrl());
		experienceContext.setCancelUrl(createOrderRequest.getCancelUrl());
		
		Paypal paypal = new Paypal();
		paypal.setExperienceContext(experienceContext);
		
		PaymentSource paymentSource = new PaymentSource();
		paymentSource.setPaypal(paypal);
		
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setIntent(Constant.INTENT_CAPTURE);
		orderRequest.setPurchaseUnits(java.util.Collections.singletonList(purchaseUnit));
		orderRequest.setPaymentSource(paymentSource);
		
		// convert orderRequest to JSON string
		String orderRequestJson = jsonUtil.toJson(orderRequest);
		return orderRequestJson;
	}

	private HttpHeaders prepareHeader(String tokenResponse) {
		// prepare headers for the request
		HttpHeaders headers = new HttpHeaders();
//		headers.set("Authorization", "Bearer " + tokenResponse);
		headers.setBearerAuth(tokenResponse); // Use setBearerAuth method to set the Authorization header
//		headers.set("Content-Type", "application/json");
		headers.setContentType(MediaType.APPLICATION_JSON); // Use setContentType method to set the Content-Type header
		
		// prepare header for payment-requst-id => UUID for idempotency
		String uuid = java.util.UUID.randomUUID().toString();
		log.info("Generated UUID for PayPal-Request-Id header: {}", uuid);
		headers.set(Constant.PAYPAL_REQUEST_ID, uuid);
		
		return headers;
	}
	
	private OrderResponse convertToOrderResponse(PaypalOrderResponse paypalOrderResponse) {

	    OrderResponse orderResponse = new OrderResponse();

	    orderResponse.setOrderId(paypalOrderResponse.getId());
	    orderResponse.setPaypalStatus(paypalOrderResponse.getStatus());

	    paypalOrderResponse.getLinks().stream()
	            .filter(link -> "payer-action".equals(link.getRel()))
	            .findFirst()
	            .ifPresent(link -> orderResponse.setRedirectUrl(link.getHref()));

	    return orderResponse;
	}
	
	public OrderResponse processResponse(ResponseEntity<String> httpResponse) {
		log.info("Processing PayPal response in PaymentServiceImpl "+ "httpResponse:{}", httpResponse);
		
		if(httpResponse.getStatusCode().is2xxSuccessful()) {
			log.info("HTTP call successful with status code: {}", httpResponse.getStatusCode());
			
			PaypalOrderResponse successResponse = jsonUtil.fromJson(httpResponse.getBody(), PaypalOrderResponse.class);
			log.info("Create order response parsed: {}", httpResponse);
			
			OrderResponse orderResponse = convertToOrderResponse(successResponse);
			log.info("OrderResponse created: {}", orderResponse);
			
			if(orderResponse != null
					&& orderResponse.getOrderId() != null
					&& !orderResponse.getOrderId().isEmpty()
					&& orderResponse.getPaypalStatus() != null
					&& orderResponse.getPaypalStatus().equalsIgnoreCase(Constant.PAYER_ACTION_REQUIRED)
					&& orderResponse.getRedirectUrl() != null
					&& !orderResponse.getRedirectUrl().isEmpty()) {
				
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
					ErrorCodeEnum.PAYPAL_ERROR.getErrorCode(),
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
}
