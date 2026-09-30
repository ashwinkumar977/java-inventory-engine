package com.rabtech.inventory;

import com.rabtech.inventory.exception.*;
import com.rabtech.inventory.model.*;
import com.rabtech.inventory.service.InventoryManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryManagerTest {

    private InventoryManager manager;

    @BeforeEach
    void setUp() {
        manager = new InventoryManager();

        PhysicalProduct laptop = new PhysicalProduct(
                "P101", "Gaming Laptop", new BigDecimal("1000.00"),
                Category.ELECTRONICS, 10, 2.5, new BigDecimal("10.00") // Final Price: 1025.00
        );

        DigitalProduct ebook = new DigitalProduct(
                "D201", "Java Programming Guide", new BigDecimal("50.00"),
                Category.BOOKS, 100, 20.0 // Final Price: 40.00
        );

        manager.addProduct(laptop);
        manager.addProduct(ebook);
    }

    @Test
    @DisplayName("Polymorphism Test: Calculates correct price for physical vs digital products")
    void testPolymorphicPriceCalculation() {
        Product laptop = manager.findById("P101");
        Product ebook = manager.findById("D201");

        //assertEquals(new BigDecimal("1025.00"), laptop.calculateFinalPrice());
        assertEquals(0, new BigDecimal("1025.00").compareTo(laptop.calculateFinalPrice()));
        assertEquals(0, new BigDecimal("40.00").compareTo(ebook.calculateFinalPrice()));
    }

    @Test
    @DisplayName("Streams & Sorting: Sort products by final price ascending")
    void testSortingByPrice() {
        List<Product> sorted = manager.getProductsSortedByPrice(true);

        assertEquals("D201", sorted.get(0).getId()); // 40.00
        assertEquals("P101", sorted.get(1).getId()); // 1025.00
    }

    @Test
    @DisplayName("Streams & Filtering: Filter products by category")
    void testFilterByCategory() {
        List<Product> books = manager.filterByCategory(Category.BOOKS);

        assertEquals(1, books.size());
        assertEquals("Java Programming Guide", books.get(0).getName());
    }

    @Test
    @DisplayName("Exception Handling: ProductNotFoundException thrown for invalid ID")
    void testProductNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> manager.findById("INVALID_ID"));
    }

    @Test
    @DisplayName("Exception Handling: InsufficientStockException thrown when order exceeds stock")
    void testInsufficientStockException() {
        assertThrows(InsufficientStockException.class, () -> manager.fulfillOrder("P101", 50));
    }

    @Test
    @DisplayName("Stock Reduction: Stock reduces correctly upon fulfillment")
    void testStockReduction() {
        manager.fulfillOrder("P101", 3);
        assertEquals(7, manager.findById("P101").getStockQuantity());
    }

    @Test
    @DisplayName("Record DTO Mapping: Summaries generated accurately")
    void testProductSummaries() {
        List<ProductSummary> summaries = manager.getProductSummaries();
        assertEquals(2, summaries.size());
    }
}