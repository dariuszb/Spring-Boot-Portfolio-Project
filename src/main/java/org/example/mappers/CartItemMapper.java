package org.example.mappers;

import java.util.Set;
import org.example.configuration.MapperConfiguration;
import org.example.dto.cartitem.CartItemDto;
import org.example.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfiguration.class)
public interface CartItemMapper {

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);

    Set<CartItemDto> map(Set<CartItem> cartItems);

}
