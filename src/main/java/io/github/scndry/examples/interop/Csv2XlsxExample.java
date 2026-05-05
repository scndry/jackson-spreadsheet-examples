package io.github.scndry.examples.interop;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;

import java.io.File;
import java.util.List;

/**
 * Convert CSV to XLSX — load all rows into memory, write all at once.
 *
 * <p>Reverse of {@link Xlsx2CsvExample}. {@code CsvMapper} reads with a header-aware schema,
 * {@code SpreadsheetMapper} writes the typed list to XLSX. The same {@link Product} POJO
 * is reused, no remapping.</p>
 *
 * <p>For very large CSV files, see {@link StreamingCsv2XlsxExample}.</p>
 */
public class Csv2XlsxExample {

    public static void convert(File csvIn, File xlsxOut) throws Exception {
        var csv = new CsvMapper();
        var spreadsheet = new SpreadsheetMapper();

        CsvSchema schema = csv.schemaFor(Product.class).withHeader();
        List<Product> products;
        try (MappingIterator<Product> iter = csv.readerFor(Product.class).with(schema).readValues(csvIn)) {
            products = iter.readAll();
        }

        spreadsheet.writeValue(xlsxOut, products, Product.class);
    }
}
