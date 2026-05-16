package io.github.scndry.examples.advanced;

import com.fasterxml.jackson.annotation.OptBoolean;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumnGroup;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Avoid the back-write buffer for large nested lists — declare outer
 * fields before the list, or switch to {@code USE_POI_USER_MODEL}.
 *
 * <p>When an outer (non-array-scope) field is declared <em>after</em> a
 * nested {@code List<T>}, the streaming writer buffers every inner row
 * until the outer field arrives, then back-writes it into the list's
 * first row. The buffer is heap-aware
 * ({@code max(1 MB, heap/128)}) with three safety layers:
 *
 * <ol>
 *   <li><strong>Schema-build warn</strong> — schema generator logs at
 *       WARN when an outer field follows a list field, suggesting the
 *       fix.</li>
 *   <li><strong>Pre-list fail-fast</strong> — when the list size is
 *       known at {@code writeStartArray}, the projected memory is
 *       checked against the limit and writes throw early.</li>
 *   <li><strong>Runtime monitor</strong> — if the list size is unknown
 *       (Iterator / Stream sources), the buffer's heap footprint is
 *       checked on every cell append; growth beyond the limit triggers
 *       a fail-fast.</li>
 * </ol>
 *
 * <p>The structurally safe model — outer fields first — sidesteps the
 * buffer altogether and is preferred for high-cardinality nested lists.
 * If the outer-after-list shape is required (e.g. invoice totals), keep
 * the inner list bounded or use {@code USE_POI_USER_MODEL}.
 */
public class BackWriteOomGuardExample {

    // ----------------------------------------------------------------
    // Risky shape — outer field after the list triggers back-write.
    // ----------------------------------------------------------------

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid(mergeColumn = OptBoolean.TRUE)
    public static class InvoiceRisky {
        @DataColumn("ID") int id;
        @DataColumnGroup("Items") List<Item> items;
        @DataColumn("Total") BigDecimal total;   // outer field after the list
    }

    // ----------------------------------------------------------------
    // Safe shape — outer fields declared before the list.
    // ----------------------------------------------------------------

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid(mergeColumn = OptBoolean.TRUE)
    public static class InvoiceSafe {
        @DataColumn("ID") int id;
        @DataColumn("Total") BigDecimal total;   // moved before the list
        @DataColumnGroup("Items") List<Item> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        @DataColumn("Product") String product;
        @DataColumn("Qty") int qty;
    }

    /**
     * Writes the risky shape. Schema generation logs a WARN; the buffer
     * stays small here, so the write succeeds. Use the safe shape when
     * the list grows.
     */
    public static void writeRisky(File file) throws Exception {
        var mapper = new SpreadsheetMapper();
        var data = List.of(
                new InvoiceRisky(1,
                        List.of(new Item("Apple", 3), new Item("Banana", 5)),
                        new BigDecimal("95.00")));
        mapper.writeValue(file, data, InvoiceRisky.class);
    }

    /**
     * Writes the safe shape. No back-write, no buffer accumulation,
     * scales linearly with the list.
     */
    public static void writeSafe(File file, int itemCount) throws Exception {
        var mapper = new SpreadsheetMapper();
        var items = new ArrayList<Item>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            items.add(new Item("SKU-" + i, i));
        }
        var data = List.of(new InvoiceSafe(1, new BigDecimal("0.00"), items));
        mapper.writeValue(file, data, InvoiceSafe.class);
    }
}
