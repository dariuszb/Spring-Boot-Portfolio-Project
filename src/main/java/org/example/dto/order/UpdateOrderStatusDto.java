package org.example.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.model.enums.Status;

@Data
public class UpdateOrderStatusDto {

    @NotNull
    private Status status;
}
