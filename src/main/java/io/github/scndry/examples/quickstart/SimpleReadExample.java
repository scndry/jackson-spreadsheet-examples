package io.github.scndry.examples.quickstart;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;

import java.io.File;
import java.util.List;

/**
 * Import an Excel file (XLSX) into typed Java objects — one line.
 * The simplest way to parse spreadsheet data: pass a file and a type, get a list of objects.
 *
 * <p>Column headers are matched to field names automatically.
 * For streaming large files without loading all rows, see {@link io.github.scndry.examples.read.StreamingReadExample}.</p>
 */
public class SimpleReadExample {

    public static List<Product> read(File file) throws Exception {
        var mapper = new SpreadsheetMapper();
        return mapper.readValues(file, Product.class);
    }
}
