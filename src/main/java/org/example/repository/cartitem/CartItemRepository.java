package org.example.repository.cartitem;

import java.util.Optional;
import org.example.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query(value = "SELECT ci FROM CartItem ci JOIN FETCH ci.book "
            + "WHERE ci.book.id = :createItemDtoBookId"
            + " AND ci.shoppingCart.id = :shoppingCartId")
    Optional<CartItem> findItemByProperBookIdAndShoppingCartId(Long createItemDtoBookId,
                                             Long shoppingCartId);

}
