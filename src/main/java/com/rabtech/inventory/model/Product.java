package com.rabtech.inventory.model;

import com.rabtech.inventory.exception.InvalidProductDataException;
import java.math.BigDecimal;
import java.util.Objects;

public abstract class Product {
    private final String id;
    private String name;
    private BigDecimal basePrice;
    private Category category;
    private int stockQuantity;

    public Product(String id, String name, BigDecimal basePrice, Category category, int stockQuantity) {
        if (id == null || id.isBlank()) throw new InvalidProductDataException("Product ID cannot be null or empty.");
        if (name == null || name.isBlank()) throw new InvalidProductDataException("Product name cannot be null or empty.");
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) < 0) throw new InvalidProductDataException("Base price cannot be negative.");
        if (category == null) throw new InvalidProductDataException("Category cannot be null.");
        if (stockQuantity < 0) throw new InvalidProductDataException("Stock quantity cannot be negative.");

        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.category = category;
        this.stockQuantity = stockQuantity;
    }

    public abstract BigDecimal calculateFinalPrice();

    public void reduceStock(int quantity) {
        if (quantity <= 0) throw new InvalidProductDataException("Quantity to reduce must be positive.");
        if (this.stockQuantity < quantity) {
            throw new IllegalArgumentException("Stock reduction exceeds available inventory.");
        }
        this.stockQuantity -= quantity;
    }

    public void addStock(int quantity) {
        if (quantity <= 0) throw new InvalidProductDataException("Quantity to add must be positive.");
        this.stockQuantity += quantity;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getBasePrice() { return basePrice; }
    public Category getCategory() { return category; }
    public int getStockQuantity() { return stockQuantity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}