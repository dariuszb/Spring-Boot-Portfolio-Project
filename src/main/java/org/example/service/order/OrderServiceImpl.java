package org.example.service.order;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.dto.order.CreateOrderDto;
import org.example.dto.order.OrderDto;
import org.example.dto.order.UpdateOrderStatusDto;
import org.example.dto.orderitem.OrderItemDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.mappers.OrderItemMapper;
import org.example.mappers.OrderMapper;
import org.example.model.CartItem;
import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.model.ShoppingCart;
import org.example.model.enums.Status;
import org.example.repository.order.OrderRepository;
import org.example.repository.orderitem.OrderItemRepository;
import org.example.repository.shoppingcart.ShoppingCartRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public OrderDto placeAnOrder(CreateOrderDto createOrder) {

        String userNameByAuthentication = getUserNameByAuthentication();
        ShoppingCart userShoppingCart = shoppingCartRepository
                .findByUserEmail(userNameByAuthentication)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found"));

        OrderDto orderCart = createOrderCart(userShoppingCart,
                createOrder);
        userShoppingCart.getCartItems().clear();
        shoppingCartRepository.save(userShoppingCart);

        return orderCart;

    }

    @Override
    public List<OrderDto> getOrders() {
        List<Order> orders = orderRepository
                .findAllOrdersByUserEmail(getUserNameByAuthentication());
        return orders
                .stream()
                .map(orderMapper::toDto)
                .toList();

    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long id,
                                      UpdateOrderStatusDto updateOrderStatusDto) {
        Status status = updateOrderStatusDto.getStatus();

        Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Order with id " + id + " not found"
                    ));
        order.setStatus(status);

        return orderMapper.toDto(orderRepository.save(order));

    }

    @Override
    public Set<OrderItemDto> getAllItemsForOrder(Long orderId) {

        isOrderBelongToAuthUser(orderId);

        return orderRepository.getAllItemsForOrder(orderId)
                    .stream()
                    .map(orderItemMapper::toDto)
                    .collect(Collectors.toSet());

    }

    @Override
    public OrderItemDto getItemFromOrder(Long orderId, Long itemId) {
        isOrderBelongToAuthUser(orderId);
        return orderItemMapper.toDto(orderItemRepository.getItemFromOrder(orderId, itemId));
    }

    private OrderDto createOrderCart(ShoppingCart shoppingCart,
                                  CreateOrderDto createOrderDto) {
        Order order = new Order();

        order.setUser(shoppingCart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(createOrderDto.shippingAddress());
        order.setTotal(BigDecimal.ZERO);
        order.setStatus(Status.PENDING);

        orderRepository.save(order);

        Set<OrderItem> collect = shoppingCart
                .getCartItems()
                .stream()
                .map(m -> fromCartItemToOrderItem(order, m))
                .collect(Collectors.toSet());

        order.setOrderItems(collect);

        order.setTotal(collect.stream()
                        .map(m -> m.getPrice().multiply(
                                BigDecimal.valueOf(m.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        orderRepository.save(order);

        return orderMapper.toDto(order);

    }

    private OrderItem fromCartItemToOrderItem(Order order, CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setBook(cartItem.getBook());
        orderItem.setPrice(cartItem.getBook().getPrice());
        orderItem.setOrder(order);
        orderItemRepository.save(orderItem);
        return orderItem;
    }

    private String getUserNameByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        return authentication.getName();
    }

    private void isOrderBelongToAuthUser(Long orderId) {
        if (!orderRepository.getUserEmailByOrderId(orderId)
                .equals(getUserNameByAuthentication())) {
            throw new AccessDeniedException(
                    "Order with id " + orderId + " for"
                            + " authenticated user was not found");
        }
    }

}
