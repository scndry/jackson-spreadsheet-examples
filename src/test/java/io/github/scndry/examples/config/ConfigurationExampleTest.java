package io.github.scndry.examples.config;

import io.github.scndry.examples.config.ConfigurationExample.Entry;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationExampleTest {

    @TempDir
    Path tempDir;

    @Test
    void originB2() throws Exception {
        var file = tempDir.resolve("origin.xlsx").toFile();
        var mapper = ConfigurationExample.withOrigin();
        mapper.writeValue(file, List.of(new Entry("A", 1)), Entry.class);

        var result = mapper.readValues(file, Entry.class);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("A");
    }

    @Test
    void columnReordering() throws Exception {
        // Write with default order: name, value
        var file = tempDir.resolve("reorder.xlsx").toFile();
        new SpreadsheetMapper().writeValue(file, List.of(new Entry("A", 1)), Entry.class);

        // Read with column reordering enabled
        var mapper = ConfigurationExample.withColumnReordering();
        var result = mapper.readValues(file, Entry.class);
        assertThat(result.get(0).getName()).isEqualTo("A");
        assertThat(result.get(0).getValue()).isEqualTo(1);
    }

    @Test
    void breakOnBlankRow() throws Exception {
        var file = tempDir.resolve("blank.xlsx").toFile();
        var input = List.of(new Entry("A", 1), new Entry("B", 2));
        new SpreadsheetMapper().writeValue(file, input, Entry.class);

        var mapper = ConfigurationExample.withBreakOnBlankRow();
        var result = mapper.readValues(file, Entry.class);
        assertThat(result).hasSize(2);
    }
}
