package org.example.repository.cartitem;

import java.util.Optional;
import org.example.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findById(Long id);
}
