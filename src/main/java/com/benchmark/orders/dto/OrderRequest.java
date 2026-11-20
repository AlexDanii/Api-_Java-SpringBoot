package com.benchmark.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record OrderRequest(
        @NotBlank @Size(max = 50) String customerId,
        @NotEmpty List<@NotNull @Valid OrderItemRequest> items) {
}
