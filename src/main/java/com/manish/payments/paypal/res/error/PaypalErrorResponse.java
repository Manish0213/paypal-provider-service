package com.manish.payments.paypal.res.error;

import java.util.List;

import lombok.Data;

@Data
public class PaypalErrorResponse {
	
	private String name;
    private String message;
    private String debugId;
    private List<ErrorDetail> details;
    private List<ErrorLink> links;

    private String error;
    private String errorDescription;
}