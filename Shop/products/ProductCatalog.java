package Shop.products;

import Shop.User;
import Shop.products.filters.ProductFilter;

import java.util.*;

public class ProductCatalog {
    private final List<Product> products;
    // Для быстрого поиска по ID
    private final Map<String, Product> productIdCache;

    public ProductCatalog() {
        this.products = new ArrayList<>();
        this.productIdCache = new HashMap<>();
    }

    public void addProduct(Product product) {
        products.add(product);
        productIdCache.put(product.getId(), product);
    }

    // Принцип Лисков и Принцип инверсии зависимостей
    // Метод работает с базовым ProductFilter, но может принимать любые его подтипы
    // Зависим от абстракции ProductFilter, а не от конкретной реализации
    public List<Product> filterProducts(ProductFilter filter) {
        return products.stream()
                .filter(filter::matches)
                .toList();
    }

    // Возвращаем неизменяемую копию
    public List<Product> getAllProducts() {
        return List.copyOf(products);
    }

    // Поиск по ID через кэш
    public Product getProductById(String productId) {
        return productIdCache.get(productId);
    }

    // Отдаем список рекомендованных (оцененных) товаров (отсортированы по оценке)
    public List<Product> getRecommendedProducts(User user) {
        return user.getRatedProducts().stream()
                .sorted(Comparator.comparingDouble(Product::getRating).reversed())
                .toList();
    }
}