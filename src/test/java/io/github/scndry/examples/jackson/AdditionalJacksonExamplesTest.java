package io.github.scndry.examples.jackson;

import io.github.scndry.examples.jackson.CustomSerializerExample.Task;
import io.github.scndry.examples.jackson.JsonUnwrappedExample.Address;
import io.github.scndry.examples.jackson.JsonUnwrappedExample.Contact;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdditionalJacksonExamplesTest {

    @TempDir
    Path tempDir;

    @Test
    void jsonUnwrapped() throws Exception {
        var file = tempDir.resolve("unwrapped.xlsx").toFile();

        JsonUnwrappedExample.write(file);

        // Headers should be leaf names, not paths
        try (var wb = new XSSFWorkbook(file)) {
            var header = wb.getSheetAt(0).getRow(0);
            assertThat(header.getCell(0).getStringCellValue()).isEqualTo("name");
            assertThat(header.getCell(1).getStringCellValue()).isEqualTo("city");
            assertThat(header.getCell(2).getStringCellValue()).isEqualTo("zipcode");
        }

        var result = JsonUnwrappedExample.read(file);
        assertThat(result.get(0).getAddress()).isEqualTo(new Address("Seoul", "12345"));
    }

    @Test
    void customSerializer() throws Exception {
        var file = tempDir.resolve("custom-ser.xlsx").toFile();

        CustomSerializerExample.write(file);

        // Cell shows "Yes"/"No" instead of true/false
        try (var wb = new XSSFWorkbook(file)) {
            assertThat(wb.getSheetAt(0).getRow(1).getCell(1).getStringCellValue()).isEqualTo("Yes");
            assertThat(wb.getSheetAt(0).getRow(2).getCell(1).getStringCellValue()).isEqualTo("No");
        }

        // Round-trip deserializes back to boolean
        var result = CustomSerializerExample.read(file);
        assertThat(result.get(0).isCompleted()).isTrue();
        assertThat(result.get(1).isCompleted()).isFalse();
    }

    @Test
    void nullHandling() throws Exception {
        var file = tempDir.resolve("null-handling.xlsx").toFile();

        NullHandlingExample.write(file);

        try (var wb = new XSSFWorkbook(file)) {
            var sheet = wb.getSheetAt(0);
            // Alice has no nickname — cell should be blank/absent
            assertThat(sheet.getRow(1).getCell(1)).isNull();
            // Bob has nickname
            assertThat(sheet.getRow(2).getCell(1).getStringCellValue()).isEqualTo("Bobby");
        }
    }
}
