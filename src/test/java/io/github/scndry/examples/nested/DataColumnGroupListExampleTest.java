package io.github.scndry.examples.nested;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataColumnGroupListExampleTest {

    @TempDir
    Path tempDir;

    @Test
    void writeOrderWithItemList() throws Exception {
        var file = tempDir.resolve("order.xlsx").toFile();

        DataColumnGroupListExample.write(file);

        try (var wb = new XSSFWorkbook(file)) {
            Sheet sheet = wb.getSheetAt(0);

            // Row 0: id | customer | Items (group, C1:F1) | subtotal | tax | total
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("id");
            assertThat(sheet.getRow(0).getCell(1).getStringCellValue()).isEqualTo("customer");
            assertThat(sheet.getRow(0).getCell(2).getStringCellValue()).isEqualTo("Items");
            assertThat(sheet.getRow(0).getCell(6).getStringCellValue()).isEqualTo("subtotal");
            assertThat(sheet.getRow(0).getCell(7).getStringCellValue()).isEqualTo("tax");
            assertThat(sheet.getRow(0).getCell(8).getStringCellValue()).isEqualTo("total");

            // Row 1: leaf header for Items group (sku/name/qty/amount)
            assertThat(sheet.getRow(1).getCell(2).getStringCellValue()).isEqualTo("sku");
            assertThat(sheet.getRow(1).getCell(3).getStringCellValue()).isEqualTo("name");
            assertThat(sheet.getRow(1).getCell(4).getStringCellValue()).isEqualTo("qty");
            assertThat(sheet.getRow(1).getCell(5).getStringCellValue()).isEqualTo("amount");

            // Order 1: rows 2..3, id/customer/totals back-written to row 2
            assertThat((int) sheet.getRow(2).getCell(0).getNumericCellValue()).isEqualTo(1);
            assertThat(sheet.getRow(2).getCell(1).getStringCellValue()).isEqualTo("Alice");
            assertThat(sheet.getRow(2).getCell(2).getStringCellValue()).isEqualTo("A1");
            assertThat(sheet.getRow(2).getCell(3).getStringCellValue()).isEqualTo("Apple");
            assertThat((int) sheet.getRow(2).getCell(4).getNumericCellValue()).isEqualTo(3);
            assertThat(BigDecimal.valueOf(sheet.getRow(2).getCell(5).getNumericCellValue()))
                    .isEqualByComparingTo("3000");
            assertThat(BigDecimal.valueOf(sheet.getRow(2).getCell(6).getNumericCellValue()))
                    .isEqualByComparingTo("8000");
            assertThat(BigDecimal.valueOf(sheet.getRow(2).getCell(8).getNumericCellValue()))
                    .isEqualByComparingTo("8800");

            assertThat(sheet.getRow(3).getCell(2).getStringCellValue()).isEqualTo("A2");
            assertThat(sheet.getRow(3).getCell(3).getStringCellValue()).isEqualTo("Banana");

            // Order 1's outer fields vertically merged across rows 2..3
            List<CellRangeAddress> merged = sheet.getMergedRegions();
            // id
            assertThat(merged).anySatisfy(r -> {
                assertThat(r.getFirstColumn()).isEqualTo(0);
                assertThat(r.getFirstRow()).isEqualTo(2);
                assertThat(r.getLastRow()).isEqualTo(3);
            });
            // customer
            assertThat(merged).anySatisfy(r -> {
                assertThat(r.getFirstColumn()).isEqualTo(1);
                assertThat(r.getFirstRow()).isEqualTo(2);
                assertThat(r.getLastRow()).isEqualTo(3);
            });
            // total
            assertThat(merged).anySatisfy(r -> {
                assertThat(r.getFirstColumn()).isEqualTo(8);
                assertThat(r.getFirstRow()).isEqualTo(2);
                assertThat(r.getLastRow()).isEqualTo(3);
            });
            // Items group horizontal: C1:F1
            assertThat(merged).anySatisfy(r -> {
                assertThat(r.getFirstRow()).isEqualTo(0);
                assertThat(r.getLastRow()).isEqualTo(0);
                assertThat(r.getFirstColumn()).isEqualTo(2);
                assertThat(r.getLastColumn()).isEqualTo(5);
            });
        }
    }

    @Test
    void readOrdersBackFromItemList() throws Exception {
        var file = tempDir.resolve("order-roundtrip.xlsx").toFile();
        DataColumnGroupListExample.write(file);

        var orders = DataColumnGroupListExample.read(file);

        assertThat(orders).hasSize(2);

        assertThat(orders.get(0).getId()).isEqualTo(1);
        assertThat(orders.get(0).getCustomer()).isEqualTo("Alice");
        assertThat(orders.get(0).getItems()).hasSize(2);
        assertThat(orders.get(0).getItems().get(0).getSku()).isEqualTo("A1");
        assertThat(orders.get(0).getItems().get(0).getName()).isEqualTo("Apple");
        assertThat(orders.get(0).getItems().get(1).getSku()).isEqualTo("A2");
        assertThat(orders.get(0).getSubtotal()).isEqualByComparingTo("8000");
        assertThat(orders.get(0).getTotal()).isEqualByComparingTo("8800");

        assertThat(orders.get(1).getId()).isEqualTo(2);
        assertThat(orders.get(1).getCustomer()).isEqualTo("Bob");
        assertThat(orders.get(1).getItems()).hasSize(1);
        assertThat(orders.get(1).getItems().get(0).getSku()).isEqualTo("B1");
        assertThat(orders.get(1).getTotal()).isEqualByComparingTo("3300");
    }
}
