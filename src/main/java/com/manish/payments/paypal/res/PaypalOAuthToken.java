package com.manish.payments.paypal.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaypalOAuthToken {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;
}
