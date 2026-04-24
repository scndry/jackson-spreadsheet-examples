package io.github.scndry.examples.poi;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Direct POI Sheet/Workbook integration — you control the workbook lifecycle.
 */
public class POIIntegrationExample {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class SalesRow {
        private String product;
        private int quantity;
        private double revenue;
    }

    /**
     * Multiple sheets in one workbook.
     */
    public static void multiSheet(File file, List<SalesRow> q1, List<SalesRow> q2) throws Exception {
        var mapper = new SpreadsheetMapper();
        try (var wb = new SXSSFWorkbook()) {
            mapper.writeValue(wb.createSheet("Q1"), q1, SalesRow.class);
            mapper.writeValue(wb.createSheet("Q2"), q2, SalesRow.class);
            try (var out = new FileOutputStream(file)) {
                wb.write(out);
            }
        }
    }

    /**
     * Post-processing — add formulas after data binding.
     */
    public static void withFormula(File file, List<SalesRow> data) throws Exception {
        var mapper = new SpreadsheetMapper();
        try (var wb = new SXSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Sales");
            mapper.writeValue(sheet, data, SalesRow.class);

            int lastRow = sheet.getLastRowNum();
            Row totalRow = sheet.createRow(lastRow + 1);
            totalRow.createCell(0).setCellValue("TOTAL");
            totalRow.createCell(2).setCellFormula(
                    "SUM(C2:C" + (lastRow + 1) + ")");

            try (var out = new FileOutputStream(file)) {
                wb.write(out);
            }
        }
    }

    /**
     * Read from multiple sheets in one workbook.
     */
    public static List<SalesRow> readSheet(File file, String sheetName) throws Exception {
        var mapper = new SpreadsheetMapper();
        try (var wb = new XSSFWorkbook(file)) {
            return mapper.readValues(wb.getSheet(sheetName), SalesRow.class);
        }
    }
}
