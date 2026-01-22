package org.example.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import org.example.dto.orderitem.OrderItemDto;
import org.example.model.enums.Status;

@Data
public class OrderDto {

    private Long id;
    private Long userId;
    private Set<OrderItemDto> orderItems; //private Set<OrderItemDto> orderItems;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private Status status;
}
