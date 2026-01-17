package org.example.service.shoppingcart;

import org.example.dto.cartitem.CreateCartItemDto;
import org.example.dto.cartitem.UpdateCartItemDto;
import org.example.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {

    ShoppingCartDto get();

    ShoppingCartDto addBookToShoppingCart(CreateCartItemDto createCartItemDto);

    ShoppingCartDto update(Long id, UpdateCartItemDto updateCartItemDto);

    void deleteItemById(Long cartItemId);

}
