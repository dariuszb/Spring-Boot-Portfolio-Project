package org.example.dto.order;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderDto(

        @NotBlank
        String shippingAddress

) {
}
