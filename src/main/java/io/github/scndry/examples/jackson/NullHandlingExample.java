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
 * Handle null fields in Excel exports — skip null cells with {@code @JsonInclude(NON_NULL)}.
 *
 * <p>Without this annotation, null fields produce cells containing the text "null".
 * With {@code NON_NULL}, null fields become blank cells — the expected behavior for optional data.</p>
 *
 * <pre>
 * +-------+----------+-------------------+
 * | name  | nickname | email             |
 * +-------+----------+-------------------+
 * | Alice |          | alice@example.com |
 * | Bob   | Bobby    |                   |
 * +-------+----------+-------------------+
 * </pre>
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
