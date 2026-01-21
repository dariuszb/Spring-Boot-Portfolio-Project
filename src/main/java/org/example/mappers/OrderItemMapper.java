package org.example.mappers;

import java.util.Set;
import java.util.stream.Collectors;
import org.example.configuration.MapperConfiguration;
import org.example.dto.orderitem.OrderItemDto;
import org.example.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfiguration.class)
public interface OrderItemMapper {

    OrderItem toEntity(OrderItemDto orderDto);

    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);

    @Named("mapSet")
    default Set<OrderItemDto> mapSet(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }
    /*

}

    /*
    @Named("setMapper")
    default Set<OrderItem> toOrderItemSet (Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::fromCartItemToOrderItem)
                .collect(Collectors.toSet());

    }
    @Name("getTotalCost")
    default BigDecimal getTotalCost(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }*/
}

