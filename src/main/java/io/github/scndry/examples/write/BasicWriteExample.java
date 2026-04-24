package io.github.scndry.examples.write;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.ser.SheetOutput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

/**
 * Basic write operations: file, OutputStream, named sheet, byte array.
 */
public class BasicWriteExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Order {
        @DataColumn("Order ID")
        private int id;
        private String product;
        private int quantity;
        private double price;
    }

    public static void writeToFile(File file, List<Order> orders) throws Exception {
        var mapper = new SpreadsheetMapper();
        mapper.writeValue(file, orders, Order.class);
    }

    public static void writeToOutputStream(OutputStream out, List<Order> orders) throws Exception {
        var mapper = new SpreadsheetMapper();
        mapper.writeValue(SheetOutput.target(out), orders, Order.class);
    }

    public static void writeWithSheetName(File file, List<Order> orders, String sheetName) throws Exception {
        var mapper = new SpreadsheetMapper();
        mapper.writeValue(SheetOutput.target(file, sheetName), orders, Order.class);
    }

    public static byte[] writeToBytes(List<Order> orders) throws Exception {
        var mapper = new SpreadsheetMapper();
        return mapper.writeValueAsBytes(orders, Order.class);
    }
}
