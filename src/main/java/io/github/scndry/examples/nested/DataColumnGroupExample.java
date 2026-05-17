package io.github.scndry.examples.nested;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumnGroup;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Multi-row header — group flattened nested fields under a shared parent header.
 *
 * <p>Where {@link NestedObjectExample} produces path-based leaf headers
 * ({@code address/city}, {@code address/zipcode}), {@link DataColumnGroup}
 * promotes the parent to its own header row and merges across the child columns.
 * Leaf columns need an explicit {@link DataColumn} to render a non-path name;
 * with an empty {@code @DataColumn(value)} or no annotation, the header
 * falls back to the column pointer's path form.
 *
 * <pre>
 * +----+-------+----------------------+
 * |    |       |       Address        |
 * | id | name  +----------+-----------+
 * |    |       | zipcode  |   city    |
 * +----+-------+----------+-----------+
 * |  1 | Alice |   12345  |  Seoul    |
 * |  2 | Bob   |   67890  |  Busan    |
 * +----+-------+----------+-----------+
 * </pre>
 */
public class DataColumnGroupExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    // columnStyle / columnHeaderStyle = "border" — border lines for visual fixture only;
    // multi-row header cell boundaries are invisible in PNG renders without them.
    @DataGrid(columnStyle = "border", columnHeaderStyle = "border", groupHeaderStyle = "border")
    public static class Employee {
        private int id;
        private String name;
        @DataColumnGroup("Address")
        private Address address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        @DataColumn("zipcode") private String zipcode;
        @DataColumn("city") private String city;
    }

    public static void write(File file) throws Exception {
        var employees = List.of(
                new Employee(1, "Alice", new Address("12345", "Seoul")),
                new Employee(2, "Bob", new Address("67890", "Busan")));
        // border for visual fixture
        var styles = new StylesBuilder()
                .cellStyle("border").border().thin().end();
        var mapper = SpreadsheetMapper.builder().stylesBuilder(styles).build();
        mapper.writeValue(file, employees, Employee.class);
    }

    public static List<Employee> read(File file) throws Exception {
        return new SpreadsheetMapper().readValues(file, Employee.class);
    }
}
