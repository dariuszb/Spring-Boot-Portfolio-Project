package org.example.mappers;

import org.example.configuration.MapperConfiguration;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfiguration.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems",
            qualifiedByName = "mapSet")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

}
