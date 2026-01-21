package org.example.service.order;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import org.example.dto.order.CreateOrderDto;
import org.example.dto.order.OrderDto;
import org.example.dto.order.UpdateOrderStatusDto;
import org.example.dto.orderitem.OrderItemDto;

public interface OrderService {

    OrderDto placeAnOrder(@Valid CreateOrderDto createOrder);

    List<OrderDto> getOrders();

    OrderDto updateOrderStatus(Long id, @Valid UpdateOrderStatusDto updateOrderStatusDto);

    Set<OrderItemDto> getAllItemsForOrder(Long orderId);

    OrderItemDto getItemFromOrder(Long orderId, Long itemId);

}
