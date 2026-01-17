package org.example.repository.shoppingcart;

import java.util.Optional;
import org.example.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository
        extends JpaRepository<ShoppingCart,Long> {

    Optional<ShoppingCart> findByUserEmail(String userName);

}
