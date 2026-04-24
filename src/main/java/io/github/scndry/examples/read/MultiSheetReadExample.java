package io.github.scndry.examples.read;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.deser.SheetInput;

import java.io.File;
import java.util.List;

/**
 * Read from a specific sheet by name or index.
 */
public class MultiSheetReadExample {

    public static List<Employee> readByName(File file, String sheetName) throws Exception {
        var mapper = new SpreadsheetMapper();
        var input = SheetInput.source(file, sheetName);
        return mapper.readValues(input, Employee.class);
    }

    public static List<Employee> readByIndex(File file, int sheetIndex) throws Exception {
        var mapper = new SpreadsheetMapper();
        var input = SheetInput.source(file, sheetIndex);
        return mapper.readValues(input, Employee.class);
    }
}
