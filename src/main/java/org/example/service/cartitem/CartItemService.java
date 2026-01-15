package org.example.service.cartitem;

import org.example.dto.cartitem.CartItemDto;
import org.example.dto.cartitem.CreateCartItemDto;

public interface CartItemService {

    CartItemDto addBookToShoppingCart(CreateCartItemDto createCartItemDto);

    CartItemDto update(Long id, CreateCartItemDto createCartItemDto);

    void deleteItemById(Long cartItemId);
}
