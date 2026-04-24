package io.github.scndry.examples.quickstart;

import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DataGrid
public class Product {
    private String name;
    private int quantity;
    private double price;
}
