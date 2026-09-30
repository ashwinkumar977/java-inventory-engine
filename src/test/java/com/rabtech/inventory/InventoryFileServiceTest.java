package com.rabtech.inventory;

import com.rabtech.inventory.io.InventoryFileService;
import com.rabtech.inventory.model.ProductSummary;
import com.rabtech.inventory.service.InventoryManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryFileServiceTest {

    @Test
    @DisplayName("I/O Test: Read products from file and write summary report")
    void testFileImportAndExport(@TempDir Path tempDir) throws IOException {
        Path inputCsv = tempDir.resolve("input.csv");
        Path outputCsv = tempDir.resolve("output.csv");

        String csvData = """
                TYPE,ID,NAME,CATEGORY,BASE_PRICE,STOCK,PARAM1,PARAM2
                PHYSICAL,P999,Test Monitor,ELECTRONICS,200.00,5,3.0,10.00
                DIGITAL,D999,Test Ebook,BOOKS,30.00,50,10.0,0
                """;
        Files.writeString(inputCsv, csvData);

        InventoryManager manager = new InventoryManager();
        InventoryFileService service = new InventoryFileService();

        // Perform Input
        service.importFromCsv(inputCsv, manager);
        assertEquals(2, manager.getTotalItemCount());

        // Perform Output
        List<ProductSummary> summaries = manager.getProductSummaries();
        service.exportToCsv(summaries, outputCsv);

        assertTrue(Files.exists(outputCsv));
        List<String> outputLines = Files.readAllLines(outputCsv);
        assertEquals(3, outputLines.size()); // Header + 2 data rows
    }
}