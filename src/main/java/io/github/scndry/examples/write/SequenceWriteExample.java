package io.github.scndry.examples.write;

import com.fasterxml.jackson.databind.SequenceWriter;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.ser.SheetOutput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * Stream rows one at a time using Jackson's SequenceWriter.
 * Useful when data is generated on-the-fly (database cursor, API pagination).
 */
public class SequenceWriteExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class LogEntry {
        private String timestamp;
        private String level;
        private String message;
    }

    public static void write(File file, int rowCount) throws Exception {
        var mapper = new SpreadsheetMapper();
        var writer = mapper.sheetWriterFor(LogEntry.class);
        try (SequenceWriter seq = writer.writeValues(SheetOutput.target(file))) {
            for (int i = 0; i < rowCount; i++) {
                seq.write(new LogEntry(
                        "2024-01-15T10:00:" + String.format("%02d", i % 60),
                        i % 10 == 0 ? "ERROR" : "INFO",
                        "Log message " + i));
            }
        }
    }
}
