package io.github.scndry.examples.jackson;

import com.fasterxml.jackson.annotation.*;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * Standard Jackson annotations work seamlessly with {@code @DataGrid} classes.
 *
 * <p>{@code @JsonProperty} renames columns, {@code @JsonIgnore} excludes fields,
 * {@code @JsonPropertyOrder} controls column order, and enum {@code @JsonProperty}
 * customizes cell values.</p>
 *
 * <pre>
 * Person (internalId excluded by @JsonIgnore, name renamed to fullName):
 * +----------+-----+----------+
 * | fullName | age | role     |
 * +----------+-----+----------+
 * | Alice Kim|  30 | Engineer |
 * | Bob Lee  |  25 | Designer |
 * +----------+-----+----------+
 *
 * Status (enum values customized by @JsonProperty):
 * +--------+----------+
 * | label  | priority |
 * +--------+----------+
 * | Task A | High     |
 * | Task B | Low      |
 * +--------+----------+
 * </pre>
 */
public class JacksonAnnotationExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    @JsonPropertyOrder({"fullName", "age", "role"})
    public static class Person {
        @JsonProperty("fullName")
        private String name;

        private int age;

        @JsonIgnore
        private String internalId;

        private String role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Status {
        private String label;
        private Priority priority;
    }

    public enum Priority {
        @JsonProperty("Low") LOW,
        @JsonProperty("Medium") MEDIUM,
        @JsonProperty("High") HIGH
    }

    public static void writePersons(File file) throws Exception {
        var persons = List.of(
                new Person("Alice Kim", 30, "internal-001", "Engineer"),
                new Person("Bob Lee", 25, "internal-002", "Designer"));
        new SpreadsheetMapper().writeValue(file, persons, Person.class);
    }

    public static List<Person> readPersons(File file) throws Exception {
        return new SpreadsheetMapper().readValues(file, Person.class);
    }

    public static void writeStatuses(File file) throws Exception {
        var statuses = List.of(
                new Status("Task A", Priority.HIGH),
                new Status("Task B", Priority.LOW));
        new SpreadsheetMapper().writeValue(file, statuses, Status.class);
    }
}
