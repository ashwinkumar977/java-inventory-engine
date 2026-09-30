package com.rabtech.inventory.exception;

public class ProductNotFoundException extends InventoryException {
    public ProductNotFoundException(String productId) {
        super("Product with ID '" + productId + "' was not found in inventory.");
    }
}