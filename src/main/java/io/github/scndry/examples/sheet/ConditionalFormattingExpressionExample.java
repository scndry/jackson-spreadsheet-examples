package io.github.scndry.examples.sheet;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.GridConfigurer;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;

import static io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.ConditionalFormats.expression;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Conditional formatting with arbitrary boolean Excel formula — cross-column logic.
 *
 * <p>{@code expression(formula)} maps to OOXML {@code <cfRule type="expression">}. The
 * formula is evaluated per cell in the formatting range and must return TRUE or FALSE.
 * Use for conditions involving multiple columns or functions like {@code AND},
 * {@code OR}, {@code ISBLANK} — anything that {@code cellIs} comparisons can't express.</p>
 *
 * <p>Reference cells in the formula by their Excel column letter and the first data row
 * (e.g., {@code $B2}, {@code $C2}). Excel auto-shifts the row for each cell in the
 * formatting range.</p>
 *
 * <pre>
 * +--------+-------------+-------------+
 * | name   | totalSpent  | orderCount  |
 * +--------+-------------+-------------+
 * | Alice  |    1000     |     3       |  ← VIP (high spend & few orders)
 * | Bob    |     200     |    10       |
 * | Carol  |     800     |     4       |  ← VIP
 * | Dave   |     600     |     8       |
 * | Erin   |      50     |     2       |
 * +--------+-------------+-------------+
 *
 * CF on name: expression("AND($B2>500, $C2<5)") → orange highlight (VIP)
 * </pre>
 */
public class ConditionalFormattingExpressionExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Customer {
        private String name;
        private double totalSpent;
        private int orderCount;
    }

    public static void write(File file) throws Exception {
        var styles = new StylesBuilder()
                .cellStyle("vip")
                    .fillForegroundColor(0xFFCDA0)
                    .fillPattern().solidForeground()
                    .end();

        var grid = new GridConfigurer()
                // CF on name column — highlight when totalSpent > 500 AND orderCount < 5
                .conditionalFormatting("name",
                        expression("AND($B2>500, $C2<5)").style("vip"));

        var mapper = SpreadsheetMapper.builder()
                .stylesBuilder(styles)
                .gridConfigurer(grid)
                .build();

        var data = List.of(
                new Customer("Alice", 1000.0, 3),
                new Customer("Bob", 200.0, 10),
                new Customer("Carol", 800.0, 4),
                new Customer("Dave", 600.0, 8),
                new Customer("Erin", 50.0, 2));

        mapper.writeValue(file, data, Customer.class);
    }
}
