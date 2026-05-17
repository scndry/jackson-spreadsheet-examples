package io.github.scndry.examples.write;

import com.fasterxml.jackson.annotation.OptBoolean;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Vertical cell merging — merge parent cells when child rows repeat (e.g., order with multiple items).
 *
 * <p>Set {@code @DataColumn(merge = OptBoolean.TRUE)} on fields that should span across
 * child rows. The library handles merge region creation automatically.</p>
 *
 * <pre>
 * +----------+---------+-----+-------+
 * | Order ID | product | qty | Total |
 * +----------+---------+-----+-------+
 * |          | Apple   |   3 |       |
 * |    1     +---------+-----+  8.50 |
 * |          | Banana  |   5 |       |
 * +----------+---------+-----+-------+
 * |    2     | Cherry  |   2 |  6.00 |
 * +----------+---------+-----+-------+
 * </pre>
 */
public class MergeWriteExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    // columnStyle / columnHeaderStyle = "border" — border lines for visual fixture only;
    // merge cell boundaries are invisible in PNG renders without them.
    @DataGrid(columnStyle = "border", columnHeaderStyle = "border")
    public static class Order {
        @DataColumn(value = "Order ID", merge = OptBoolean.TRUE)
        private int orderId;
        private List<Item> items;
        @DataColumn(value = "Total", merge = OptBoolean.TRUE)
        private double total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String product;
        private int qty;
    }

    public static void write(File file) throws Exception {
        var orders = List.of(
                new Order(1, List.of(new Item("Apple", 3), new Item("Banana", 5)), 8.50),
                new Order(2, List.of(new Item("Cherry", 2)), 6.00));

        // border for visual fixture
        var styles = new StylesBuilder()
                .cellStyle("border").border().thin().end();
        var mapper = SpreadsheetMapper.builder().stylesBuilder(styles).build();
        mapper.writeValue(file, orders, Order.class);
    }
}
