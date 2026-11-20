package com.benchmark.orders.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record OrderItemRequest(
        @NotBlank @Size(max = 50) String productId,
        @NotNull @Positive Integer quantity,
        @NotNull @Positive @Digits(integer = 8, fraction = 2) BigDecimal price) {
}
