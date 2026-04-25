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
 * Format and style Excel exports — number formats, fonts, borders, fills, and header styling.
 *
 * <p>Use {@link io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder} to define
 * reusable cell styles. For automatic type-based formatting without manual configuration,
 * see {@link io.github.scndry.examples.style.SimpleStylesExample}.</p>
 *
 * <pre>
 * +----------+----------+-----------+
 * | customer | quantity |    amount | -- header styled (bold, grey fill)
 * +----------+----------+-----------+
 * | Alice    |      100 |  1,999.99 | -- quantity: #,##0  amount: #,##0.00
 * +----------+----------+-----------+
 * </pre>
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
