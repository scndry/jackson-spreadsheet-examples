package io.github.scndry.examples.read;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.deser.SheetInput;
import io.github.scndry.jackson.dataformat.spreadsheet.SheetMappingIterator;
import io.github.scndry.jackson.dataformat.spreadsheet.deser.SheetLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Process large files row-by-row without loading all rows into memory.
 */
public class StreamingReadExample {

    /**
     * Iterate rows one at a time with location tracking.
     */
    public static void iterateWithLocation(File file, Consumer<Employee> consumer) throws Exception {
        var mapper = new SpreadsheetMapper();
        var reader = mapper.sheetReaderFor(Employee.class);
        try (SheetMappingIterator<Employee> iter = reader.readValues(SheetInput.source(file))) {
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
        try (SheetMappingIterator<Employee> iter = reader.readValues(SheetInput.source(file))) {
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
