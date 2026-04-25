package io.github.scndry.examples.jackson;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;

import java.io.File;
import java.util.List;

/**
 * Export third-party classes to Excel without modifying their source — use Jackson Mix-ins.
 *
 * <p>Define a mix-in class with {@code @DataGrid} and {@code @DataColumn} annotations,
 * then register it on the mapper. Useful for DTOs from external libraries, generated code,
 * or legacy classes you cannot annotate directly.</p>
 *
 * <pre>
 * +------+-------------+--------+
 * | Code | Desc        | Amount |
 * +------+-------------+--------+
 * | A001 | Widget      | 29.99  |
 * | B002 | Gadget      | 49.99  |
 * +------+-------------+--------+
 * </pre>
 */
public class MixInExample {

    /**
     * Third-party class — cannot modify source.
     */
    public static class ExternalRecord {
        private String code;
        private String description;
        private double value;

        public ExternalRecord() {}
        public ExternalRecord(String code, String description, double value) {
            this.code = code;
            this.description = description;
            this.value = value;
        }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
    }

    /**
     * Mix-in class — annotations applied to ExternalRecord.
     */
    @DataGrid
    public static abstract class ExternalRecordMixIn {
        @DataColumn("Code")
        abstract String getCode();
        @DataColumn("Desc")
        abstract String getDescription();
        @DataColumn("Amount")
        abstract double getValue();
    }

    public static void write(File file, List<ExternalRecord> records) throws Exception {
        var mapper = new SpreadsheetMapper();
        mapper.addMixIn(ExternalRecord.class, ExternalRecordMixIn.class);
        mapper.writeValue(file, records, ExternalRecord.class);
    }

    public static List<ExternalRecord> read(File file) throws Exception {
        var mapper = new SpreadsheetMapper();
        mapper.addMixIn(ExternalRecord.class, ExternalRecordMixIn.class);
        return mapper.readValues(file, ExternalRecord.class);
    }
}
