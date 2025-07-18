package Shop.products;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private final String id;
    private final String name;
    private final double price;
    private final String manufacturer;
    private Double rating;
    private final List<String> categories;

    // Избегание магических чисел - вводим константы
    private static final double MIN_RATING = 0.0;
    private static final double MAX_RATING = 5.0;

    public Product(String id, String name, double price, String manufacturer, List<String> categories) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.manufacturer = manufacturer;
        this.categories = categories;
        this.rating = null; // Рейтинг по умолчанию
    }

    // Принцип открытости/закрытости
    // Класс открыт для расширения (можно наследовать и изменять логику расчета рейтинга), но закрыт для модификации
    public void updateRating(Double newRating) {
        if (newRating < MIN_RATING || newRating > MAX_RATING) {
            throw new IllegalArgumentException("Рейтинг должен быть от " + MIN_RATING + " до " + MAX_RATING);
        }
        this.rating = newRating; // Конечно, тут можно сложить текущую оценку с новой оценкой и поделить на количество оценок в боевом варианте
    }

    // Геттеры
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Double getRating() {
        return rating;
    }

    public List<String> getCategories() {
        return new ArrayList<>(categories);
    }
}