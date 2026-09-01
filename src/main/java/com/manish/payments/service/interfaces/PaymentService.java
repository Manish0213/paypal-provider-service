package com.manish.payments.service.interfaces;

import com.manish.payments.pojo.CreateOrderRequest;
import com.manish.payments.pojo.OrderResponse;

public interface PaymentService {
	
	public OrderResponse createOrder(CreateOrderRequest createOrderRequest);
	public OrderResponse captureOrder(String orderId);
}