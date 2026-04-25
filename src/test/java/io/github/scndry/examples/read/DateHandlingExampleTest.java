package io.github.scndry.examples.read;

import io.github.scndry.examples.read.DateHandlingExample.Event;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DateHandlingExampleTest {

    @TempDir
    Path tempDir;

    @Test
    void writeDateFormats() throws Exception {
        var file = tempDir.resolve("dates.xlsx").toFile();

        DateHandlingExample.write(file);

        try (var wb = new XSSFWorkbook(file)) {
            var row = wb.getSheetAt(0).getRow(1);
            // eventDate formatted as yyyy-mm-dd
            assertThat(row.getCell(1).getCellStyle().getDataFormatString()).isEqualTo("yyyy-mm-dd");
            // createdAt formatted as yyyy-mm-dd hh:mm:ss
            assertThat(row.getCell(2).getCellStyle().getDataFormatString()).isEqualTo("yyyy-mm-dd hh:mm:ss");
        }
    }

    @Test
    void readDatesRoundTrip() throws Exception {
        var file = tempDir.resolve("dates-rt.xlsx").toFile();

        DateHandlingExample.write(file);
        var result = DateHandlingExample.read(file);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEventDate()).isEqualTo(LocalDate.of(2024, 6, 15));
    }
}
