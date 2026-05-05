package io.github.scndry.examples.interop;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;

import java.io.File;
import java.util.List;

/**
 * Convert XLSX to CSV — load all rows into memory, write all at once.
 *
 * <p>Demonstrates the Jackson dataformat composition: the same {@link Product} POJO
 * flows through {@code SpreadsheetMapper} for read and {@code CsvMapper} for write.
 * {@code @JsonProperty} headers carry across, so column titles match in both files.</p>
 *
 * <p>For files too large to load fully, see {@link StreamingXlsx2CsvExample}.</p>
 */
public class Xlsx2CsvExample {

    public static void convert(File xlsxIn, File csvOut) throws Exception {
        var spreadsheet = new SpreadsheetMapper();
        var csv = new CsvMapper();

        List<Product> products = spreadsheet.readValues(xlsxIn, Product.class);

        CsvSchema schema = csv.schemaFor(Product.class).withHeader();
        csv.writer(schema).writeValue(csvOut, products);
    }
}
