package Shop;

import Shop.orders.Order;
import Shop.products.Product;
import Shop.products.ProductCatalog;

import java.util.*;
import java.util.stream.Collectors;

public class ShopSystem {
    private final ProductCatalog productCatalog;
    private final Map<String, User> users;
    private User currentUser;

    public ShopSystem() {
        this.productCatalog = new ProductCatalog();
        this.users = new HashMap<>();
        initializeShop();
    }

    private void initializeShop() {
        // Добавляем каталог
        productCatalog.addProduct(new Product("1", "Смартфон", 49000, "Tech", Arrays.asList("электроника", "телефоны")));
        productCatalog.addProduct(new Product("2", "Ноутбук", 139000, "Tech", Arrays.asList("электроника", "компьютеры")));
        productCatalog.addProduct(new Product("3", "Наушники", 19999, "Audio", Arrays.asList("электроника", "аудио")));
        productCatalog.addProduct(new Product("4", "Книга", 679, "Book", Arrays.asList("книги", "литература")));
        productCatalog.addProduct(new Product("5", "Мышь", 5899, "Tech", Arrays.asList("электроника", "компьютеры", "аксессуары")));

        // Создаем администратора
        User admin = new User("admin", "admin", "admin");
        users.put(admin.getUserId(), admin);
    }

    public boolean registerUser(String username, String password) {
        if (users.values().stream().anyMatch(u -> u.getUsername().equals(username))) {
            return false;
        }
        String userId = "user" + (users.size() + 1);
        User newUser = new User(userId, username, password);
        users.put(userId, newUser);
        return true;
    }

    public boolean login(String username, String password) {
        // Используем Optional, так как пользователя может не быть
        Optional<User> userOpt = users.values().stream()
                .filter(u -> u.getUsername().equals(username) && u.authenticate(password))
                .findFirst();

        userOpt.ifPresent(user -> this.currentUser = user);
        return userOpt.isPresent();
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public ProductCatalog getProductCatalog() {
        return productCatalog;
    }

    public void placeOrder() {
        ShoppingCart cart = currentUser.getShoppingCart();
        if (cart.getItems().isEmpty()) return;

        String orderId = "order" + System.currentTimeMillis();
        Order order = new Order(orderId, cart.getItems());
        currentUser.addOrderToHistory(order);
        cart.clear();
    }

    public List<Order> getCurrentUserOrders() {
        return currentUser.getOrderHistory();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<Order> getAllOrders() {
        return users.values().stream()
                .flatMap(user -> user.getOrderHistory().stream())
                .collect(Collectors.toList());
    }
}