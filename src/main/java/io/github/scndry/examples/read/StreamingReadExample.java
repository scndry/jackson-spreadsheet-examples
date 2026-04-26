package io.github.scndry.examples.read;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.SheetMappingIterator;
import io.github.scndry.jackson.dataformat.spreadsheet.SheetLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Stream large Excel files (100K+ rows) row-by-row — constant memory footprint.
 * Avoids OutOfMemoryError by processing one row at a time via {@code SheetMappingIterator}.
 *
 * <p>Two patterns: single-row iteration with location tracking, and batch processing
 * for bulk database inserts. For simple full-load reads, see {@link BasicReadExample}.</p>
 *
 * <pre>
 * +-------+-------------+--------+--------+
 * | name  | department  | salary | active |  -- reads row-by-row
 * +-------+-------------+--------+--------+
 * | Alice | Engineering |  80000 | true   |  -> consumer.accept(alice)
 * | Bob   | Design      |  75000 | false  |  -> consumer.accept(bob)
 * | ...   | ...         |    ... | ...    |
 * +-------+-------------+--------+--------+
 * </pre>
 */
public class StreamingReadExample {

    /**
     * Iterate rows one at a time with location tracking.
     */
    public static void iterateWithLocation(File file, Consumer<Employee> consumer) throws Exception {
        var mapper = new SpreadsheetMapper();
        var reader = mapper.sheetReaderFor(Employee.class);
        try (SheetMappingIterator<Employee> iter = reader.readValues(file)) {
            while (iter.hasNext()) {
                Employee e = iter.next();
                SheetLocation loc = iter.getCurrentLocation();
                consumer.accept(e);
            }
        }
    }

    /**
     * Batch processing — collect rows in batches for bulk inserts.
     */
    public static void batchProcess(File file, int batchSize, Consumer<List<Employee>> batchConsumer) throws Exception {
        var mapper = new SpreadsheetMapper();
        var reader = mapper.sheetReaderFor(Employee.class);
        var batch = new ArrayList<Employee>(batchSize);
        try (SheetMappingIterator<Employee> iter = reader.readValues(file)) {
            while (iter.hasNext()) {
                batch.add(iter.next());
                if (batch.size() >= batchSize) {
                    batchConsumer.accept(List.copyOf(batch));
                    batch.clear();
                }
            }
        }
        if (!batch.isEmpty()) {
            batchConsumer.accept(List.copyOf(batch));
        }
    }
}
