package org.example.mappers;

import org.example.configuration.MapperConfiguration;
import org.example.dto.shoppingcart.CreateShoppingCartDto;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfiguration.class)
public interface ShoppingCartMapper {

    ShoppingCart toEntity(CreateShoppingCartDto createShoppingCartDto);

    @Mapping(source = "user.id", target = "userId")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

}
