package com.rabtech.inventory.exception;

public class InsufficientStockException extends InventoryException {
    public InsufficientStockException(String productId, int requested, int available) {
        super(String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d", productId, requested, available));
    }
}