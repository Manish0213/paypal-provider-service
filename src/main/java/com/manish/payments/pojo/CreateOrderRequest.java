package com.manish.payments.pojo;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String currencyCode;
    private Double amount;
    private String successUrl;
    private String cancelUrl;
}