package io.github.scndry.examples.style;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class StyleExamplesTest {

    @TempDir
    Path tempDir;

    @Test
    void simpleStyles() throws Exception {
        var file = tempDir.resolve("simple-styles.xlsx").toFile();

        SimpleStylesExample.write(file);

        try (var wb = new XSSFWorkbook(file)) {
            var row = wb.getSheetAt(0).getRow(1);
            // int quantity → #,##0
            assertThat(row.getCell(1).getCellStyle().getDataFormatString()).isEqualTo("#,##0");
            // double amount → #,##0.00
            assertThat(row.getCell(2).getCellStyle().getDataFormatString()).isEqualTo("#,##0.00");
            // BigDecimal tax → @
            assertThat(row.getCell(3).getCellStyle().getDataFormatString()).isEqualTo("@");
            // LocalDate → yyyy-mm-dd
            assertThat(row.getCell(4).getCellStyle().getDataFormatString()).isEqualTo("yyyy-mm-dd");
        }
    }

    @Test
    void cloneStyle() throws Exception {
        var file = tempDir.resolve("clone-style.xlsx").toFile();

        CloneStyleExample.write(file);

        try (var wb = new XSSFWorkbook(file)) {
            var row = wb.getSheetAt(0).getRow(1);
            // highlight inherits border from base
            assertThat(row.getCell(0).getCellStyle().getBorderLeft()).isEqualTo(BorderStyle.THIN);
            // highlight adds fill
            assertThat(row.getCell(0).getCellStyle().getFillPattern()).isEqualTo(FillPatternType.SOLID_FOREGROUND);
            // base has border + format
            assertThat(row.getCell(1).getCellStyle().getDataFormatString()).isEqualTo("#,##0");
            assertThat(row.getCell(1).getCellStyle().getBorderLeft()).isEqualTo(BorderStyle.THIN);
        }
    }
}
