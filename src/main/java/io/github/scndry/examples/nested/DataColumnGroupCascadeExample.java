package io.github.scndry.examples.nested;

import com.fasterxml.jackson.annotation.OptBoolean;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumnGroup;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/**
 * {@link DataColumnGroup} cascade slots — set default styles, widths, and
 * merge behavior on the group, and they apply to every child leaf column
 * unless the leaf {@link DataColumn} overrides.
 *
 * <p>Cascade chain (highest priority first):
 *
 * <ol>
 *   <li>{@code @DataColumn} on the leaf (e.g. {@code style = "amountOverride"})</li>
 *   <li>Innermost enclosing {@code @DataColumnGroup} (e.g. {@code columnStyle = "itemData"})</li>
 *   <li>Outer enclosing {@code @DataColumnGroup} (recurse outward)</li>
 *   <li>{@code @DataGrid} on the declaring class</li>
 *   <li>{@code @DataGrid} on the enclosing class</li>
 * </ol>
 *
 * <p>{@link AttributeResolutionExample} walks the full chain step by step.
 *
 * <pre>
 * +----------+----------+----------------------------+--------+
 * |          |          |          Items             |        |
 * | Order ID | Customer +---------+-------+----------+ Total  |
 * |          |          | Product | Qty   | Amount   |        |
 * +----------+----------+---------+-------+----------+--------+
 * |    1     |  Alice   | Apple   |   3   | 30.00    | 95.00  |
 * |          |          +---------+-------+----------+        |
 * |          |          | Banana  |   5   | 65.00    |        |
 * +----------+----------+---------+-------+----------+--------+
 *                       ^child header / data cells use the group cascade^
 *                                            ^Amount style overrides via @DataColumn(style=...)^
 * </pre>
 */
public class DataColumnGroupCascadeExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    // outer columnStyle / columnHeaderStyle / groupHeaderStyle = "border" for visual fixture;
    // child cell borders are added by composing border into itemHeader / itemData / amountOverride below.
    @DataGrid(mergeColumn = OptBoolean.TRUE,
            columnStyle = "border", columnHeaderStyle = "border", groupHeaderStyle = "border")
    public static class Order {
        @DataColumn("Order ID") int id;
        @DataColumn("Customer") String customer;
        @DataColumnGroup(
                value = "Items",
                columnHeaderStyle = "itemHeader",
                columnStyle = "itemData",
                mergeColumn = OptBoolean.FALSE)
        List<Item> items;
        @DataColumn("Total") BigDecimal total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        @DataColumn("Product") String product;
        @DataColumn("Qty") int qty;
        @DataColumn(value = "Amount", style = "amountOverride") BigDecimal amount;
    }

    public static void write(File file) throws Exception {
        var styles = new StylesBuilder()
                // shared "border" used by outer cells; each cascade style below
                // also carries .border().thin() so child cells render with lines
                // in the visual fixture.
                .cellStyle("border").border().thin().end()
                .cellStyle("itemHeader")
                    .font().bold().end()
                    .dataFormat().general()
                    .border().thin()
                    .end()
                .cellStyle("itemData")
                    .alignment().center()
                    .border().thin()
                    .end()
                .cellStyle("amountOverride")
                    .dataFormat().numberFloatWithComma()
                    .font().italic().end()
                    .border().thin()
                    .end();

        var mapper = SpreadsheetMapper.builder().stylesBuilder(styles).build();

        var orders = List.of(
                new Order(1, "Alice",
                        List.of(
                                new Item("Apple", 3, new BigDecimal("30.00")),
                                new Item("Banana", 5, new BigDecimal("65.00"))),
                        new BigDecimal("95.00")));

        mapper.writeValue(file, orders, Order.class);
    }
}
