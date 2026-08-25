package com.manish.payments.paypal.res.error;

import lombok.Data;

@Data
public class ErrorDetail {
	private String field;
    private String value;
    private String location;
    private String issue;
    private String description;
}
