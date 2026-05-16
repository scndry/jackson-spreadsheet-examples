package io.github.scndry.examples.jackson;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Read legacy header names via {@link JsonAlias} together with
 * {@code columnReordering(true)} — for files whose column order or
 * column titles drift over time without breaking the consumer.
 *
 * <p>{@code columnReordering(true)} matches columns by header name
 * instead of position. {@link JsonAlias} adds extra acceptable header
 * names for a field — when the file's header matches the canonical
 * {@code @DataColumn(value)} <em>or</em> any alias, the column binds to
 * that field.
 *
 * <p>Typical use cases:
 *
 * <ul>
 *   <li>A producer renames a column header (e.g. {@code "cust_id" → "Customer ID"})
 *       and the consumer accepts both during the transition.</li>
 *   <li>Multiple upstream sources use different header names for the
 *       same field.</li>
 *   <li>Imported files with snake_case or legacy header conventions.</li>
 * </ul>
 *
 * <pre>
 * Legacy file headers:    cust_id | full_name | Email
 * Canonical model:        Customer ID | Name | Email
 *                              |          |
 * Match path:        @JsonAlias({"cust_id", "CID"})
 *                                          |
 *                                @JsonAlias({"full_name"})
 * </pre>
 */
public class JsonAliasReorderingExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Customer {
        @DataColumn("Customer ID")
        @JsonAlias({"cust_id", "customer_id", "CID"})
        int id;

        @DataColumn("Name")
        @JsonAlias({"full_name", "customer_name"})
        String name;

        @DataColumn("Email")
        String email;
    }

    /** Reads a legacy-headered XLSX into {@link Customer} via {@link JsonAlias}. */
    public static List<Customer> read(File file) throws Exception {
        var mapper = SpreadsheetMapper.builder()
                .columnReordering(true)
                .build();
        return mapper.readValues(file, Customer.class);
    }

    /**
     * Writes a sample legacy-headered XLSX (snake_case headers) so the
     * {@link #read} method has a fixture to consume. Useful for local
     * testing and demoing the alias path.
     */
    public static void writeLegacyFixture(File file) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet();
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("cust_id");
            header.createCell(1).setCellValue("full_name");
            header.createCell(2).setCellValue("Email");

            Row r1 = sheet.createRow(1);
            r1.createCell(0).setCellValue(101);
            r1.createCell(1).setCellValue("Alice Park");
            r1.createCell(2).setCellValue("alice@example.com");

            Row r2 = sheet.createRow(2);
            r2.createCell(0).setCellValue(102);
            r2.createCell(1).setCellValue("Bob Lee");
            r2.createCell(2).setCellValue("bob@example.com");

            try (var out = new FileOutputStream(file)) {
                wb.write(out);
            }
        }
    }
}
