package org.example.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrderStatusDto {

    @NotBlank
    private String currentStatus;
}
