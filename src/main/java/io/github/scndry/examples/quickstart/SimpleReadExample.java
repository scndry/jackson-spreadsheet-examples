package io.github.scndry.examples.quickstart;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;

import java.io.File;
import java.util.List;

/**
 * Read an Excel file into typed POJOs — one line.
 */
public class SimpleReadExample {

    public static List<Product> read(File file) throws Exception {
        var mapper = new SpreadsheetMapper();
        return mapper.readValues(file, Product.class);
    }
}
