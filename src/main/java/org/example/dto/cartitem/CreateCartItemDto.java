package org.example.dto.cartitem;

import jakarta.validation.constraints.Positive;

public record CreateCartItemDto(

        Long bookId,
        @Positive
        int quantity

) {
}
