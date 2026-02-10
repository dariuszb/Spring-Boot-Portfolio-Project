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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping Cart management",
        description = "Endpoints for Shopping Cart management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Get user's shopping cart", description = "Get user's shopping cart")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        String userName = authentication.getName();
        return shoppingCartService.get(userName);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add item to shopping cart", description = "Add item to shopping cart")
    public ShoppingCartDto addCartItemToShoppingCart(
            Authentication authentication, @RequestBody @Valid CreateCartItemDto createItemDto) {
        String username = authentication.getName();
        return shoppingCartService.addBookToShoppingCart(username, createItemDto);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update cart item's properties",
            description = "Update cart item's properties")
    public ShoppingCartDto updateCartItemById(
            Authentication authentication, @PathVariable Long cartItemId,
            @RequestBody @Valid UpdateCartItemDto updateCartItemDto) {
        String username = authentication.getName();
        return shoppingCartService.update(username, cartItemId, updateCartItemDto);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete book from the cart",
            description = "Delete book from the cart")
    @DeleteMapping("/cart-items/{cartItemId}")
    public void deleteCartItemById(@PathVariable Long cartItemId) {
        shoppingCartService.deleteItemById(cartItemId);
    }

}
