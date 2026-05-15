package io.github.scndry.examples.nested;

import com.fasterxml.jackson.annotation.OptBoolean;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumnGroup;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/**
 * Invoice / order-receipt layout — line items grouped under an "Items"
 * header, with order-level fields ({@code id}, {@code customer}, totals)
 * vertically merged across all line-item rows.
 *
 * <p>{@code @DataColumnGroup} on a {@code List<NestedType>} field promotes
 * the list to its own header row that spans every flattened element column.
 * Totals declared after the list ({@code subtotal} / {@code tax} /
 * {@code total}) carry {@code merge = TRUE} and back-write into the
 * first item row.
 *
 * <pre>
 * +----+----------+--------------------------------+----------+------+-------+
 * |    |          |             Items              |          |      |       |
 * | id | customer +------+--------+-----+----------+ subtotal | tax  | total |
 * |    |          | sku  | name   | qty | amount   |          |      |       |
 * +----+----------+------+--------+-----+----------+----------+------+-------+
 * |    |          | A1   | Apple  |  3  |   3000   |          |      |       |
 * |  1 |  Alice   +------+--------+-----+----------+   8000   | 800  | 8800  |
 * |    |          | A2   | Banana |  5  |   5000   |          |      |       |
 * +----+----------+------+--------+-----+----------+----------+------+-------+
 * </pre>
 *
 * <p>The totals are serialized by Jackson <em>after</em> the items list,
 * but their cells anchor at the first item row — SSML writer's back-write
 * path inserts the cell into the already-emitted first row, then the
 * vertical merge covers the rest of the line-item rows.
 */
public class DataColumnGroupListExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Order {
        @DataColumn(value = "id", merge = OptBoolean.TRUE)
        private int id;
        @DataColumn(value = "customer", merge = OptBoolean.TRUE)
        private String customer;
        @DataColumnGroup("Items")
        private List<LineItem> items;
        @DataColumn(value = "subtotal", merge = OptBoolean.TRUE)
        private BigDecimal subtotal;
        @DataColumn(value = "tax", merge = OptBoolean.TRUE)
        private BigDecimal tax;
        @DataColumn(value = "total", merge = OptBoolean.TRUE)
        private BigDecimal total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItem {
        @DataColumn("sku") private String sku;
        @DataColumn("name") private String name;
        @DataColumn("qty") private int qty;
        @DataColumn("amount") private BigDecimal amount;
    }

    public static void write(File file) throws Exception {
        var orders = List.of(
                new Order(1, "Alice",
                        List.of(new LineItem("A1", "Apple", 3, BigDecimal.valueOf(3000)),
                                new LineItem("A2", "Banana", 5, BigDecimal.valueOf(5000))),
                        BigDecimal.valueOf(8000),
                        BigDecimal.valueOf(800),
                        BigDecimal.valueOf(8800)),
                new Order(2, "Bob",
                        List.of(new LineItem("B1", "Cherry", 2, BigDecimal.valueOf(3000))),
                        BigDecimal.valueOf(3000),
                        BigDecimal.valueOf(300),
                        BigDecimal.valueOf(3300)));
        new SpreadsheetMapper().writeValue(file, orders, Order.class);
    }
}
