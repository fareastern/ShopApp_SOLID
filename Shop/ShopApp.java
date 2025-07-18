package Shop;

import Shop.orders.*;
import Shop.products.*;
import Shop.products.filters.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ShopApp {
    private final Scanner scanner;
    private final ShopSystem shopSystem;

    public ShopApp() {
        this.scanner = new Scanner(System.in);
        this.shopSystem = new ShopSystem();
    }

    public void run() {
        while (true) {
            if (shopSystem.getCurrentUser() == null) {
                showMainMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("3. Просмотреть товары");
        System.out.println("4. Выход");
        System.out.print("Выберите вариант: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> auth("login");
            case "2" -> auth("register");
            case "3" -> showProducts();
            case "4" -> System.exit(0);
            default -> System.out.println("Неизвестная команда.");
        }
    }

    private void auth(String action) {
        System.out.print("Имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        boolean success;
        String successMsg;
        String errorMsg;

        if ("login".equals(action)) {
            success = shopSystem.login(username, password);
            successMsg = "Вход выполнен успешно!";
            errorMsg = "Неверное имя пользователя или пароль.";
        } else {
            success = shopSystem.registerUser(username, password);
            successMsg = "Регистрация прошла успешно! Теперь вы можете войти.";
            errorMsg = "Имя пользователя уже занято.";
        }

        System.out.println(success ? successMsg : errorMsg);
    }

    private void showUserMenu() {
        User user = shopSystem.getCurrentUser();
        System.out.println("\n=== Личный кабинет (" + user.getUsername() + ") ===");
        System.out.println("1. Просмотреть товары");
        System.out.println("2. Поиск товаров");
        System.out.println("3. Корзина");
        System.out.println("4. Мои заказы");
        System.out.println("5. Рекомендации");
        System.out.println("6. Настройки аккаунта");
        System.out.println("7. Администрирование");
        System.out.println("8. Выйти");
        System.out.print("Выберите вариант: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> showProducts();
            case "2" -> searchProducts();
            case "3" -> showCart();
            case "4" -> showOrders();
            case "5" -> showRecommendations();
            case "6" -> accountSettings();
            case "7" -> adminMenu();
            case "8" -> shopSystem.logout();
            default -> System.out.println("Неизвестная команда.");
        }
    }

    private void showProducts() {
        List<Product> products = shopSystem.getProductCatalog().getAllProducts();
        displayProducts(products);
        showMenuProduct();
    }

    private void searchProducts() {
        System.out.println("\n=== Поиск товаров ===");
        System.out.println("1. По ключевому слову");
        System.out.println("2. По цене");
        System.out.println("3. По производителю");
        System.out.print("Выберите вариант поиска: ");

        String choice = scanner.nextLine();

        List<Product> results = null;
        ProductCatalog catalog = shopSystem.getProductCatalog();

        switch (choice) {
            case "1" -> {
                System.out.print("Введите ключевое слово: ");
                String keyword = scanner.nextLine();
                results = catalog.filterProducts(new KeywordFilter(keyword));
            }
            case "2" -> {
                System.out.print("Минимальная цена: ");
                double minPrice = scanner.nextDouble();
                System.out.print("Максимальная цена: ");
                double maxPrice = scanner.nextDouble();
                scanner.nextLine();
                results = catalog.filterProducts(new PriceRangeFilter(minPrice, maxPrice));
            }
            case "3" -> {
                System.out.print("Введите производителя: ");
                String manufacturer = scanner.nextLine();
                results = catalog.filterProducts(new ManufacturerFilter(manufacturer));
            }
            default -> System.out.println("Неизвестная команда.");
        }

        if (results != null) {
            displayProducts(results);
            showMenuProduct();
        }
    }

    private void displayProducts(List<Product> products) {
        System.out.println("\n=== Список товаров ===");
        if (products.isEmpty()) {
            System.out.println("Товары не найдены.");
            return;
        }

        System.out.printf("%-5s %-20s %-15s %-10s %-5s%n", "ID", "Название", "Производитель", "Цена", "Моя оценка");
        for (Product product : products) {
            System.out.printf("%-5s %-20s %-15s %-10.2f %-5s%n",
                    product.getId(),
                    product.getName(),
                    product.getManufacturer(),
                    product.getPrice(),
                    product.getRating());
        }
    }

    private void showMenuProduct() {
        if (shopSystem.getCurrentUser() != null) {
            System.out.print("\nВыберите действие:\n" +
                    "1. Добавить товар в корзину\n" +
                    "2. Оценить товар\n" +
                    "0. Назад\n" +
                    "Ваш выбор: ");

            String action = scanner.nextLine();

            switch (action) {
                case "1" -> productAction("addToCart");
                case "2" -> productAction("rateProduct");
                case "0" -> {
                    return;
                }
                default -> System.out.println("Неизвестная команда.");
            }
        }
    }

    // Добавление в корзину и оценка товара
    private void productAction(String action) {
        System.out.print("Введите ID товара: ");
        String productId = scanner.nextLine();
        Product product = shopSystem.getProductCatalog().getProductById(productId);
        if (product == null) {
            System.out.println("Товар не найден");
            return;
        }

        if ("addToCart".equals(action)) {
            Integer quantity = getValidNumber("Введите количество: ", false);
            shopSystem.getCurrentUser().getShoppingCart().addProduct(product, quantity);
            System.out.println("Товар добавлен в корзину.");
        } else if ("rateProduct".equals(action)) {
            Double rating = getValidNumber("Введите вашу оценку (1-5): ", true);
            if (rating >= 1 && rating <= 5) {
                product.updateRating(rating);
                shopSystem.getCurrentUser().addRatedProduct(product);
                System.out.println("Спасибо за вашу оценку!");
            } else {
                System.out.println("Оценка должна быть от 1 до 5.");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Number> T getValidNumber(String prompt, boolean isDouble) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return isDouble ? (T) Double.valueOf(input) : (T) Integer.valueOf(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число");
            }
        }
    }

    private void showCart() {
        ShoppingCart cart = shopSystem.getCurrentUser().getShoppingCart();
        Map<Product, Integer> items = cart.getItems();

        System.out.println("\n=== Корзина ===");
        if (items.isEmpty()) {
            System.out.println("Корзина пуста.");
            return;
        }

        System.out.printf("%-5s %-20s %-10s %-8s %-10s%n", "ID", "Название", "Цена", "Кол-во", "Сумма");
        items.forEach((product, quantity) -> {
            double total = product.getPrice() * quantity;
            System.out.printf("%-5s %-20s %-10.2f %-8d %-10.2f%n",
                    product.getId(), product.getName(), product.getPrice(), quantity, total);
        });

        System.out.printf("Итого: %.2f%n", cart.getTotalPrice());

        System.out.println("\n1. Оформить заказ");
        System.out.println("2. Удалить товар");
        System.out.println("3. Назад");
        System.out.print("Выберите вариант: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> {
                shopSystem.placeOrder();
                System.out.println("Заказ оформлен успешно!");
            }
            case "2" -> {
                System.out.print("Введите ID товара для удаления: ");
                String productId = scanner.nextLine();
                Product product = shopSystem.getProductCatalog().getProductById(productId);
                if (product != null && items.containsKey(product)) {
                    System.out.print("Количество для удаления: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    cart.removeProduct(product, quantity);
                    System.out.println("Товар удален из корзины.");
                } else {
                    System.out.println("Товар не найден в корзине.");
                }
            }
            default -> System.out.println("Неизвестная команда.");
        }
    }

    private void showOrders() {
        List<Order> orders = shopSystem.getCurrentUserOrders();
        System.out.println("\n=== История заказов ===");
        if (orders.isEmpty()) {
            System.out.println("У вас нет заказов.");
            return;
        }

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            System.out.printf("%d. Заказ #%s от %s - Статус: %s - Сумма: %.2f%n",
                    i + 1,
                    order.getOrderId(),
                    order.getOrderDate(),
                    order.getStatus(),
                    order.getTotalPrice());
        }

        System.out.print("\nВыберите заказ для деталей (0 для возврата): ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine();

        if (choice >= 0 && choice < orders.size()) {
            Order selectedOrder = orders.get(choice);
            showOrderDetails(selectedOrder);
        }
    }

    private void showOrderDetails(Order order) {
        System.out.printf("\nДетали заказа #%s%n", order.getOrderId());
        System.out.printf("Дата: %s%n", order.getOrderDate());
        System.out.printf("Статус: %s%n", order.getStatus());
        System.out.println("Товары:");

        order.getItems().forEach((product, quantity) ->
                System.out.printf(" ID:%s  %s x%d - %.2f%n", product.getId(), product.getName(), quantity, product.getPrice()));

        System.out.printf("Итого: %.2f%n", order.getTotalPrice());

        if (order.getStatus() == OrderStatus.DELIVERED) {
            System.out.print("\nХотите вернуть заказ? (да/нет): ");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("да")) {
                order.returnOrder();
            }
        }
    }

    private void showRecommendations() {
        User user = shopSystem.getCurrentUser();
        List<Product> recommendations = shopSystem.getProductCatalog()
                .getRecommendedProducts(user);

        System.out.println("\n=== Рекомендации для вас ===");

        if (recommendations.isEmpty()) {
            System.out.println("Пока нет рекомендаций. Оцените несколько товаров, чтобы получить персонализированные рекомендации.");
        } else {
            displayProducts(recommendations);
            System.out.print("\n0. Назад\nВаш выбор: ");
            scanner.nextLine();
        }
    }

    private void adminMenu() {
        if (!shopSystem.getCurrentUser().getUsername().equals("admin")) {
            System.out.println("Доступ запрещен. Войдите с правами администратора (логин: admin, пароль: admin)");
            return;
        }

        while (true) {
            System.out.println("\n=== Администрирование ===");
            System.out.println("1. Изменить статус заказа");
            System.out.println("2. Просмотреть всех пользователей");
            System.out.println("3. Вернуться в меню");
            System.out.print("Выберите вариант: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> changeOrderStatus();
                case "2" -> showAllUsers();
                case "3" -> {
                    return;
                }
                default -> System.out.println("Неизвестная команда.");
            }
        }
    }

    private void showAllUsers() {
        List<User> users = shopSystem.getAllUsers();
        System.out.println("\n=== Список пользователей ===");
        users.forEach(user ->
                System.out.printf("ID: %s, Логин: %s, Заказов: %d%n",
                        user.getUserId(),
                        user.getUsername(),
                        user.getOrderHistory().size()));
    }

    private void changeOrderStatus() {
        List<Order> allOrders = shopSystem.getAllOrders();

        System.out.println("\n=== Все заказы ===");
        for (int i = 0; i < allOrders.size(); i++) {
            Order order = allOrders.get(i);
            System.out.printf("%d. Заказ #%s - Статус: %s%n",
                    i + 1, order.getOrderId(), order.getStatus());
        }

        System.out.print("Выберите номер заказа: ");
        int orderIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        if (orderIndex < 0 || orderIndex >= allOrders.size()) {
            System.out.println("Неверный выбор заказа.");
            return;
        }

        Order selectedOrder = allOrders.get(orderIndex);

        System.out.println("Доступные статусы:");
        Arrays.stream(OrderStatus.values()).forEach(System.out::println);

        System.out.print("Введите новый статус: ");
        String newStatusStr = scanner.nextLine().toUpperCase();

        try {
            OrderStatus newStatus = OrderStatus.valueOf(newStatusStr);
            selectedOrder.updateStatus(newStatus);
            System.out.println("Статус заказа успешно изменен.");
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный статус заказа.");
        }
    }

    private void accountSettings() {
        User user = shopSystem.getCurrentUser();
        System.out.println("\n=== Настройки аккаунта ===");
        System.out.println("1. Изменить имя пользователя");
        System.out.println("2. Изменить пароль");
        System.out.println("3. Назад");
        System.out.print("Выберите вариант: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> {
                System.out.print("Новое имя пользователя: ");
                String newUsername = scanner.nextLine();
                user.setUsername(newUsername);
                System.out.println("Имя пользователя изменено.");
            }
            case "2" -> {
                System.out.print("Текущий пароль: ");
                String currentPassword = scanner.nextLine();
                if (user.authenticate(currentPassword)) {
                    System.out.print("Новый пароль: ");
                    String newPassword = scanner.nextLine();
                    user.changePassword(newPassword);
                    System.out.println("Пароль изменен.");
                } else {
                    System.out.println("Неверный пароль.");
                }
            }
            default -> System.out.println("Неизвестная команда.");
        }
    }
}
