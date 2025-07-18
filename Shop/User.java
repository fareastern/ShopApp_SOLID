package Shop;

import Shop.orders.Order;
import Shop.products.Product;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String userId;
    private String username;
    private String password;
    private final List<Order> orderHistory;
    private final List<Product> ratedProducts;
    private final ShoppingCart shoppingCart;

    public User(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.orderHistory = new ArrayList<>();
        this.ratedProducts = new ArrayList<>();
        this.shoppingCart = new ShoppingCart();
    }

    public void addOrderToHistory(Order order) {
        orderHistory.add(order);
    }

    public void addRatedProduct(Product product) {
        if (!ratedProducts.contains(product)) {
            ratedProducts.add(product);
        }
    }

    // Аутентификация - более безопасный вариант, чем доставать пароль через геттер
    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    // Геттеры и сеттеры
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public List<Order> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }

    public List<Product> getRatedProducts() {
        return new ArrayList<>(ratedProducts);
    }
}