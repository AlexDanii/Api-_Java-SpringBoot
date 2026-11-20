package com.benchmark.orders.mapper;

import com.benchmark.orders.dto.OrderItemRequest;
import com.benchmark.orders.dto.OrderRequest;
import com.benchmark.orders.dto.OrderResponse;
import com.benchmark.orders.entity.Order;
import com.benchmark.orders.entity.OrderItem;

public final class OrderMapper {
    private OrderMapper() {
    }

    public static Order toEntity(OrderRequest request) {
        Order order = Order.builder().customerId(request.customerId()).build();
        for (OrderItemRequest item : request.items()) {
            order.addItem(OrderItem.builder()
                    .productId(item.productId())
                    .quantity(item.quantity())
                    .price(item.price())
                    .build());
        }
        return order;
    }

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(order.getOrderId(), order.getCustomerId(), order.getTotalAmount(),
                order.getItemsCount(), order.getCreatedAt());
    }
}
