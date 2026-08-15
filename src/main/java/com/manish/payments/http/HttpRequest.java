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
