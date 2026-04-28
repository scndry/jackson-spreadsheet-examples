package io.github.scndry.examples.write;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Header comments — attach a hover comment to a header cell to document the column.
 *
 * <p>{@code @DataColumn(comment = "...")} writes a cell comment on the corresponding header cell
 * when the spreadsheet is generated. Useful for explaining unit, format, or business rules to
 * users opening the file in Excel.</p>
 *
 * <pre>
 * +----------+-------+
 * | quantity | price |   ← both header cells carry a hover comment
 * +----------+-------+
 * |       10 |  1.50 |
 * |       20 |  0.80 |
 * +----------+-------+
 * </pre>
 */
public class HeaderCommentExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Product {
        @DataColumn(comment = "Stock on hand at end of day")
        private int quantity;
        @DataColumn(comment = "Listed price in USD")
        private double price;
    }

    public static void write(File file) throws Exception {
        var mapper = new SpreadsheetMapper();

        var data = List.of(
                new Product(10, 1.50),
                new Product(20, 0.80));

        mapper.writeValue(file, data, Product.class);
    }
}
