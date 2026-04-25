package io.github.scndry.examples.jackson;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Flatten nested objects into simple column names using {@code @JsonUnwrapped}.
 *
 * <p>By default, nested object fields produce path-based headers ({@code address/city}).
 * {@code @JsonUnwrapped} promotes leaf field names to top-level headers ({@code city}),
 * producing a cleaner spreadsheet layout.</p>
 *
 * <pre>
 * Without @JsonUnwrapped:     With @JsonUnwrapped:
 * +------+--------------+     +------+-------+---------+
 * | name | address/city |     | name | city  | zipcode |
 * +------+--------------+     +------+-------+---------+
 * | Alice| Seoul        |     | Alice| Seoul | 12345   |
 * +------+--------------+     +------+-------+---------+
 * </pre>
 */
public class JsonUnwrappedExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Contact {
        private String name;
        @JsonUnwrapped
        private Address address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String city;
        private String zipcode;
    }

    public static void write(File file) throws Exception {
        var data = List.of(
                new Contact("Alice", new Address("Seoul", "12345")),
                new Contact("Bob", new Address("Busan", "67890")));
        new SpreadsheetMapper().writeValue(file, data, Contact.class);
    }

    public static List<Contact> read(File file) throws Exception {
        return new SpreadsheetMapper().readValues(file, Contact.class);
    }
}
