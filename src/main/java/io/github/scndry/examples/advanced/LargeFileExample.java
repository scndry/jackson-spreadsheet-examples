package io.github.scndry.examples.advanced;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetFactory;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Write large Excel files (100K+ rows) efficiently — choose a memory strategy.
 *
 * <p>Default streaming writer handles most cases. For high-cardinality string columns,
 * file-backed shared strings keeps heap usage constant. Encrypted store adds AES protection
 * for sensitive data. {@code USE_POI_USER_MODEL} falls back to POI's SXSSFWorkbook
 * when you need auto-size columns or other POI-specific features.</p>
 *
 * <pre>
 * Default (in-memory SST):        File-backed SST (constant heap):
 * Heap ████████████░░░ ~512MB      Heap ████░░░░░░░░░░░ ~128MB
 * Throughput: fastest               + H2 temp file on disk
 *
 * FILE_BACKED_SHARED_STRINGS:     ENCRYPT_FILE_BACKED_STORE:
 * - Constant heap usage            - AES encryption on temp file
 * - Requires H2 on classpath       - For sensitive data at rest
 *
 * USE_POI_USER_MODEL:
 * - Falls back to POI SXSSFWorkbook
 * - Needed for auto-size columns
 * </pre>
 */
public class LargeFileExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Row {
        private String category;
        private String product;
        private String description;
        private double price;
    }

    /**
     * Default streaming writer — handles 100K+ rows efficiently.
     */
    public static void writeDefault(File file, int rowCount) throws Exception {
        var mapper = new SpreadsheetMapper();
        mapper.writeValue(file, generateData(rowCount), Row.class);
    }

    /**
     * File-backed shared strings — constant heap for high-cardinality string columns.
     * Requires {@code com.h2database:h2} on the classpath.
     */
    public static void writeFileBacked(File file, int rowCount) throws Exception {
        var mapper = SpreadsheetMapper.builder()
                .enable(SpreadsheetFactory.Feature.FILE_BACKED_SHARED_STRINGS)
                .build();
        mapper.writeValue(file, generateData(rowCount), Row.class);
    }

    /**
     * Encrypted file-backed store — AES encryption for sensitive data at rest.
     */
    public static void writeFileBackedEncrypted(File file, int rowCount) throws Exception {
        var mapper = SpreadsheetMapper.builder()
                .enable(SpreadsheetFactory.Feature.FILE_BACKED_SHARED_STRINGS)
                .enable(SpreadsheetFactory.Feature.ENCRYPT_FILE_BACKED_STORE)
                .build();
        mapper.writeValue(file, generateData(rowCount), Row.class);
    }

    /**
     * Read large files with file-backed shared strings to avoid OOM.
     */
    public static List<Row> readFileBacked(File file) throws Exception {
        var mapper = SpreadsheetMapper.builder()
                .enable(SpreadsheetFactory.Feature.FILE_BACKED_SHARED_STRINGS)
                .build();
        return mapper.readValues(file, Row.class);
    }

    /**
     * Force POI User Model path — needed for auto-size columns.
     */
    public static void writeWithPOI(File file, int rowCount) throws Exception {
        var mapper = SpreadsheetMapper.builder()
                .enable(SpreadsheetFactory.Feature.USE_POI_USER_MODEL)
                .build();
        mapper.writeValue(file, generateData(rowCount), Row.class);
    }

    private static List<Row> generateData(int count) {
        var data = new ArrayList<Row>(count);
        for (int i = 0; i < count; i++) {
            data.add(new Row(
                    "category-" + (i % 100),
                    "product-" + i,
                    "description of product " + i,
                    i * 1.5));
        }
        return data;
    }
}
