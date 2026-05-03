package io.github.scndry.examples.sheet;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.GridConfigurer;

import static io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.ConditionalFormats.colorScale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Conditional formatting with 3-color color scale — gradient visualization across a
 * column's value range.
 *
 * <p>{@code colorScale()} uses Excel defaults — MIN / PERCENTILE 50 / MAX thresholds
 * with the standard red → yellow → green color ramp. Use {@code colorScale(min, mid,
 * max)} for explicit NUMBER thresholds. No {@code .style()} required — the
 * visualization carries its own colors.</p>
 *
 * <pre>
 * +----------+----------+
 * | region   | revenue  |
 * +----------+----------+
 * | North    |  10,000  |  ← red (lowest)
 * | South    |  35,000  |
 * | East     |  60,000  |  ← yellow (median)
 * | West     |  85,000  |
 * | Central  | 100,000  |  ← green (highest)
 * +----------+----------+
 *
 * CF on revenue: colorScale() — gradient red → yellow → green
 * </pre>
 */
public class ConditionalFormattingColorScaleExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Sale {
        private String region;
        private double revenue;
    }

    public static void write(File file) throws Exception {
        var grid = new GridConfigurer()
                // colorScale() — Excel defaults (MIN/PERCENTILE 50/MAX, red→yellow→green)
                // Or with explicit thresholds:
                //   .conditionalFormatting("revenue", colorScale(0, 50_000, 100_000))
                .conditionalFormatting("revenue", colorScale());

        var mapper = SpreadsheetMapper.builder()
                .gridConfigurer(grid)
                .build();

        var data = List.of(
                new Sale("North", 10_000),
                new Sale("South", 35_000),
                new Sale("East", 60_000),
                new Sale("West", 85_000),
                new Sale("Central", 100_000));

        mapper.writeValue(file, data, Sale.class);
    }
}
