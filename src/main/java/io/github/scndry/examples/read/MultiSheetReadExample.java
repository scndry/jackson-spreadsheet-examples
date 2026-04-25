package io.github.scndry.examples.read;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.deser.SheetInput;

import java.io.File;
import java.util.List;

/**
 * Read data from a specific sheet in a multi-sheet Excel workbook — select by name or index.
 *
 * <p>Useful for workbooks containing multiple datasets (e.g., quarterly reports,
 * per-department data). For reading multiple sheets from a single POI Workbook,
 * see {@link io.github.scndry.examples.poi.POIIntegrationExample}.</p>
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
