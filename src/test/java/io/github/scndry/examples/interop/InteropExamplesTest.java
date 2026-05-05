package io.github.scndry.examples.interop;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InteropExamplesTest {

    @TempDir
    Path tempDir;

    final List<Product> input = List.of(
            new Product("Apple", 10, 1.50),
            new Product("Banana", 20, 0.80),
            new Product("Cherry", 15, 3.25));

    File xlsxFile;
    File csvFile;

    @BeforeEach
    void setUp() {
        xlsxFile = tempDir.resolve("products.xlsx").toFile();
        csvFile = tempDir.resolve("products.csv").toFile();
    }

    @Test
    void xlsx2CsvSimple() throws Exception {
        new SpreadsheetMapper().writeValue(xlsxFile, input, Product.class);

        Xlsx2CsvExample.convert(xlsxFile, csvFile);

        var roundTrip = readCsv(csvFile);
        assertThat(roundTrip).isEqualTo(input);
    }

    @Test
    void xlsx2CsvStreaming() throws Exception {
        new SpreadsheetMapper().writeValue(xlsxFile, input, Product.class);

        StreamingXlsx2CsvExample.convert(xlsxFile, csvFile);

        var roundTrip = readCsv(csvFile);
        assertThat(roundTrip).isEqualTo(input);
    }

    @Test
    void csv2XlsxSimple() throws Exception {
        writeCsv(csvFile, input);

        Csv2XlsxExample.convert(csvFile, xlsxFile);

        var roundTrip = new SpreadsheetMapper().readValues(xlsxFile, Product.class);
        assertThat(roundTrip).isEqualTo(input);
    }

    @Test
    void csv2XlsxStreaming() throws Exception {
        writeCsv(csvFile, input);

        StreamingCsv2XlsxExample.convert(csvFile, xlsxFile);

        var roundTrip = new SpreadsheetMapper().readValues(xlsxFile, Product.class);
        assertThat(roundTrip).isEqualTo(input);
    }

    private static List<Product> readCsv(File file) throws Exception {
        var csv = new CsvMapper();
        CsvSchema schema = csv.schemaFor(Product.class).withHeader();
        try (MappingIterator<Product> iter = csv.readerFor(Product.class).with(schema).readValues(file)) {
            return iter.readAll();
        }
    }

    private static void writeCsv(File file, List<Product> products) throws Exception {
        var csv = new CsvMapper();
        CsvSchema schema = csv.schemaFor(Product.class).withHeader();
        csv.writer(schema).writeValue(file, products);
    }
}
