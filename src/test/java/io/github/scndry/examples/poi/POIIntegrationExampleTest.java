package io.github.scndry.examples.poi;

import io.github.scndry.examples.poi.POIIntegrationExample.SalesRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class POIIntegrationExampleTest {

    @TempDir
    Path tempDir;

    final List<SalesRow> q1 = List.of(
            new SalesRow("Apple", 100, 150.0),
            new SalesRow("Banana", 200, 160.0));

    final List<SalesRow> q2 = List.of(
            new SalesRow("Cherry", 50, 250.0));

    @Test
    void multiSheet() throws Exception {
        var file = tempDir.resolve("multi.xlsx").toFile();

        POIIntegrationExample.multiSheet(file, q1, q2);

        try (var wb = new XSSFWorkbook(file)) {
            assertThat(wb.getSheet("Q1")).isNotNull();
            assertThat(wb.getSheet("Q2")).isNotNull();
            assertThat(wb.getSheet("Q1").getLastRowNum()).isEqualTo(2);
            assertThat(wb.getSheet("Q2").getLastRowNum()).isEqualTo(1);
        }
    }

    @Test
    void withFormula() throws Exception {
        var file = tempDir.resolve("formula.xlsx").toFile();

        POIIntegrationExample.withFormula(file, q1);

        try (var wb = new XSSFWorkbook(file)) {
            var sheet = wb.getSheetAt(0);
            var totalRow = sheet.getRow(sheet.getLastRowNum());
            assertThat(totalRow.getCell(0).getStringCellValue()).isEqualTo("TOTAL");
            assertThat(totalRow.getCell(2).getCellFormula()).startsWith("SUM(C2:C");
        }
    }

    @Test
    void readFromNamedSheet() throws Exception {
        var file = tempDir.resolve("read-multi.xlsx").toFile();
        POIIntegrationExample.multiSheet(file, q1, q2);

        var result = POIIntegrationExample.readSheet(file, "Q1");
        assertThat(result).isEqualTo(q1);
    }
}
