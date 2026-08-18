package com.manish.payments.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import lombok.Data;

@Data
public class HttpRequest {

    private String url;

    private HttpMethod method;

    private HttpHeaders headers;

    private Object body;
}


// private HttpHeaders headers; we have the type of headers as HttpHeaders, so its setter
// takes argument of type HttpHeaders. So we need to pass the HttpHeaders object to the setter method.

//httpRequest.setHeaders("Authorization", "Bearer " + tokenResponse); // wrong
//httpRequest.setHeaders("Content-Type", "application/json");			// wrong