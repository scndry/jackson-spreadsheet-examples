package io.github.scndry.examples.poi;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Template-based writing — open an existing template, write data into it.
 * Pre-formatted headers, charts, formulas, and conditional formatting are preserved.
 */
public class TemplateWriteExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Record {
        private String item;
        private int count;
        private double total;
    }

    /**
     * Write data into a template file. Uses POI direct Sheet access.
     * Template formatting outside the data area is untouched.
     */
    public static void write(File template, File output, List<Record> data) throws Exception {
        try (var wb = new XSSFWorkbook(template)) {
            var mapper = SpreadsheetMapper.builder()
                    .origin("A2")       // header is already in the template
                    .useHeader(false)    // don't overwrite template header
                    .build();
            mapper.writeValue(wb.getSheetAt(0), data, Record.class);

            try (var out = new FileOutputStream(output)) {
                wb.write(out);
            }
        }
    }
}
