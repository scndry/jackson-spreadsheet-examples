package io.github.scndry.examples.quickstart;

import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple POJO mapped to a spreadsheet row. {@code @DataGrid} marks it as the root type.
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
