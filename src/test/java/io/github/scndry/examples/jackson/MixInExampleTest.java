package io.github.scndry.examples.jackson;

import io.github.scndry.examples.jackson.MixInExample.ExternalRecord;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MixInExampleTest {

    @TempDir
    Path tempDir;

    @Test
    void writeAndReadWithMixIn() throws Exception {
        var file = tempDir.resolve("mixin.xlsx").toFile();
        var records = List.of(
                new ExternalRecord("A001", "Widget", 9.99),
                new ExternalRecord("B002", "Gadget", 19.99));

        MixInExample.write(file, records);

        // Verify custom column headers from mix-in
        try (var wb = new XSSFWorkbook(file)) {
            var header = wb.getSheetAt(0).getRow(0);
            assertThat(header.getCell(0).getStringCellValue()).isEqualTo("Code");
            assertThat(header.getCell(1).getStringCellValue()).isEqualTo("Desc");
            assertThat(header.getCell(2).getStringCellValue()).isEqualTo("Amount");
        }

        // Round-trip
        var result = MixInExample.read(file);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("A001");
        assertThat(result.get(1).getValue()).isEqualTo(19.99);
    }
}
