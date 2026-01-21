package org.example.dto.order;

import jakarta.validation.constraints.NotNull;

public record CreateOrderDto(

        @NotNull
        String shippingAddress

) {
}
