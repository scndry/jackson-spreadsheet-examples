package io.github.scndry.examples.read;

import io.github.scndry.jackson.dataformat.spreadsheet.SheetLocation;
import io.github.scndry.jackson.dataformat.spreadsheet.SheetMappingIterator;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle malformed rows during Excel import — skip invalid data and log errors with row locations.
 *
 * <p>Uses {@code SheetLocation.of(exception)} to extract the exact row and column
 * where parsing failed. Valid rows are collected; invalid rows are logged and skipped.
 * Essential for production imports where input quality is not guaranteed.</p>
 *
 * <pre>
 * +-------+------------+--------+--------+
 * | name  | department | salary | active |
 * +-------+------------+--------+--------+
 * | Alice | Engineering|  80000 | true   |  -> valid
 * | Bob   | Design     |  oops  | false  |  -> error at row 3: salary
 * | Carol | Marketing  |  70000 | true   |  -> valid
 * +-------+------------+--------+--------+
 *
 * Result: [Alice, Carol] + 1 error logged
 * </pre>
 */
public class ErrorHandlingExample {

    public record ImportResult<T>(List<T> rows, List<String> errors) {}

    /**
     * Read all valid rows, skip and log invalid ones.
     */
    public static ImportResult<Employee> readWithErrorRecovery(File file) throws Exception {
        var mapper = new SpreadsheetMapper();
        var reader = mapper.sheetReaderFor(Employee.class);
        var rows = new ArrayList<Employee>();
        var errors = new ArrayList<String>();

        try (SheetMappingIterator<Employee> iter = reader.readValues(file)) {
            while (iter.hasNext()) {
                try {
                    rows.add(iter.next());
                } catch (Exception e) {
                    var loc = SheetLocation.of(e);
                    String prefix = loc != null
                            ? "Row " + loc.getRow() + ", Col " + loc.getColumn() + ": " : "";
                    errors.add(prefix + e.getMessage());
                }
            }
        }
        return new ImportResult<>(rows, errors);
    }
}
