package org.example.dto.bookdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

public record CreateBookRequestDto(
        @NotBlank
        String title,
        @NotBlank
        String author,
        @NotBlank
        String isbn,
        @NotNull
        @Min(0)
        BigDecimal price,
        @NotNull
        Set<Long> categoriesIds,
        String description,
        String coverImage) {

}
