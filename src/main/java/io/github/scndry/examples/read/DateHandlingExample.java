package io.github.scndry.examples.read;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Excel dates are stored as numeric serial values.
 * {@code ExcelDateModule} is registered by default — automatic conversion
 * between Java date types and Excel date numbers. No setup needed.
 */
public class DateHandlingExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Event {
        private String title;
        private LocalDate eventDate;
        private LocalDateTime createdAt;
    }

    public static void write(File file) throws Exception {
        var mapper = SpreadsheetMapper.builder()
                .stylesBuilder(StylesBuilder.simple())
                .build();
        var data = List.of(
                new Event("Launch", LocalDate.of(2024, 6, 15),
                        LocalDateTime.of(2024, 6, 1, 9, 0)),
                new Event("Review", LocalDate.of(2024, 7, 1),
                        LocalDateTime.of(2024, 6, 20, 14, 30)));
        mapper.writeValue(file, data, Event.class);
    }

    public static List<Event> read(File file) throws Exception {
        return new SpreadsheetMapper().readValues(file, Event.class);
    }
}
