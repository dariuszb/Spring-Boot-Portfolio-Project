package org.example.mappers;

import org.example.configuration.MapperConfiguration;
import org.example.dto.order.CreateOrderDto;
import org.example.dto.order.OrderDto;
import org.example.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfiguration.class, uses = OrderItemMapper.class)
public interface OrderMapper {

    Order toEntity(CreateOrderDto createOrderDto);

    @Mapping(source = "orderItems", target = "orderItems",
            qualifiedByName = "mapSet")
    @Mapping(source = "user.id", target = "userId")
    /*@Mapping(source = "total", target = "total",
            qualifiedByName = "getTotalCost" )*/
    OrderDto toDto(Order order);

    /*
    @Mapping(source = "cartItems", target = "orderItems",
            qualifiedByName = "setMapper" )
    @Mapping(source = "cartItems", target = "total",
            qualifiedByName = "getTotalCost")
    Order fromShoppingCart(ShoppingCart shoppingCart);*/
}
