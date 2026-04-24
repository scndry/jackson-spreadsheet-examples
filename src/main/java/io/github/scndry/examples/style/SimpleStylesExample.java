package io.github.scndry.examples.style;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * {@link StylesBuilder#simple()} applies type-based default formats automatically.
 *
 * <ul>
 *   <li>{@code float}/{@code double} (primitives) → {@code #,##0.00}</li>
 *   <li>{@code int}/{@code long} (primitives) → {@code #,##0}</li>
 *   <li>{@code Float}/{@code Double} (wrappers) → {@code 0.00}</li>
 *   <li>{@code Integer}/{@code Long} (wrappers) → {@code 0}</li>
 *   <li>{@code BigInteger}/{@code BigDecimal}/{@code String} → {@code @} (text)</li>
 *   <li>{@code Date}/{@code Calendar}/{@code LocalDateTime} → {@code yyyy-mm-dd hh:mm:ss}</li>
 *   <li>{@code LocalDate} → {@code yyyy-mm-dd}</li>
 * </ul>
 */
public class SimpleStylesExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Transaction {
        private String description;
        private int quantity;
        private double amount;
        private BigDecimal tax;
        private LocalDate date;
    }

    public static void write(File file) throws Exception {
        var mapper = SpreadsheetMapper.builder()
                .stylesBuilder(StylesBuilder.simple())
                .build();

        var data = List.of(
                new Transaction("Sale", 10, 1500.50, new BigDecimal("135.05"), LocalDate.of(2024, 1, 15)),
                new Transaction("Refund", 1, -200.00, new BigDecimal("-18.00"), LocalDate.of(2024, 1, 20)));

        mapper.writeValue(file, data, Transaction.class);
    }
}
