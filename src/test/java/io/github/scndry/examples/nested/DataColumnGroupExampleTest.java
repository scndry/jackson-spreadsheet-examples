package io.github.scndry.examples.nested;

import io.github.scndry.examples.nested.DataColumnGroupExample.Address;
import io.github.scndry.examples.nested.DataColumnGroupExample.Employee;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataColumnGroupExampleTest {

    @TempDir
    Path tempDir;

    @Test
    void writeAndReadGroupedHeader() throws Exception {
        var file = tempDir.resolve("group.xlsx").toFile();

        DataColumnGroupExample.write(file);

        try (var wb = new XSSFWorkbook(file)) {
            Sheet sheet = wb.getSheetAt(0);

            // Row 0: id, name, Address (merged 2..3), Address
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("id");
            assertThat(sheet.getRow(0).getCell(1).getStringCellValue()).isEqualTo("name");
            assertThat(sheet.getRow(0).getCell(2).getStringCellValue()).isEqualTo("Address");

            // Row 1: leaf header — zipcode, city under Address
            assertThat(sheet.getRow(1).getCell(2).getStringCellValue()).isEqualTo("zipcode");
            assertThat(sheet.getRow(1).getCell(3).getStringCellValue()).isEqualTo("city");

            // Merge regions: Address horizontal (row 0, cols 2..3), id / name vertical (rows 0..1)
            List<CellRangeAddress> merged = sheet.getMergedRegions();
            assertThat(merged).anySatisfy(r -> {
                assertThat(r.getFirstRow()).isEqualTo(0);
                assertThat(r.getLastRow()).isEqualTo(0);
                assertThat(r.getFirstColumn()).isEqualTo(2);
                assertThat(r.getLastColumn()).isEqualTo(3);
            });

            // Row 2: first data row
            assertThat((int) sheet.getRow(2).getCell(0).getNumericCellValue()).isEqualTo(1);
            assertThat(sheet.getRow(2).getCell(1).getStringCellValue()).isEqualTo("Alice");
            assertThat(sheet.getRow(2).getCell(2).getStringCellValue()).isEqualTo("12345");
            assertThat(sheet.getRow(2).getCell(3).getStringCellValue()).isEqualTo("Seoul");
        }

        var employees = DataColumnGroupExample.read(file);
        assertThat(employees).hasSize(2);
        assertThat(employees.get(0).getAddress()).isEqualTo(new Address("12345", "Seoul"));
        assertThat(employees.get(1).getAddress()).isEqualTo(new Address("67890", "Busan"));
    }
}
