package io.github.scndry.examples.nested;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumnGroup;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Attribute resolution order — when the same slot
 * (e.g. {@code columnStyle}) appears at multiple levels, the most
 * specific source wins.
 *
 * <p>Priority (highest first):
 *
 * <ol>
 *   <li>{@code @DataColumn} on the leaf property</li>
 *   <li>Innermost enclosing {@code @DataColumnGroup}</li>
 *   <li>Outer enclosing {@code @DataColumnGroup} (recurse outward)</li>
 *   <li>{@code @DataGrid} on the declaring class</li>
 *   <li>{@code @DataGrid} on the enclosing class</li>
 * </ol>
 *
 * <p>This example sets {@code columnStyle} at three levels — enclosing
 * {@code @DataGrid}, intermediate {@code @DataColumnGroup}, and the leaf
 * {@code @DataColumn} — to show which source each cell resolves to.
 *
 * <pre>
 * +----+--------------+----------+
 * |    |     g       |          |
 * | A  +-----+--------+ (n/a)    |
 * |    |  B  |   C    |          |
 * +----+-----+--------+----------+
 * | … gridLevel | groupLevel | leafLevel |
 * +-------------+------------+-----------+
 * </pre>
 */
public class AttributeResolutionExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid(columnStyle = "gridLevel")
    public static class Report {
        /** Inherits {@code "gridLevel"} from the enclosing {@code @DataGrid}. */
        @DataColumn("A") int a;
        @DataColumnGroup(value = "g", columnStyle = "groupLevel")
        Inner inner;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inner {
        /** Inherits {@code "groupLevel"} from the innermost {@code @DataColumnGroup}. */
        @DataColumn("B") int b;
        /** Overrides via leaf {@code @DataColumn(style = ...)} — {@code "leafLevel"} wins. */
        @DataColumn(value = "C", style = "leafLevel") int c;
    }

    public static void write(File file) throws Exception {
        var styles = new StylesBuilder()
                .cellStyle("gridLevel")
                    .fillForegroundColor(0xCFE2F3)
                    .fillPattern().solidForeground()
                    .end()
                .cellStyle("groupLevel")
                    .fillForegroundColor(0xFFF2CC)
                    .fillPattern().solidForeground()
                    .end()
                .cellStyle("leafLevel")
                    .fillForegroundColor(0xD9EAD3)
                    .fillPattern().solidForeground()
                    .font().bold().end()
                    .end();

        var mapper = SpreadsheetMapper.builder().stylesBuilder(styles).build();

        var rows = List.of(new Report(1, new Inner(2, 3)));
        mapper.writeValue(file, rows, Report.class);
    }
}
