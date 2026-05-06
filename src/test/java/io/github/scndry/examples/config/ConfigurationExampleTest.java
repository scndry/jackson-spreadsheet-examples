package io.github.scndry.examples.config;

import io.github.scndry.examples.config.ConfigurationExample.Entry;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationExampleTest {

    @TempDir
    Path tempDir;

    @Test
    void originB2() throws Exception {
        var file = tempDir.resolve("origin.xlsx").toFile();
        var mapper = ConfigurationExample.withOrigin();
        mapper.writeValue(file, List.of(new Entry("A", 1)), Entry.class);

        // Verify via raw POI: data is actually placed at B2/B3 (not A1/A2).
        // Without origin, header would be at row 0, col 0.
        try (var wb = new XSSFWorkbook(file)) {
            var sheet = wb.getSheetAt(0);
            assertThat((Object) sheet.getRow(0)).isNull();              // Excel row 1 empty
            assertThat(sheet.getRow(1).getCell(0)).isNull();            // A2 empty
            assertThat(sheet.getRow(1).getCell(1).getStringCellValue()) // B2 = header
                    .isEqualTo("name");
            assertThat(sheet.getRow(2).getCell(1).getStringCellValue()) // B3 = data
                    .isEqualTo("A");
        }

        var result = mapper.readValues(file, Entry.class);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("A");
    }

    @Test
    void columnReordering() throws Exception {
        // Write a file with REVERSED column order (value, name) using POI directly.
        // POJO declares (name, value); without columnReordering, position-based matching
        // would put numeric "value" into the String "name" field and fail.
        var file = tempDir.resolve("reorder.xlsx").toFile();
        try (var wb = new XSSFWorkbook()) {
            var sheet = wb.createSheet();
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("value"); // reversed: value first
            header.createCell(1).setCellValue("name");  // reversed: name second
            var dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(1);
            dataRow.createCell(1).setCellValue("A");
            try (var out = new FileOutputStream(file)) {
                wb.write(out);
            }
        }

        // With columnReordering, columns match by header name regardless of position.
        var mapper = ConfigurationExample.withColumnReordering();
        var result = mapper.readValues(file, Entry.class);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("A");
        assertThat(result.get(0).getValue()).isEqualTo(1);
    }

    @Test
    void breakOnBlankRow() throws Exception {
        // TODO: substantively exercise BREAK_ON_BLANK_ROW. The library detects
        // a blank row only when the OOXML contains an explicit <row> element
        // with no cells; POI's writer optimizes away empty rows, so building
        // such a file from outside the library is non-trivial. Current test
        // only verifies the feature flag does not break normal reads.
        var file = tempDir.resolve("blank.xlsx").toFile();
        var input = List.of(new Entry("A", 1), new Entry("B", 2));
        new SpreadsheetMapper().writeValue(file, input, Entry.class);

        var mapper = ConfigurationExample.withBreakOnBlankRow();
        var result = mapper.readValues(file, Entry.class);
        assertThat(result).hasSize(2);
    }
}
