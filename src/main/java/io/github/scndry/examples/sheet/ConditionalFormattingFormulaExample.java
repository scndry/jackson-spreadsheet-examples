package io.github.scndry.examples.sheet;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.GridConfigurer;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;

import static io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.ConditionalFormats.formula;
import static io.github.scndry.jackson.dataformat.spreadsheet.schema.grid.ConditionalFormats.greaterThan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Conditional formatting with raw Excel formula passthrough — references a benchmark
 * cell that lives outside the data grid.
 *
 * <p>{@code formula(text)} emits the text verbatim into the OOXML formula element. Use
 * for cell references, functions, or expressions outside the schema. The example below
 * places a config cell at {@code D2} (set via POI direct API), then references it from
 * the CF rule. End users edit {@code D2} alone to change the threshold for the entire
 * column.</p>
 *
 * <p>Pattern: jackson-spreadsheet writes the data grid into a POI {@code Sheet}, then
 * POI direct API writes the benchmark cell outside the grid.</p>
 *
 * <pre>
 *      A         B         D
 *  +--------+--------+   +-----------+
 *  | name   | price  |   | Benchmark |  ← D1 label (POI)
 *  +--------+--------+   +-----------+
 *  | Apple  |  2.50  |←  |   1.50    |  ← D2 value (POI)
 *  | Banana |  0.80  |   +-----------+
 *  | Cherry |  1.80  |←
 *  | Date   |  0.50  |
 *  | Fig    |  3.00  |←
 *  +--------+--------+
 *           ↑
 *           CF: price > $D$2 → yellow highlight
 * </pre>
 */
public class ConditionalFormattingFormulaExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Item {
        private String name;
        private double price;
    }

    public static void write(File file) throws Exception {
        var styles = new StylesBuilder()
                .cellStyle("aboveBenchmark")
                    .fillForegroundColor(0xFFEB9C)
                    .fillPattern().solidForeground()
                    .end();

        var grid = new GridConfigurer()
                .conditionalFormatting("price",
                        greaterThan(formula("$D$2")).style("aboveBenchmark"));

        var mapper = SpreadsheetMapper.builder()
                .stylesBuilder(styles)
                .gridConfigurer(grid)
                .build();

        var data = List.of(
                new Item("Apple", 2.50),
                new Item("Banana", 0.80),
                new Item("Cherry", 1.80),
                new Item("Date", 0.50),
                new Item("Fig", 3.00));

        try (var wb = new SXSSFWorkbook()) {
            Sheet sheet = wb.createSheet();
            mapper.writeValue(sheet, data, Item.class);

            // POI direct API — write benchmark cell outside the data grid (column D)
            sheet.getRow(0).createCell(3).setCellValue("Benchmark");
            sheet.getRow(1).createCell(3).setCellValue(1.50);

            try (var out = new FileOutputStream(file)) {
                wb.write(out);
            }
        }
    }
}
