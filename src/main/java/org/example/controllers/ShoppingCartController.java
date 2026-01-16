package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.cartitem.CreateCartItemDto;
import org.example.dto.cartitem.UpdateCartItemDto;
import org.example.dto.shoppingcart.ShoppingCartDto;
import org.example.service.shoppingcart.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ShoppingCartRepository management",
        description = "Endpoints for ShoppingCartRepository management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Get user's shoppingcart", description = "Get user's shoppingcart")
    public ShoppingCartDto getShoppingCart() {
        return shoppingCartService.get();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add item to shoppingcart", description = "Add item to shoppingcart")
    public ShoppingCartDto addCartItemToShoppingCart(
            @RequestBody @Valid CreateCartItemDto createItemDto) {
        return shoppingCartService.addBookToShoppingCart(createItemDto);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update cart item's properties",
            description = "Update cart item's properties")
    public ShoppingCartDto updateCartItemById(@PathVariable Long cartItemId,
                                              @RequestBody @Valid UpdateCartItemDto
                                                      updateCartItemDto) {
        return shoppingCartService.update(cartItemId, updateCartItemDto);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Delete book from the cart",
            description = "Delete book from the cart")
    @DeleteMapping("/cart-items/{cartItemId}")
    public ShoppingCartDto deleteCartItemById(@PathVariable Long cartItemId) {
        return shoppingCartService.deleteItemById(cartItemId);
    }

}
