package io.github.scndry.examples.read;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;

import java.io.File;
import java.util.List;

/**
 * Read all rows from an Excel file into typed POJOs.
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
