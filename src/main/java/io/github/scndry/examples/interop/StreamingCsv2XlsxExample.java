package io.github.scndry.examples.interop;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;

import java.io.File;

/**
 * Convert CSV to XLSX with constant memory — pull rows one at a time, push to XLSX writer.
 *
 * <p>Streaming variant of {@link Csv2XlsxExample}. The CSV side uses Jackson's
 * {@code MappingIterator} (line-by-line); the XLSX side uses {@code SequenceWriter}
 * over the default StringBuilder-based streaming writer. Memory stays flat regardless
 * of row count.</p>
 */
public class StreamingCsv2XlsxExample {

    public static void convert(File csvIn, File xlsxOut) throws Exception {
        var csv = new CsvMapper();
        var spreadsheet = new SpreadsheetMapper();
        CsvSchema schema = csv.schemaFor(Product.class).withHeader();

        try (MappingIterator<Product> iter = csv.readerFor(Product.class).with(schema).readValues(csvIn);
             SequenceWriter seq = spreadsheet.sheetWriterFor(Product.class).writeValues(xlsxOut)) {
            while (iter.hasNext()) {
                seq.write(iter.next());
            }
        }
    }
}
