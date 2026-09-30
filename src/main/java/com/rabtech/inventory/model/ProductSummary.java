package com.rabtech.inventory.model;

import java.math.BigDecimal;

public record ProductSummary(
        String id,
        String name,
        Category category,
        BigDecimal finalPrice,
        int stockQuantity
) {}