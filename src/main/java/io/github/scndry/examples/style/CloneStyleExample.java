package io.github.scndry.examples.style;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Derive a new style from an existing one with {@code cellStyle("name", "cloneFrom")}.
 * {@code "highlight"} inherits {@code "base"} (border, format) and adds a green fill.
 *
 * <pre>
 * +----------+-------+
 * | category | count |       "base" style: #,##0 format, thin border
 * +----------+-------+       "highlight" style: base + green fill
 * | Sales    | 1,500 |
 * +----------+-------+
 * </pre>
 */
public class CloneStyleExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Report {
        @DataColumn(style = "highlight")
        private String category;
        @DataColumn(style = "base")
        private int count;
    }

    public static void write(File file) throws Exception {
        var styles = new StylesBuilder()
                .cellStyle("base")
                    .dataFormat("#,##0")
                    .border().thin()
                    .end()
                .cellStyle("highlight", "base")
                    .fillForegroundColor(0x00FF00)
                    .fillPattern().solidForeground()
                    .end();

        var mapper = SpreadsheetMapper.builder()
                .stylesBuilder(styles)
                .build();

        mapper.writeValue(file, List.of(new Report("Sales", 1500)), Report.class);
    }
}
