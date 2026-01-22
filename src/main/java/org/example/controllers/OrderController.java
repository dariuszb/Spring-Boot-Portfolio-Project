package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.dto.order.CreateOrderDto;
import org.example.dto.order.OrderDto;
import org.example.dto.order.UpdateOrderStatusDto;
import org.example.dto.orderitem.OrderItemDto;
import org.example.service.order.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management",
        description = "Endpoints for order management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Get user's order history",
            description = "Get user's order history")
    public List<OrderDto> getOrders() {
        return orderService.getOrders();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place an order",
            description = "Place an order")
    public OrderDto placeAnOrder(
            @RequestBody @Valid CreateOrderDto createOrder) {
        return orderService.placeAnOrder(createOrder);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update order status",
            description = "Update order status")
    public OrderDto updateOrderStatus(@PathVariable Long id,
            @RequestBody @Valid UpdateOrderStatusDto updateOrderStatusDto) {
        return orderService.updateOrderStatus(id, updateOrderStatusDto);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get items for specific order",
            description = "Get items for specific order")
    public Set<OrderItemDto> getAllItemsForOrder(@PathVariable Long orderId) {
        return orderService.getAllItemsForOrder(orderId);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get specific OrderItem from an order",
            description = "Get specific OrderItem from an order")

    public OrderItemDto getItemOfOrder(@PathVariable Long orderId,
                                   @PathVariable Long itemId) {
        return orderService.getItemFromOrder(orderId, itemId);
    }
}
