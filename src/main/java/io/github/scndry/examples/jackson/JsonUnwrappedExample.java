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
 * {@code @JsonUnwrapped} flattens nested objects with leaf field names as headers
 * instead of the default path-based headers ({@code address/city} → {@code city}).
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
