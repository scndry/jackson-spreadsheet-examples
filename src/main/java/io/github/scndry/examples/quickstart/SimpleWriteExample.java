package io.github.scndry.examples.quickstart;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;

import java.io.File;
import java.util.List;

/**
 * Write a list of POJOs to an Excel file — one line.
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
