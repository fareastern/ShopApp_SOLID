package Shop.products.filters;

import Shop.products.Product;

public interface ProductFilter {
    boolean matches(Product product);
}