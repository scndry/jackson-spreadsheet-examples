package io.github.scndry.examples.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Customize how cell values are written and read — convert booleans to "Yes"/"No" in Excel.
 *
 * <p>Use Jackson's {@code @JsonSerialize} / {@code @JsonDeserialize} to transform values
 * during export and import. Any custom serializer that works with JSON works with spreadsheets too.</p>
 *
 * <pre>
 * +------------+-----------+
 * | title      | completed |
 * +------------+-----------+
 * | Write docs | Yes       |
 * | Fix bug    | No        |
 * +------------+-----------+
 * </pre>
 */
public class CustomSerializerExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Task {
        private String title;
        @JsonSerialize(using = YesNoSerializer.class)
        @JsonDeserialize(using = YesNoDeserializer.class)
        private boolean completed;
    }

    public static class YesNoSerializer extends StdSerializer<Boolean> {
        public YesNoSerializer() { super(Boolean.class); }

        @Override
        public void serialize(Boolean value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(Boolean.TRUE.equals(value) ? "Yes" : "No");
        }
    }

    public static class YesNoDeserializer extends StdDeserializer<Boolean> {
        public YesNoDeserializer() { super(Boolean.class); }

        @Override
        public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return "Yes".equalsIgnoreCase(p.getText());
        }
    }

    public static void write(File file) throws Exception {
        var data = List.of(
                new Task("Write docs", true),
                new Task("Fix bug", false));
        new SpreadsheetMapper().writeValue(file, data, Task.class);
    }

    public static List<Task> read(File file) throws Exception {
        return new SpreadsheetMapper().readValues(file, Task.class);
    }
}
