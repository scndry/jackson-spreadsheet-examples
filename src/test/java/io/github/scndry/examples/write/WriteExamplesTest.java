package io.github.scndry.examples.write;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WriteExamplesTest {

    @TempDir
    Path tempDir;

    @Test
    void basicWriteToFile() throws Exception {
        var file = tempDir.resolve("orders.xlsx").toFile();
        var orders = List.of(
                new BasicWriteExample.Order(1, "Apple", 10, 1.50),
                new BasicWriteExample.Order(2, "Banana", 20, 0.80));

        BasicWriteExample.writeToFile(file, orders);

        try (var wb = new XSSFWorkbook(file)) {
            assertThat(wb.getSheetAt(0).getLastRowNum()).isEqualTo(2);
        }
    }

    @Test
    void basicWriteWithSheetName() throws Exception {
        var file = tempDir.resolve("named.xlsx").toFile();
        var orders = List.of(new BasicWriteExample.Order(1, "Apple", 10, 1.50));

        BasicWriteExample.writeWithSheetName(file, orders, "Orders");

        try (var wb = new XSSFWorkbook(file)) {
            assertThat(wb.getSheet("Orders")).isNotNull();
        }
    }

    @Test
    void basicWriteToBytes() throws Exception {
        var orders = List.of(new BasicWriteExample.Order(1, "Apple", 10, 1.50));

        byte[] bytes = BasicWriteExample.writeToBytes(orders);

        try (var wb = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertThat(wb.getSheetAt(0).getRow(1).getCell(1).getStringCellValue()).isEqualTo("Apple");
        }
    }

    @Test
    void styleWrite() throws Exception {
        var file = tempDir.resolve("styled.xlsx").toFile();
        var invoices = List.of(
                new StyleWriteExample.Invoice("Alice", 100, 1999.99));

        StyleWriteExample.write(file, invoices);

        try (var wb = new XSSFWorkbook(file)) {
            var row = wb.getSheetAt(0).getRow(1);
            assertThat(row.getCell(2).getCellStyle().getDataFormatString()).isEqualTo("#,##0.00");
        }
    }

    @Test
    void mergeWrite() throws Exception {
        var file = tempDir.resolve("merged.xlsx").toFile();

        MergeWriteExample.write(file);

        try (var wb = new XSSFWorkbook(file)) {
            var merged = wb.getSheetAt(0).getMergedRegions();
            assertThat(merged).isNotEmpty();
            assertThat(merged).anySatisfy(r ->
                    assertThat(r.getLastRow() - r.getFirstRow() + 1).isEqualTo(2));
        }
    }
}
