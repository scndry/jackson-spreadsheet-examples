package io.github.scndry.examples.write;

import com.fasterxml.jackson.databind.SequenceWriter;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * Stream rows to Excel one at a time — write without holding the full dataset in memory.
 *
 * <p>Uses Jackson's {@code SequenceWriter} to emit rows incrementally.
 * Ideal for database cursors, API pagination, or any data source where rows arrive on-the-fly.
 * For reading large files in a streaming fashion, see {@link io.github.scndry.examples.read.StreamingReadExample}.</p>
 *
 * <pre>
 * +---------------------+-------+---------------+
 * | timestamp           | level | message       |
 * +---------------------+-------+---------------+
 * | 2024-01-15T10:00:00 | ERROR | Log message 0 |
 * | 2024-01-15T10:00:01 | INFO  | Log message 1 |
 * | ...                 | ...   | ...           |
 * +---------------------+-------+---------------+
 * </pre>
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
        try (SequenceWriter seq = writer.writeValues(file)) {
            for (int i = 0; i < rowCount; i++) {
                seq.write(new LogEntry(
                        "2024-01-15T10:00:" + String.format("%02d", i % 60),
                        i % 10 == 0 ? "ERROR" : "INFO",
                        "Log message " + i));
            }
        }
    }
}
