package com.rabtech.inventory.model;

import java.math.BigDecimal;

public class DigitalProduct extends Product {
    private final double discountPercentage;

    public DigitalProduct(String id, String name, BigDecimal basePrice, Category category, int stockQuantity, double discountPercentage) {
        super(id, name, basePrice, category, stockQuantity);
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");
        }
        this.discountPercentage = discountPercentage;
    }

    @Override
    public BigDecimal calculateFinalPrice() {
        BigDecimal discountMultiplier = BigDecimal.valueOf(1 - (discountPercentage / 100.0));
        return getBasePrice().multiply(discountMultiplier);
    }

    public double getDiscountPercentage() { return discountPercentage; }
}