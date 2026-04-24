package io.github.scndry.examples.jackson;

import io.github.scndry.examples.jackson.JacksonAnnotationExample.Person;
import io.github.scndry.examples.jackson.JacksonAnnotationExample.Priority;
import io.github.scndry.examples.jackson.JacksonAnnotationExample.Status;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonAnnotationExampleTest {

    @TempDir
    Path tempDir;

    @Test
    void jsonPropertyAndIgnore() throws Exception {
        var file = tempDir.resolve("persons.xlsx").toFile();

        JacksonAnnotationExample.writePersons(file);

        // Verify column headers
        try (var wb = new XSSFWorkbook(file)) {
            var header = wb.getSheetAt(0).getRow(0);
            assertThat(header.getCell(0).getStringCellValue()).isEqualTo("fullName");
            assertThat(header.getCell(1).getStringCellValue()).isEqualTo("age");
            assertThat(header.getCell(2).getStringCellValue()).isEqualTo("role");
            // internalId should be absent (3 columns, not 4)
            assertThat(header.getLastCellNum()).isEqualTo((short) 3);
        }

        // Round-trip
        var persons = JacksonAnnotationExample.readPersons(file);
        assertThat(persons.get(0).getName()).isEqualTo("Alice Kim");
        assertThat(persons.get(0).getInternalId()).isNull(); // @JsonIgnore
    }

    @Test
    void enumMapping() throws Exception {
        var file = tempDir.resolve("statuses.xlsx").toFile();

        JacksonAnnotationExample.writeStatuses(file);

        try (var wb = new XSSFWorkbook(file)) {
            var cell = wb.getSheetAt(0).getRow(1).getCell(1);
            assertThat(cell.getStringCellValue()).isEqualTo("High");
        }

        var statuses = new SpreadsheetMapper().readValues(file, Status.class);
        assertThat(statuses.get(0).getPriority()).isEqualTo(Priority.HIGH);
    }
}
