package com.manish.payments.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {
	
//	@Bean
//	public RestClient restClient() {
//		return RestClient.create();
//	}
	
	@Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
	
	@Bean
	RestClient restClient(RestClient.Builder builder) {

	    ConnectionConfig connectionConfig = ConnectionConfig.custom()
	            .setConnectTimeout(Timeout.ofSeconds(10))
	            .build();

	    PoolingHttpClientConnectionManager connectionManager =
	            PoolingHttpClientConnectionManagerBuilder.create()
	                    .setDefaultConnectionConfig(connectionConfig)
	                    .setMaxConnTotal(100)
	                    .setMaxConnPerRoute(100)
	                    .build();

	    RequestConfig requestConfig = RequestConfig.custom()
	            .setConnectionRequestTimeout(Timeout.ofSeconds(10))
	            .setResponseTimeout(Timeout.ofSeconds(15))
	            .build();

	    CloseableHttpClient httpClient = HttpClients.custom()
	            .setConnectionManager(connectionManager)
	            .setDefaultRequestConfig(requestConfig)
	            .evictIdleConnections(TimeValue.ofSeconds(30))
	            .build();

	    HttpComponentsClientHttpRequestFactory requestFactory =
	            new HttpComponentsClientHttpRequestFactory(httpClient);

	    return builder
	            .requestFactory(requestFactory)
	            .build();
	}

}
