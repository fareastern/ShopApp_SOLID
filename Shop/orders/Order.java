package Shop.orders;

import Shop.products.Product;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private final String orderId;
    private final Map<Product, Integer> items;
    private final Date orderDate;
    private OrderStatus status;

    public Order(String orderId, Map<Product, Integer> items) {
        this.orderId = orderId;
        this.items = new HashMap<>(items);
        this.orderDate = new Date();
        this.status = OrderStatus.NEW;
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public double getTotalPrice() {
        return items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    public void returnOrder() {
        this.status = OrderStatus.RETURNED;
        System.out.println("Заказ успешно возвращен");
    }

    // Геттеры
    public String getOrderId() {
        return orderId;
    }

    public Map<Product, Integer> getItems() {
        return new HashMap<>(items);
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }
}