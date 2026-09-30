package com.rabtech.inventory.io;

import com.rabtech.inventory.model.*;
import com.rabtech.inventory.service.InventoryManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class InventoryFileService {

    /**
     * INPUT OPERATION: Reads inventory products from a CSV file into InventoryManager.
     * Expected CSV Line Format:
     * PHYSICAL,P101,Gaming Laptop,ELECTRONICS,1000.00,10,2.5,10.00
     * DIGITAL,D201,Java Guide,BOOKS,50.00,100,20.0,0
     */
    public void importFromCsv(Path filePath, InventoryManager manager) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("Input file does not exist: " + filePath);
        }

        List<String> lines = Files.readAllLines(filePath);
        if (lines.isEmpty()) return;

        // Skip header line (index 0)
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split(",");
            String type = tokens[0].trim();
            String id = tokens[1].trim();
            String name = tokens[2].trim();
            Category category = Category.valueOf(tokens[3].trim().toUpperCase());
            BigDecimal basePrice = new BigDecimal(tokens[4].trim());
            int stock = Integer.parseInt(tokens[5].trim());

            if ("PHYSICAL".equalsIgnoreCase(type)) {
                double weight = Double.parseDouble(tokens[6].trim());
                BigDecimal shippingRate = new BigDecimal(tokens[7].trim());
                manager.addProduct(new PhysicalProduct(id, name, basePrice, category, stock, weight, shippingRate));
            } else if ("DIGITAL".equalsIgnoreCase(type)) {
                double discount = Double.parseDouble(tokens[6].trim());
                manager.addProduct(new DigitalProduct(id, name, basePrice, category, stock, discount));
            }
        }
    }

    /**
     * OUTPUT OPERATION: Writes the product summary report into a CSV file.
     */
    public void exportToCsv(List<ProductSummary> summaries, Path outputPath) throws IOException {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("ID,NAME,CATEGORY,FINAL_PRICE,STOCK_QTY\n");

        for (ProductSummary summary : summaries) {
            csvContent.append(String.format("%s,%s,%s,%s,%d\n",
                    summary.id(),
                    summary.name(),
                    summary.category(),
                    summary.finalPrice().toPlainString(),
                    summary.stockQuantity()));
        }

        Files.writeString(outputPath, csvContent.toString(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }
}