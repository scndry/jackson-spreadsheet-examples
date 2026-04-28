package io.github.scndry.examples.sheet;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.GridConfigurer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Auto filter — enable Excel's filter dropdown on the header row.
 *
 * <p>The filter range is computed automatically from the schema column count and
 * the actual row count. No manual range calculation required.</p>
 *
 * <pre>
 * +-----------+------------+-----------+
 * | name  ▾   | category ▾ | price  ▾  |   ← dropdown arrows on each header
 * +-----------+------------+-----------+
 * | Apple     | Fruit      |    1.50   |
 * | Carrot    | Vegetable  |    0.80   |
 * | Banana    | Fruit      |    0.60   |
 * +-----------+------------+-----------+
 * </pre>
 */
public class AutoFilterExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Product {
        private String name;
        private String category;
        private double price;
    }

    public static void write(File file) throws Exception {
        var grid = new GridConfigurer().autoFilter();

        var mapper = SpreadsheetMapper.builder()
                .gridConfigurer(grid)
                .build();

        var data = List.of(
                new Product("Apple", "Fruit", 1.50),
                new Product("Carrot", "Vegetable", 0.80),
                new Product("Banana", "Fruit", 0.60));

        mapper.writeValue(file, data, Product.class);
    }
}
