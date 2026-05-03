package io.github.scndry.examples.sheet;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.GridConfigurer;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;

import static io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.ConditionalFormats.between;
import static io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.ConditionalFormats.notBetween;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Conditional formatting with range comparisons — {@code between} and {@code notBetween}.
 *
 * <p>{@code between(low, high)} matches values inclusive of both bounds; {@code
 * notBetween(low, high)} matches values outside the range. The two are mutually
 * exclusive — a value is either in or out of the range, never both.</p>
 *
 * <pre>
 * +--------+--------+
 * | name   | score  |
 * +--------+--------+
 * | Alice  |   85   |  ← outside 60-80 (outlier, red)
 * | Bob    |   75   |  ← within 60-80 (ok, yellow)
 * | Carol  |   50   |  ← outside (outlier, red)
 * | Dave   |   65   |  ← within (ok, yellow)
 * | Erin   |   90   |  ← outside (outlier, red)
 * +--------+--------+
 * </pre>
 */
public class ConditionalFormattingRangeExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Score {
        private String name;
        private int score;
    }

    public static void write(File file) throws Exception {
        var styles = new StylesBuilder()
                .cellStyle("ok")
                    .fillForegroundColor(0xFFEB9C)
                    .fillPattern().solidForeground()
                    .end()
                .cellStyle("outlier")
                    .fillForegroundColor(0xFFC7CE)
                    .fillPattern().solidForeground()
                    .end();

        var grid = new GridConfigurer()
                .conditionalFormatting("score",
                        between(60, 80).style("ok"),
                        notBetween(60, 80).style("outlier"));

        var mapper = SpreadsheetMapper.builder()
                .stylesBuilder(styles)
                .gridConfigurer(grid)
                .build();

        var data = List.of(
                new Score("Alice", 85),
                new Score("Bob", 75),
                new Score("Carol", 50),
                new Score("Dave", 65),
                new Score("Erin", 90));

        mapper.writeValue(file, data, Score.class);
    }
}
