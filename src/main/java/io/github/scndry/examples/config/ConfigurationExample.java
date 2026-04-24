package io.github.scndry.examples.config;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.deser.SheetParser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Configuration options: origin, header, column reordering, blank row handling.
 */
public class ConfigurationExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Entry {
        private String name;
        private int value;
    }

    /**
     * Start reading/writing at B2 instead of A1.
     */
    public static SpreadsheetMapper withOrigin() {
        return SpreadsheetMapper.builder()
                .origin("B2")
                .build();
    }

    /**
     * No header row — data starts at origin.
     */
    public static SpreadsheetMapper withoutHeader() {
        return SpreadsheetMapper.builder()
                .useHeader(false)
                .build();
    }

    /**
     * Match columns by header name instead of position.
     */
    public static SpreadsheetMapper withColumnReordering() {
        return SpreadsheetMapper.builder()
                .columnReordering(true)
                .build();
    }

    /**
     * Stop reading at the first blank row.
     */
    public static SpreadsheetMapper withBreakOnBlankRow() {
        return SpreadsheetMapper.builder()
                .enable(SheetParser.Feature.BREAK_ON_BLANK_ROW)
                .build();
    }
}
