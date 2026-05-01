package io.github.scndry.examples.sheet;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.GridConfigurer;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Conditional formatting — highlight cells whose value matches a rule.
 *
 * <p>Each rule references a column from the model class and a style from {@code StylesBuilder} —
 * both name-based, resolved at write time. Fill, font, and border properties translate to
 * a DXF entry; alignment and wrap-text are silently skipped.</p>
 *
 * <pre>
 * +---------+-------+
 * | name    | score |       score &gt;= 90  -> green fill
 * +---------+-------+       score &lt;  60  -> red fill
 * | Alice   |    95 |  ← green
 * | Bob     |    50 |  ← red
 * | Carol   |    72 |
 * +---------+-------+
 * </pre>
 */
public class ConditionalFormattingExample {

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
                .cellStyle("good")
                    .fillForegroundColor(0xC6EFCE)
                    .fillPattern().solidForeground()
                    .end()
                .cellStyle("bad")
                    .fillForegroundColor(0xFFC7CE)
                    .fillPattern().solidForeground()
                    .end();

        var grid = new GridConfigurer()
                .conditionalFormatting()
                    .column("score").greaterThanOrEqual(90).style("good").end()
                .conditionalFormatting()
                    .column("score").lessThan(60).style("bad").end();

        var mapper = SpreadsheetMapper.builder()
                .stylesBuilder(styles)
                .gridConfigurer(grid)
                .build();

        var data = List.of(
                new Score("Alice", 95),
                new Score("Bob", 50),
                new Score("Carol", 72));

        mapper.writeValue(file, data, Score.class);
    }
}
