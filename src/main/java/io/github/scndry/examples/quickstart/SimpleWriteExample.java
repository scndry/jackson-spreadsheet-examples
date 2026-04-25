package io.github.scndry.examples.quickstart;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;

import java.io.File;
import java.util.List;

/**
 * Export Java objects to an Excel file (XLSX) — one line.
 * The simplest way to write data: pass a list and a type, get an Excel file.
 *
 * <p>Alternative to Apache POI's verbose Sheet/Row/Cell API.
 * For styled exports, see {@link io.github.scndry.examples.write.StyleWriteExample}.
 * For streaming large datasets, see {@link io.github.scndry.examples.write.SequenceWriteExample}.</p>
 *
 * <pre>
 * +--------+----------+-------+
 * | name   | quantity | price |
 * +--------+----------+-------+
 * | Apple  |       10 |  1.50 |
 * | Banana |       20 |  0.80 |
 * | Cherry |        5 |  3.00 |
 * +--------+----------+-------+
 * </pre>
 */
public class SimpleWriteExample {

    public static void write(File file) throws Exception {
        var mapper = new SpreadsheetMapper();
        var products = List.of(
                new Product("Apple", 10, 1.50),
                new Product("Banana", 20, 0.80),
                new Product("Cherry", 5, 3.00));
        mapper.writeValue(file, products, Product.class);
    }
}
