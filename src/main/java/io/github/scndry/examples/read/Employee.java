package io.github.scndry.examples.read;

import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Shared model for read examples — mixed types (String, int, boolean).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@DataGrid
public class Employee {
    private String name;
    private String department;
    private int salary;
    private boolean active;
}
