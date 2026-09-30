package com.rabtech.inventory.service;

import com.rabtech.inventory.exception.*;
import com.rabtech.inventory.model.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryManager {
    private final Map<String, Product> productMap = new HashMap<>();
    private final Set<Category> availableCategories = new HashSet<>();

    public void addProduct(Product product) {
        if (product == null) {
            throw new InvalidProductDataException("Product cannot be null.");
        }
        if (productMap.containsKey(product.getId())) {
            throw new InvalidProductDataException("Product with ID '" + product.getId() + "' already exists.");
        }
        productMap.put(product.getId(), product);
        availableCategories.add(product.getCategory());
    }

    public Product findById(String id) {
        Product product = productMap.get(id);
        if (product == null) {
            throw new ProductNotFoundException(id);
        }
        return product;
    }

    public void fulfillOrder(String productId, int quantity) {
        Product product = findById(productId);
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStockQuantity());
        }
        product.reduceStock(quantity);
    }

    public List<Product> filterByCategory(Category category) {
        return productMap.values().stream()
                .filter(p -> p.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Product> getProductsSortedByPrice(boolean ascending) {
        Comparator<Product> priceComparator = Comparator.comparing(Product::calculateFinalPrice);
        if (!ascending) {
            priceComparator = priceComparator.reversed();
        }
        return productMap.values().stream()
                .sorted(priceComparator)
                .collect(Collectors.toList());
    }

    public List<ProductSummary> getProductSummaries() {
        return productMap.values().stream()
                .map(p -> new ProductSummary(
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        p.calculateFinalPrice(),
                        p.getStockQuantity()
                ))
                .collect(Collectors.toList());
    }

    public Set<Category> getAvailableCategories() {
        return Collections.unmodifiableSet(availableCategories);
    }

    public int getTotalItemCount() {
        return productMap.size();
    }
}