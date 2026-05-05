package io.github.scndry.examples.interop;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Shared POJO for CSV ↔ XLSX interop examples — flat fields only (CSV does not represent nesting).
 *
 * <p>{@code @JsonProperty} sets a header name that both {@code SpreadsheetMapper}
 * and {@code CsvMapper} respect, so the same column titles appear in either format.</p>
 *
 * <pre>
 * +---------+----------+-------+
 * | Name    | Quantity | Price |
 * +---------+----------+-------+
 * | Apple   |       10 |  1.50 |
 * | Banana  |       20 |  0.80 |
 * +---------+----------+-------+
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@DataGrid
public class Product {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Quantity")
    private int quantity;

    @JsonProperty("Price")
    private double price;
}
