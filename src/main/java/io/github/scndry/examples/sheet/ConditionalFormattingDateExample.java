package io.github.scndry.examples.sheet;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.GridConfigurer;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;

import static io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.ConditionalFormats.greaterThan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * Conditional formatting with date operands — {@code LocalDate}, {@code LocalDateTime},
 * {@code Date}, and {@code Calendar} all auto-convert to the appropriate Excel formula.
 *
 * <p>Date types map to {@code DATE(year, month, day)} at write time; date-time types
 * add {@code +TIME(hour, minute, second)}. Prefer {@code LocalDate} /
 * {@code LocalDateTime} for deterministic comparisons — {@code Date} carries a system
 * timezone dependence (the resulting formula varies with {@code ZoneId.systemDefault()}).</p>
 *
 * <pre>
 * +--------------+--------------+
 * | name         | date         |
 * +--------------+--------------+
 * | Conference   | 2026-03-15   |  ← > 2026-01-01 (recent, blue)
 * | Workshop     | 2025-11-20   |
 * | Hackathon    | 2026-06-10   |  ← recent
 * | Webinar      | 2025-08-05   |
 * | Summit       | 2026-02-28   |  ← recent
 * +--------------+--------------+
 *
 * CF on date: greaterThan(LocalDate.of(2026, 1, 1)) → light blue
 * </pre>
 */
public class ConditionalFormattingDateExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Event {
        private String name;
        @DataColumn(width = 14)   // streaming writer doesn't auto-size — set fixed width
        private LocalDate date;
    }

    public static void write(File file) throws Exception {
        var styles = StylesBuilder.simple()
                .cellStyle("recent")
                    .fillForegroundColor(0xCFE2F3)
                    .fillPattern().solidForeground()
                    .end();

        var grid = new GridConfigurer()
                .conditionalFormatting("date",
                        greaterThan(LocalDate.of(2026, 1, 1)).style("recent"));

        var mapper = SpreadsheetMapper.builder()
                .stylesBuilder(styles)
                .gridConfigurer(grid)
                .build();

        var data = List.of(
                new Event("Conference", LocalDate.of(2026, 3, 15)),
                new Event("Workshop", LocalDate.of(2025, 11, 20)),
                new Event("Hackathon", LocalDate.of(2026, 6, 10)),
                new Event("Webinar", LocalDate.of(2025, 8, 5)),
                new Event("Summit", LocalDate.of(2026, 2, 28)));

        mapper.writeValue(file, data, Event.class);
    }
}
