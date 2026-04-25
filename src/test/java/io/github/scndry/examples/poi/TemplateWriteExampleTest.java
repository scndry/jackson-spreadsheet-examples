package io.github.scndry.examples.poi;

import io.github.scndry.examples.poi.TemplateWriteExample.Record;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateWriteExampleTest {

    @TempDir
    Path tempDir;

    @Test
    void writeIntoTemplate() throws Exception {
        // Create a template with styled header
        var template = tempDir.resolve("template.xlsx").toFile();
        try (var wb = new XSSFWorkbook()) {
            var sheet = wb.createSheet();
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("item");
            header.createCell(1).setCellValue("count");
            header.createCell(2).setCellValue("total");
            // Template has header only — data area is empty
            try (var out = new FileOutputStream(template)) {
                wb.write(out);
            }
        }

        var output = tempDir.resolve("output.xlsx").toFile();
        var data = List.of(
                new Record("Widget", 10, 99.90),
                new Record("Gadget", 5, 149.50));

        TemplateWriteExample.write(template, output, data);

        // Verify template header preserved + data written
        try (var wb = new XSSFWorkbook(output)) {
            var sheet = wb.getSheetAt(0);
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("item");
            assertThat(sheet.getRow(1).getCell(0).getStringCellValue()).isEqualTo("Widget");
            assertThat(sheet.getRow(2).getCell(2).getNumericCellValue()).isEqualTo(149.50);
        }
    }
}
