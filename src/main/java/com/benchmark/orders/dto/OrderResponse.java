package com.benchmark.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        String orderId,
        String customerId,
        BigDecimal totalAmount,
        Integer itemsCount,
        LocalDateTime createdAt) {
}
