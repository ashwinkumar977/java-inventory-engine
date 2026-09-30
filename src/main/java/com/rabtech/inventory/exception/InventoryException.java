package com.rabtech.inventory.exception;

public abstract class InventoryException extends RuntimeException {
    public InventoryException(String message) {
        super(message);
    }
}