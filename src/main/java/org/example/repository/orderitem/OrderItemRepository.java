package org.example.repository.orderitem;

import org.example.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("FROM OrderItem oi WHERE oi.id =:itemId AND oi.order.id =:orderId")
    OrderItem getItemFromOrder(Long orderId, Long itemId);
}
