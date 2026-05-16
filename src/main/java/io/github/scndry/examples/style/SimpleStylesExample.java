package io.github.scndry.examples.style;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.schema.style.StylesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * {@link StylesBuilder#simple()} applies type-based default formats automatically.
 *
 * <ul>
 *   <li>{@code float}/{@code double} (primitives) -> {@code #,##0.00}</li>
 *   <li>{@code int}/{@code long} (primitives) -> {@code #,##0}</li>
 *   <li>{@code Float}/{@code Double} (wrappers) -> {@code 0.00}</li>
 *   <li>{@code Integer}/{@code Long} (wrappers) -> {@code 0}</li>
 *   <li>{@code BigInteger}/{@code BigDecimal}/{@code String} -> {@code @} (text)</li>
 *   <li>{@code Date}/{@code Calendar}/{@code LocalDateTime} -> {@code yyyy-mm-dd hh:mm:ss}</li>
 *   <li>{@code LocalDate} -> {@code yyyy-mm-dd}</li>
 * </ul>
 *
 * <p>The easiest way to format Excel exports — one line of configuration applies sensible defaults
 * for all numeric, date, and text columns. The {@code Transaction} model below covers each
 * format family (primitive int/long, wrapper Double, BigDecimal, LocalDate, LocalDateTime, String).</p>
 *
 * <pre>
 * +-------------+----------+-----------+-------+--------+------------+---------------------+
 * | description | quantity |    amount | price | tax    | date       | createdAt           |
 * +-------------+----------+-----------+-------+--------+------------+---------------------+
 * | Sale        |       10 | 1,500,000 | 99.99 | 135.05 | 2024-01-15 | 2024-01-15 09:30:00 |
 * | Refund      |        1 |    -2,000 | 49.99 | -18.00 | 2024-01-20 | 2024-01-20 14:00:00 |
 * +-------------+----------+-----------+-------+--------+------------+---------------------+
 *      @          #,##0        #,##0    0.00      @     yyyy-mm-dd   yyyy-mm-dd hh:mm:ss
 * </pre>
 */
public class SimpleStylesExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Transaction {
        private String description;
        private int quantity;
        @DataColumn(width = 12) private long amount;
        private Double price;
        private BigDecimal tax;
        @DataColumn(width = 12) private LocalDate date;
        @DataColumn(width = 22) private LocalDateTime createdAt;
    }

    public static void write(File file) throws Exception {
        var mapper = SpreadsheetMapper.builder()
                .stylesBuilder(StylesBuilder.simple())
                .build();

        var data = List.of(
                new Transaction("Sale", 10, 1_500_000L, 99.99, new BigDecimal("135.05"),
                        LocalDate.of(2024, 1, 15),
                        LocalDateTime.of(2024, 1, 15, 9, 30, 0)),
                new Transaction("Refund", 1, -2_000L, 49.99, new BigDecimal("-18.00"),
                        LocalDate.of(2024, 1, 20),
                        LocalDateTime.of(2024, 1, 20, 14, 0, 0)));

        mapper.writeValue(file, data, Transaction.class);
    }
}
