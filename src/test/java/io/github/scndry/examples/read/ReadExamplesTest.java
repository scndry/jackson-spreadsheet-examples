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
        // Create multi-sheet file
        var multiFile = tempDir.resolve("multi.xlsx").toFile();
        try (var wb = new XSSFWorkbook()) {
            var mapper = new SpreadsheetMapper();
            mapper.writeValue(wb.createSheet("Engineering"), input, Employee.class);
            try (var out = new FileOutputStream(multiFile)) {
                wb.write(out);
            }
        }
        var result = MultiSheetReadExample.readByName(multiFile, "Engineering");
        assertThat(result).isEqualTo(input);
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
}
