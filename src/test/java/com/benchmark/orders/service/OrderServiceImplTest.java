package com.benchmark.orders.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.benchmark.orders.dto.OrderItemRequest;
import com.benchmark.orders.dto.OrderRequest;
import com.benchmark.orders.dto.OrderResponse;
import com.benchmark.orders.entity.Order;
import com.benchmark.orders.exception.InvalidOrderException;
import com.benchmark.orders.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    private OrderServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OrderServiceImpl(orderRepository);
    }

    @Test
    void calculatesTotalAndQuantityAndLinksItems() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        OrderRequest request = new OrderRequest("C123", List.of(
                new OrderItemRequest("P1", 2, new BigDecimal("10.50")),
                new OrderItemRequest("P2", 1, new BigDecimal("5.00"))));

        OrderResponse response = service.createOrder(request);

        assertThat(response.totalAmount()).isEqualByComparingTo("26.00");
        assertThat(response.totalAmount().scale()).isEqualTo(2);
        assertThat(response.itemsCount()).isEqualTo(3);
        assertThat(response.orderId()).startsWith("ORD-").hasSize(40);
        assertThat(response.createdAt()).isNotNull();
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order saved = captor.getValue();
        assertThat(saved.getItems()).hasSize(2).allSatisfy(item -> assertThat(item.getOrder()).isSameAs(saved));
    }

    @Test
    void rejectsTotalOutsideNumericRangeBeforeSaving() {
        OrderRequest request = new OrderRequest("C123",
                List.of(new OrderItemRequest("P1", 2, new BigDecimal("99999999.99"))));
        assertThatThrownBy(() -> service.createOrder(request)).isInstanceOf(InvalidOrderException.class);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void rejectsCountOutsideIntegerRangeBeforeSaving() {
        OrderRequest request = new OrderRequest("C123", List.of(
                new OrderItemRequest("P1", Integer.MAX_VALUE, new BigDecimal("0.01")),
                new OrderItemRequest("P2", 1, new BigDecimal("0.01"))));
        assertThatThrownBy(() -> service.createOrder(request)).isInstanceOf(InvalidOrderException.class);
        verify(orderRepository, never()).save(any());
    }
}
