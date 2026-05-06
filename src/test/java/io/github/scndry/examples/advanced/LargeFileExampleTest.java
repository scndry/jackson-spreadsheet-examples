package io.github.scndry.examples.advanced;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

        // Round-trip read verifies content correctness through the file-backed
        // write path. Catches data corruption regressions in the H2-backed
        // shared-strings store path. (Verifying the FILE_BACKED flag is
        // actually applied internally is a library-level concern.)
        var result = LargeFileExample.readFileBacked(file);
        assertThat(result).hasSize(1000);
        assertThat(result.get(0).getCategory()).isEqualTo("category-0");
        assertThat(result.get(999).getProduct()).isEqualTo("product-999");
    }

    @Test
    void writeFileBackedEncrypted() throws Exception {
        var file = tempDir.resolve("large-encrypted.xlsx").toFile();
        LargeFileExample.writeFileBackedEncrypted(file, 1000);
        assertThat(file.length()).isGreaterThan(0);

        // Round-trip read verifies content correctness. The encrypted temp store
        // is internal to the write call (created and deleted there), so external
        // assertion of encryption-applied is impractical; round-trip catches a
        // regression that corrupts data via the encrypted path.
        var result = LargeFileExample.readFileBacked(file);
        assertThat(result).hasSize(1000);
        assertThat(result.get(0).getCategory()).isEqualTo("category-0");
        assertThat(result.get(999).getProduct()).isEqualTo("product-999");
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

        // Round-trip read verifies content correctness through the POI User
        // Model write path. Catches data corruption regressions in the
        // SXSSFWorkbook-based write. (Verifying USE_POI_USER_MODEL is actually
        // applied internally is a library-level concern.)
        var result = new SpreadsheetMapper().readValues(file, LargeFileExample.Row.class);
        assertThat(result).hasSize(100);
        assertThat(result.get(0).getCategory()).isEqualTo("category-0");
        assertThat(result.get(99).getProduct()).isEqualTo("product-99");
    }
}
