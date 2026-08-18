package com.manish.payments.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.manish.payments.paypal.res.PaypalOAuthToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpServiceEngine {
	
	private final RestClient restClient;
	
	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
		log.info("Making HTTP call in HttpServiceEngine...");
		
		ResponseEntity<String> response = restClient.method(httpRequest.getMethod())
			.uri(httpRequest.getUrl())
//			.headers(headers -> { httpRequest.getHeaders().forEach(headers::set);})
			.headers((HttpHeaders restHttpHeaders) -> restHttpHeaders.addAll(httpRequest.getHeaders()))
			.body(httpRequest.getBody())
			.retrieve()
			.toEntity(String.class);
//			.body(String.class);
		
		log.info("Received response from PayPal API: {}", response);
		
		return response;
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
