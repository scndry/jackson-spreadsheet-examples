package io.github.scndry.examples.write;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class SequenceWriteExampleTest {

    @TempDir
    Path tempDir;

    @Test
    void sequenceWrite() throws Exception {
        var file = tempDir.resolve("sequence.xlsx").toFile();

        SequenceWriteExample.write(file, 100);

        try (var wb = new XSSFWorkbook(file)) {
            var sheet = wb.getSheetAt(0);
            assertThat(sheet.getLastRowNum()).isEqualTo(100); // header + 100 data rows
        }

        // Verify readable
        var result = new SpreadsheetMapper()
                .readValues(file, SequenceWriteExample.LogEntry.class);
        assertThat(result).hasSize(100);
        assertThat(result.get(0).getLevel()).isEqualTo("ERROR");
        assertThat(result.get(1).getLevel()).isEqualTo("INFO");
    }
}
