package com.benchmark.orders.service;

import com.benchmark.orders.dto.OrderRequest;
import com.benchmark.orders.dto.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
}
