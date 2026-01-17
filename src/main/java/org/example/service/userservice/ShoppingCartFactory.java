package org.example.service.userservice;

import org.example.model.ShoppingCart;
import org.example.model.User;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCartFactory {
    public ShoppingCart createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        return shoppingCart;
    }
}
