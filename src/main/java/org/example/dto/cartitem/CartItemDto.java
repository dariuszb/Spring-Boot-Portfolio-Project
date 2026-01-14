package org.example.dto.cartitem;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CartItemDto {

    @NotBlank
    private Long id;
    private Long bookId;
    private String bookTitle;
    private int quantity;
}

