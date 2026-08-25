package com.manish.payments.paypal.res.error;

import lombok.Data;

@Data
public class ErrorLink {
	private String href;
    private String rel;
    private String encType;
}