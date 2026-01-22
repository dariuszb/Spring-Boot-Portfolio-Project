package org.example.repository.order;

import java.util.List;
import java.util.Set;
import org.example.model.Order;
import org.example.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findAllOrdersByUserEmail(String email); //findAllOrdersByUserId

    @Query("SELECT o.orderItems FROM Order o WHERE o.id =:orderId")
    Set<OrderItem> getAllItemsForOrder(Long orderId);

    @Query("SELECT o.user.email FROM Order o WHERE o.id =:orderId")
    String getUserEmailByOrderId(Long orderId);

}
