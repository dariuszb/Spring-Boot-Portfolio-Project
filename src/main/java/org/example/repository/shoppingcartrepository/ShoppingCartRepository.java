package org.example.repository.shoppingcartrepository;

import java.util.Optional;
import org.example.model.ShoppingCart;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository
        extends JpaRepository<ShoppingCart,Long> {

    ShoppingCart findByUserId(Long userId);

    ShoppingCart findByUser(Optional<User> user);

}
