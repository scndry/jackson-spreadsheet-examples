package io.github.scndry.examples.jackson;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.ser.SheetOutput;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

/**
 * View-based column filtering — export different column subsets for different audiences.
 *
 * <pre>
 * Summary view:              Detail view:
 * +------+-------+           +------+-------+-----------+-------+
 * | name | total |           | name | total | breakdown | notes |
 * +------+-------+           +------+-------+-----------+-------+
 * | Q1   |  1000 |           | Q1   |  1000 | detail... | ...   |
 * +------+-------+           +------+-------+-----------+-------+
 * </pre>
 */
public class JsonViewExample {

    public static class Views {
        public static class Summary {}
        public static class Detail extends Summary {}
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Report {
        @JsonView(Views.Summary.class)
        private String name;
        @JsonView(Views.Summary.class)
        private int total;
        @JsonView(Views.Detail.class)
        private String breakdown;
        @JsonView(Views.Detail.class)
        private String notes;
    }

    /**
     * Write summary view — only name and total columns.
     */
    public static void writeSummary(File file, List<Report> reports) throws Exception {
        var mapper = new SpreadsheetMapper();
        mapper.sheetWriterForWithView(Report.class, Views.Summary.class)
                .writeValue(SheetOutput.target(file), reports);
    }

    /**
     * Write detail view — all columns.
     */
    public static void writeDetail(File file, List<Report> reports) throws Exception {
        var mapper = new SpreadsheetMapper();
        mapper.sheetWriterForWithView(Report.class, Views.Detail.class)
                .writeValue(SheetOutput.target(file), reports);
    }
}
