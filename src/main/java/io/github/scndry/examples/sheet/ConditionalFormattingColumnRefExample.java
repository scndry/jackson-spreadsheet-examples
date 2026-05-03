package io.github.scndry.examples.sheet;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.GridConfigurer;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;

import static io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.ConditionalFormats.columnRef;
import static io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.ConditionalFormats.greaterThan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Conditional formatting with schema-aware row-relative column references.
 *
 * <p>{@code columnRef(name)} resolves the schema column name to
 * {@code $<col><dataRow>} at write time so Excel auto-shifts per row in the
 * formatting range. Use for cross-column comparisons evaluated independently for
 * each row (e.g., {@code price > minPrice} per row).</p>
 *
 * <pre>
 * +---------+--------+------------+
 * | name    | price  | minPrice   |
 * +---------+--------+------------+
 * | Apple   |  2.50  |    1.00    |  ← price > minPrice  (violation, red)
 * | Banana  |  0.80  |    1.00    |
 * | Cherry  |  1.80  |    2.00    |
 * | Date    |  0.50  |    1.00    |
 * | Fig     |  3.00  |    1.50    |  ← price > minPrice  (violation, red)
 * +---------+--------+------------+
 * </pre>
 */
public class ConditionalFormattingColumnRefExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Item {
        private String name;
        private double price;
        private double minPrice;
    }

    public static void write(File file) throws Exception {
        var styles = new StylesBuilder()
                .cellStyle("violation")
                    .fillForegroundColor(0xFFC7CE)
                    .fillPattern().solidForeground()
                    .end();

        var grid = new GridConfigurer()
                .conditionalFormatting("price",
                        greaterThan(columnRef("minPrice")).style("violation"));

        var mapper = SpreadsheetMapper.builder()
                .stylesBuilder(styles)
                .gridConfigurer(grid)
                .build();

        var data = List.of(
                new Item("Apple", 2.50, 1.00),
                new Item("Banana", 0.80, 1.00),
                new Item("Cherry", 1.80, 2.00),
                new Item("Date", 0.50, 1.00),
                new Item("Fig", 3.00, 1.50));

        mapper.writeValue(file, data, Item.class);
    }
}
