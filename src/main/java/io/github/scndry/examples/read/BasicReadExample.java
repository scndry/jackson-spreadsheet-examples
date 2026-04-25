package io.github.scndry.examples.read;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;

import java.io.File;
import java.util.List;

/**
 * Import Excel (XLSX) data into Java objects — read all rows or just the first row.
 *
 * <p>Use {@code readAll} for small-to-medium files loaded into memory at once.
 * For large files (100K+ rows), use {@link StreamingReadExample} to avoid OutOfMemoryError.</p>
 */
public class BasicReadExample {

    public static List<Employee> readAll(File file) throws Exception {
        var mapper = new SpreadsheetMapper();
        return mapper.readValues(file, Employee.class);
    }

    public static Employee readFirst(File file) throws Exception {
        var mapper = new SpreadsheetMapper();
        return mapper.readValue(file, Employee.class);
    }
}
