package io.github.scndry.examples.quickstart;

import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple POJO mapped to an Excel (XLSX) spreadsheet row.
 * {@code @DataGrid} marks it as the root type for serialization/deserialization.
 *
 * <p>Each field becomes a column. No additional configuration needed —
 * just annotate with {@code @DataGrid} and pass to {@link io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper}.</p>
 *
 * <pre>
 * +--------+----------+--------+
 * | name   | quantity | price  |
 * +--------+----------+--------+
 * | Laptop |       10 | 999.99 |
 * +--------+----------+--------+
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@DataGrid
public class Product {
    private String name;
    private int quantity;
    private double price;
}
