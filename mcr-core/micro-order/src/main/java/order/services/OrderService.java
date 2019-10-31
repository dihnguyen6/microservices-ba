package order.services;

import order.models.Order;
import org.bson.types.ObjectId;

import java.util.List;

public interface OrderService {
    Order findOrderById(ObjectId orderId);
    List<Order> findAllOrders();
    List<Order> findOrdersByCustomerId(ObjectId customerId);
    void save(Order order);
    void delete(ObjectId orderId);
}
