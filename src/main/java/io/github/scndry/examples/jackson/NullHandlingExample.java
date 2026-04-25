package io.github.scndry.examples.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * {@code @JsonInclude(NON_NULL)} skips null cells on write.
 */
public class NullHandlingExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OptionalFields {
        private String name;
        private String nickname;
        private String email;
    }

    public static void write(File file) throws Exception {
        var data = List.of(
                new OptionalFields("Alice", null, "alice@example.com"),
                new OptionalFields("Bob", "Bobby", null));
        new SpreadsheetMapper().writeValue(file, data, OptionalFields.class);
    }
}
