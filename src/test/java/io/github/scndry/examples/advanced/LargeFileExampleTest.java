package io.github.scndry.examples.advanced;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class LargeFileExampleTest {

    @TempDir
    Path tempDir;

    @Test
    void writeDefault() throws Exception {
        var file = tempDir.resolve("large-default.xlsx").toFile();
        LargeFileExample.writeDefault(file, 1000);
        assertThat(file.length()).isGreaterThan(0);
    }

    @Test
    void writeFileBacked() throws Exception {
        var file = tempDir.resolve("large-filebacked.xlsx").toFile();
        LargeFileExample.writeFileBacked(file, 1000);
        assertThat(file.length()).isGreaterThan(0);
    }

    @Test
    void writeFileBackedEncrypted() throws Exception {
        var file = tempDir.resolve("large-encrypted.xlsx").toFile();
        LargeFileExample.writeFileBackedEncrypted(file, 1000);
        assertThat(file.length()).isGreaterThan(0);
    }

    @Test
    void roundTripFileBacked() throws Exception {
        var file = tempDir.resolve("large-roundtrip.xlsx").toFile();
        LargeFileExample.writeDefault(file, 500);

        var result = LargeFileExample.readFileBacked(file);
        assertThat(result).hasSize(500);
        assertThat(result.get(0).getCategory()).isEqualTo("category-0");
        assertThat(result.get(499).getProduct()).isEqualTo("product-499");
    }

    @Test
    void writeWithPOI() throws Exception {
        var file = tempDir.resolve("large-poi.xlsx").toFile();
        LargeFileExample.writeWithPOI(file, 100);
        assertThat(file.length()).isGreaterThan(0);
    }
}
