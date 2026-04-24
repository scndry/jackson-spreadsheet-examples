package io.github.scndry.examples.write;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.File;
import java.util.List;

/**
 * Apply cell styles: data formats, fonts, borders, fills, header styles.
 */
public class StyleWriteExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid(columnHeaderStyle = "header")
    public static class Invoice {
        private String customer;
        @DataColumn(style = "int")
        private int quantity;
        @DataColumn(style = "currency")
        private double amount;
    }

    public static void write(File file, List<Invoice> invoices) throws Exception {
        var styles = new StylesBuilder()
                .cellStyle("header")
                    .fillForegroundColor(IndexedColors.GREY_25_PERCENT)
                    .fillPattern().solidForeground()
                    .font().bold().end()
                    .end()
                .cellStyle("currency")
                    .dataFormat("#,##0.00")
                    .end()
                .cellStyle("int")
                    .dataFormat("#,##0")
                    .end();

        var mapper = SpreadsheetMapper.builder()
                .stylesBuilder(styles)
                .build();
        mapper.writeValue(file, invoices, Invoice.class);
    }
}
