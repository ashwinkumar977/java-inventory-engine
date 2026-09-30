package com.rabtech.inventory.model;

import java.math.BigDecimal;

public class PhysicalProduct extends Product {
    private final double weightKg;
    private final BigDecimal shippingRatePerKg;

    public PhysicalProduct(String id, String name, BigDecimal basePrice, Category category, int stockQuantity, double weightKg, BigDecimal shippingRatePerKg) {
        super(id, name, basePrice, category, stockQuantity);
        if (weightKg <= 0) throw new IllegalArgumentException("Weight must be positive.");
        if (shippingRatePerKg == null || shippingRatePerKg.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Shipping rate cannot be negative.");
        }
        this.weightKg = weightKg;
        this.shippingRatePerKg = shippingRatePerKg;
    }

    @Override
    public BigDecimal calculateFinalPrice() {
        BigDecimal shippingCost = shippingRatePerKg.multiply(BigDecimal.valueOf(weightKg));
        return getBasePrice().add(shippingCost);
    }

    public double getWeightKg() { return weightKg; }
}