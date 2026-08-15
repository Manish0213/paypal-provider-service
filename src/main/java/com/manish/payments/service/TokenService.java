package com.manish.payments.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.manish.payments.constant.Constant;
import com.manish.payments.http.HttpRequest;
import com.manish.payments.http.HttpServiceEngine;
import com.manish.payments.paypal.PaypalOAuthToken;
import com.manish.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
	
	private final HttpServiceEngine httpServiceEngine;
	private final JsonUtil jsonUtil;
	private static String accessToken;
	
	@Value("${paypal.client.id}")
	String clientId;
	@Value("${paypal.client.secret}")
	String clientSecret;
	
	@Value("${paypal.oauth.token.url}")
	String tokenUrl;
	
	public String getAccessToken() {
		log.info("Getting access token in TokenService...");
		
		if(accessToken != null) {
			log.info("Access token already exists, returning cached token.");
			return accessToken;
		}
		
		log.info("Access token not found, preparing HTTP request to get new token...");
		
		HttpRequest httpRequest = prepareHttpRequest();
		log.info("HttpRequest prepared for token request: {}", httpRequest);
		
		ResponseEntity<String> tokenResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("Token response received: {}", tokenResponse);
		
		String tokenBody = tokenResponse.getBody();
		log.info("Token response body: {}", tokenBody);
		
		PaypalOAuthToken token = jsonUtil.fromJson(tokenBody, PaypalOAuthToken.class);
		log.info("Parsed token response: {}", token);
		
		accessToken = token.getAccessToken();
		
		return accessToken;
	}

	private HttpRequest prepareHttpRequest() {
		
		// prepare HTTP headers for the request
//		String encodedCredentials = encode();
//		
//		Map<String, String> httHeaders = new HashMap<>();
//		httHeaders.put(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials);
//		httHeaders.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		
		// prepare HTTP headers for the request
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBasicAuth(clientId, clientSecret);
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		// prepare form data for the request body
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add(Constant.GRANT_TYPE, Constant.CLIENT_CREDENTIALS);
		
		// prepare the HttpRequest object with the URL, method, headers, and body
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(tokenUrl);
		httpRequest.setMethod(HttpMethod.POST);
		httpRequest.setHeaders(httpHeaders);
		httpRequest.setBody(formData);
		log.info("Constructed HttpRequest for OAuth token: {}", httpRequest);
		
		return httpRequest;
	}

//	private String encode() {
//		String credentials = clientId + ":" + clientSecret;
//		String encodedCredentials = Base64.getEncoder()
//		        .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
//		return encodedCredentials;
//	}
	
}
