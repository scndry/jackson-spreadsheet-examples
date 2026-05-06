package io.github.scndry.examples.read;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReadExamplesTest {

    @TempDir
    Path tempDir;
    File file;

    final List<Employee> input = List.of(
            new Employee("Alice", "Engineering", 90000, true),
            new Employee("Bob", "Marketing", 70000, false),
            new Employee("Charlie", "Engineering", 85000, true));

    @BeforeEach
    void setUp() throws Exception {
        file = tempDir.resolve("employees.xlsx").toFile();
        var mapper = new SpreadsheetMapper();
        mapper.writeValue(file, input, Employee.class);
    }

    @Test
    void basicReadAll() throws Exception {
        var result = BasicReadExample.readAll(file);
        assertThat(result).isEqualTo(input);
    }

    @Test
    void basicReadFirst() throws Exception {
        var result = BasicReadExample.readFirst(file);
        assertThat(result).isEqualTo(input.get(0));
    }

    @Test
    void multiSheetReadByName() throws Exception {
        // Create a file with TWO sheets and DIFFERENT data per sheet, then read the
        // *second* sheet by name. Reading the second one ensures fall-back-to-first
        // would return the wrong data, so the test exercises name resolution.
        var multiFile = tempDir.resolve("multi.xlsx").toFile();
        var engineering = List.of(
                new Employee("Alice", "Engineering", 90000, true));
        var marketing = List.of(
                new Employee("Bob", "Marketing", 70000, false),
                new Employee("Carol", "Marketing", 72000, true));
        try (var wb = new XSSFWorkbook()) {
            var mapper = new SpreadsheetMapper();
            mapper.writeValue(wb.createSheet("Engineering"), engineering, Employee.class);
            mapper.writeValue(wb.createSheet("Marketing"), marketing, Employee.class);
            try (var out = new FileOutputStream(multiFile)) {
                wb.write(out);
            }
        }
        var result = MultiSheetReadExample.readByName(multiFile, "Marketing");
        assertThat(result).isEqualTo(marketing);
    }

    @Test
    void streamingIterate() throws Exception {
        var collected = new ArrayList<Employee>();
        StreamingReadExample.iterateWithLocation(file, collected::add);
        assertThat(collected).isEqualTo(input);
    }

    @Test
    void streamingBatch() throws Exception {
        var batches = new ArrayList<List<Employee>>();
        StreamingReadExample.batchProcess(file, 2, batches::add);
        assertThat(batches).hasSize(2);
        assertThat(batches.get(0)).hasSize(2);
        assertThat(batches.get(1)).hasSize(1);
    }

    @Test
    void errorHandlingSkipsInvalidRows() throws Exception {
        // Create file with a malformed salary value
        var badFile = tempDir.resolve("bad-data.xlsx").toFile();
        try (var wb = new XSSFWorkbook()) {
            var sheet = wb.createSheet();
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("name");
            header.createCell(1).setCellValue("department");
            header.createCell(2).setCellValue("salary");
            header.createCell(3).setCellValue("active");

            var row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Alice");
            row1.createCell(1).setCellValue("Engineering");
            row1.createCell(2).setCellValue(80000);
            row1.createCell(3).setCellValue(true);

            var row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("Bob");
            row2.createCell(1).setCellValue("Design");
            row2.createCell(2).setCellValue("not-a-number"); // invalid salary
            row2.createCell(3).setCellValue(false);

            var row3 = sheet.createRow(3);
            row3.createCell(0).setCellValue("Carol");
            row3.createCell(1).setCellValue("Marketing");
            row3.createCell(2).setCellValue(70000);
            row3.createCell(3).setCellValue(true);

            try (var out = new FileOutputStream(badFile)) {
                wb.write(out);
            }
        }

        var result = ErrorHandlingExample.readWithErrorRecovery(badFile);
        assertThat(result.rows()).extracting(Employee::getName).containsExactly("Alice", "Carol");
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0)).startsWith("Row ");
    }
}
