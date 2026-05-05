package io.github.scndry.examples.interop;

import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.github.scndry.jackson.dataformat.spreadsheet.SheetMappingIterator;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;

import java.io.File;

/**
 * Convert XLSX to CSV with constant memory — pull rows one at a time, push to CSV writer.
 *
 * <p>Streaming variant of {@link Xlsx2CsvExample}. The XLSX side uses
 * {@code SheetMappingIterator} (default StAX-based pull); the CSV side uses Jackson's
 * {@code SequenceWriter}. Memory stays flat regardless of row count — suitable for
 * 100K+ row datasets.</p>
 */
public class StreamingXlsx2CsvExample {

    public static void convert(File xlsxIn, File csvOut) throws Exception {
        var spreadsheet = new SpreadsheetMapper();
        var csv = new CsvMapper();
        CsvSchema schema = csv.schemaFor(Product.class).withHeader();

        try (SheetMappingIterator<Product> iter = spreadsheet.sheetReaderFor(Product.class).readValues(xlsxIn);
             SequenceWriter seq = csv.writer(schema).writeValues(csvOut)) {
            while (iter.hasNext()) {
                seq.write(iter.next());
            }
        }
    }
}
