package io.github.scndry.examples.nested;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Flatten nested Java objects into Excel columns — automatic denormalization and reconstruction.
 *
 * <p>Nested POJOs (Address, Employment) become flat columns with path-based headers
 * ({@code address/city}, {@code employment/title}). On read, the flat data reconstructs
 * the original object hierarchy. For flat leaf-name headers instead of paths,
 * see {@link io.github.scndry.examples.jackson.JsonUnwrappedExample}.</p>
 *
 * <pre>
 * +----+-------+------------------+---------------+--------------------+--------+
 * | id | name  | address/zipcode  | address/city  | employment/title   | salary |
 * +----+-------+------------------+---------------+--------------------+--------+
 * |  1 | Alice | 12345            | Seoul         | SRE                |  80000 |
 * |  2 | Bob   | 67890            | Busan         | Backend            |  75000 |
 * +----+-------+------------------+---------------+--------------------+--------+
 * </pre>
 */
public class NestedObjectExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Employee {
        private int id;
        private String name;
        private Address address;
        private Employment employment;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String zipcode;
        private String city;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Employment {
        private String title;
        private long salary;
    }

    public static void write(File file) throws Exception {
        var employees = List.of(
                new Employee(1, "Alice", new Address("12345", "Seoul"), new Employment("SRE", 80000)),
                new Employee(2, "Bob", new Address("67890", "Busan"), new Employment("Backend", 75000)));
        new SpreadsheetMapper().writeValue(file, employees, Employee.class);
    }

    public static List<Employee> read(File file) throws Exception {
        return new SpreadsheetMapper().readValues(file, Employee.class);
    }
}
