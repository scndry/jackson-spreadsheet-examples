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
 * Freeze pane — keep the header row (and optionally leading columns) visible while scrolling.
 *
 * <p>{@code freezePane(colSplit, rowSplit)} delegates to POI {@code Sheet#createFreezePane}.
 * The most common use is freezing the header row only.</p>
 *
 * <pre>
 * +---------+----------+--------+   ← header row stays visible
 * | name    | quantity | price  |     while data rows scroll
 * +=========+==========+========+   ← split line
 * | Apple   |       10 |   1.50 |
 * | Banana  |       20 |   0.80 |
 * | ...     |      ... |    ... |
 * +---------+----------+--------+
 * </pre>
 */
public class FreezePaneExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Product {
        private String name;
        private int quantity;
        private double price;
    }

    public static void write(File file) throws Exception {
        var grid = new GridConfigurer().freezePane(0, 1);

        var mapper = SpreadsheetMapper.builder()
                .gridConfigurer(grid)
                .build();

        var data = List.of(
                new Product("Apple", 10, 1.50),
                new Product("Banana", 20, 0.80),
                new Product("Cherry", 30, 2.40));

        mapper.writeValue(file, data, Product.class);
    }
}
