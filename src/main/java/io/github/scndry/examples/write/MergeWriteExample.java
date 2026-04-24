package io.github.scndry.examples.write;

import com.fasterxml.jackson.annotation.OptBoolean;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Vertical cell merging with nested lists.
 */
public class MergeWriteExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
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

        var mapper = new SpreadsheetMapper();
        mapper.writeValue(file, orders, Order.class);
    }
}
