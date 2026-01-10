package org.example.dto.categorydto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryDto(

        @NotBlank
        String name,
        String description
) {
}
