package org.example.mappers;

import java.util.Set;
import java.util.stream.Collectors;
import org.example.configuration.MapperConfiguration;
import org.example.dto.cartitem.CartItemDto;
import org.example.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfiguration.class)
public interface CartItemMapper {

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);

    @Named("mapSet")
    default Set<CartItemDto> mapSet(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

}

