package com.rabtech.inventory;

import com.rabtech.inventory.exception.InventoryException;
import com.rabtech.inventory.io.InventoryFileService;
import com.rabtech.inventory.model.*;
import com.rabtech.inventory.service.InventoryManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final InventoryManager manager = new InventoryManager();
    private static final InventoryFileService fileService = new InventoryFileService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        System.out.println("=================================================");
        System.out.println("    INVENTORY MANAGEMENT SYSTEM (CLI)");
        System.out.println("=================================================");

        while (running) {
            printMenu();
            System.out.print("Enter choice (0-9): ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> addPhysicalProductUI();
                    case "2" -> addDigitalProductUI();
                    case "3" -> viewAllProductsUI();
                    case "4" -> findProductByIdUI();
                    case "5" -> fulfillOrderUI();
                    case "6" -> filterByCategoryUI();
                    case "7" -> sortProductsByPriceUI();
                    case "8" -> importFromCsvUI();
                    case "9" -> exportToCsvUI();
                    case "0" -> {
                        running = false;
                        System.out.println("\nExiting application. Goodbye!");
                    }
                    default -> System.out.println("\n[!] Invalid selection. Please enter a number between 0 and 9.");
                }
            } catch (InventoryException e) {
                System.out.println("\n[DOMAIN ERROR] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\n[SYSTEM ERROR] " + e.getMessage());
            }
            System.out.println("-------------------------------------------------");
        }
    }

    private static void printMenu() {
        System.out.println("\nSELECT AN OPERATION:");
        System.out.println("1. Add New Physical Product");
        System.out.println("2. Add New Digital Product");
        System.out.println("3. View All Products Summary");
        System.out.println("4. Find Product by ID");
        System.out.println("5. Fulfill Order (Reduce Stock)");
        System.out.println("6. Filter Products by Category");
        System.out.println("7. Sort Products by Final Price");
        System.out.println("8. Import Products from CSV File");
        System.out.println("9. Export Summary Report to CSV File");
        System.out.println("0. Exit");
    }

    private static void addPhysicalProductUI() {
        System.out.println("\n--- Add Physical Product ---");
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine().trim();

        Category category = selectCategoryUI();

        System.out.print("Enter Base Price: ");
        BigDecimal basePrice = new BigDecimal(scanner.nextLine().trim());

        System.out.print("Enter Initial Stock Quantity: ");
        int stock = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Weight (in kg): ");
        double weight = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Enter Shipping Rate per Kg: ");
        BigDecimal shippingRate = new BigDecimal(scanner.nextLine().trim());

        PhysicalProduct product = new PhysicalProduct(id, name, basePrice, category, stock, weight, shippingRate);
        manager.addProduct(product);
        System.out.println("[✓] Physical Product added successfully!");
    }

    private static void addDigitalProductUI() {
        System.out.println("\n--- Add Digital Product ---");
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine().trim();

        Category category = selectCategoryUI();

        System.out.print("Enter Base Price: ");
        BigDecimal basePrice = new BigDecimal(scanner.nextLine().trim());

        System.out.print("Enter Initial Stock Quantity: ");
        int stock = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Discount Percentage (0-100): ");
        double discount = Double.parseDouble(scanner.nextLine().trim());

        DigitalProduct product = new DigitalProduct(id, name, basePrice, category, stock, discount);
        manager.addProduct(product);
        System.out.println("[✓] Digital Product added successfully!");
    }

    private static void viewAllProductsUI() {
        System.out.println("\n--- All Product Summaries ---");
        List<ProductSummary> summaries = manager.getProductSummaries();
        if (summaries.isEmpty()) {
            System.out.println("No products found in inventory.");
            return;
        }
        summaries.forEach(s -> System.out.printf("ID: %s | Name: %s | Category: %s | Final Price: %s | Stock: %d%n",
                s.id(), s.name(), s.category(), s.finalPrice().toPlainString(), s.stockQuantity()));
    }

    private static void findProductByIdUI() {
        System.out.print("\nEnter Product ID to search: ");
        String id = scanner.nextLine().trim();
        Product product = manager.findById(id);
        System.out.printf("[✓] Found: %s (%s) - Category: %s | Base Price: %s | Final Price: %s | Stock: %d%n",
                product.getName(), product.getId(), product.getCategory(),
                product.getBasePrice(), product.calculateFinalPrice(), product.getStockQuantity());
    }

    private static void fulfillOrderUI() {
        System.out.print("\nEnter Product ID to order: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter Quantity to Fulfill: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());

        manager.fulfillOrder(id, qty);
        System.out.println("[✓] Order fulfilled successfully! Updated Stock: " + manager.findById(id).getStockQuantity());
    }

    private static void filterByCategoryUI() {
        Category category = selectCategoryUI();
        List<Product> filtered = manager.filterByCategory(category);
        System.out.println("\n--- Products in Category: " + category + " ---");
        if (filtered.isEmpty()) {
            System.out.println("No products found in this category.");
            return;
        }
        filtered.forEach(p -> System.out.printf("%s (%s) - Final Price: %s | Stock: %d%n",
                p.getName(), p.getId(), p.calculateFinalPrice(), p.getStockQuantity()));
    }

    private static void sortProductsByPriceUI() {
        System.out.print("\nSort Ascending (Low to High)? (y/n): ");
        boolean asc = scanner.nextLine().trim().equalsIgnoreCase("y");

        List<Product> sorted = manager.getProductsSortedByPrice(asc);
        System.out.println("\n--- Sorted Product List ---");
        sorted.forEach(p -> System.out.printf("%s (%s) - Final Price: %s | Category: %s%n",
                p.getName(), p.getId(), p.calculateFinalPrice(), p.getCategory()));
    }

    private static void importFromCsvUI() throws IOException {
        System.out.print("\nEnter CSV File Path to Import (e.g., inventory_input.csv): ");
        String pathStr = scanner.nextLine().trim();
        fileService.importFromCsv(Path.of(pathStr), manager);
        System.out.println("[✓] Successfully imported products. Total products in inventory: " + manager.getTotalItemCount());
    }

    private static void exportToCsvUI() throws IOException {
        System.out.print("\nEnter Output CSV File Path (e.g., inventory_summary_report.csv): ");
        String pathStr = scanner.nextLine().trim();
        fileService.exportToCsv(manager.getProductSummaries(), Path.of(pathStr));
        System.out.println("[✓] Successfully exported report to: " + Path.of(pathStr).toAbsolutePath());
    }

    private static Category selectCategoryUI() {
        System.out.println("Select Category:");
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, categories[i]);
        }
        System.out.print("Choice (1-" + categories.length + "): ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        return categories[choice - 1];
    }
}