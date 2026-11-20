package com.benchmark.orders.service;

import com.benchmark.orders.dto.OrderItemRequest;
import com.benchmark.orders.dto.OrderRequest;
import com.benchmark.orders.dto.OrderResponse;
import com.benchmark.orders.entity.Order;
import com.benchmark.orders.exception.InvalidOrderException;
import com.benchmark.orders.mapper.OrderMapper;
import com.benchmark.orders.repository.OrderRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final BigDecimal MAX_TOTAL = new BigDecimal("99999999.99");
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        BigDecimal total = BigDecimal.ZERO;
        long count = 0;
        for (OrderItemRequest item : request.items()) {
            count += item.quantity();
            if (count > Integer.MAX_VALUE) {
                throw new InvalidOrderException("La suma de cantidades excede el máximo permitido");
            }
            total = total.add(item.price().multiply(BigDecimal.valueOf(item.quantity())));
        }
        total = total.setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(MAX_TOTAL) > 0) {
            throw new InvalidOrderException("El total excede el máximo permitido");
        }

        Order order = OrderMapper.toEntity(request);
        order.setOrderId("ORD-" + UUID.randomUUID());
        order.setTotalAmount(total);
        order.setItemsCount((int) count);
        order.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        return OrderMapper.toResponse(orderRepository.save(order));
    }
}
