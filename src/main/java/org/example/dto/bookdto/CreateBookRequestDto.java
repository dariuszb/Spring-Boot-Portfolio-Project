package org.example.dto.bookdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Set;

public record CreateBookRequestDto(
        @NotBlank
        String title,
        @NotBlank
        String author,
        @NotBlank
        String isbn,
        @Min(0)
        BigDecimal price,
        Set<Long> categoriesIds,
        String description,
        String coverImage) {

}
