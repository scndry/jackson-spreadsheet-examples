package io.github.scndry.examples.nested;

import io.github.scndry.examples.nested.NestedObjectExample.Address;
import io.github.scndry.examples.nested.NestedObjectExample.Employee;
import io.github.scndry.examples.nested.NestedObjectExample.Employment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class NestedObjectExampleTest {

    @TempDir
    Path tempDir;

    @Test
    void writeAndReadNestedObjects() throws Exception {
        var file = tempDir.resolve("nested.xlsx").toFile();

        NestedObjectExample.write(file);

        // Verify flat columns in Excel
        try (var wb = new XSSFWorkbook(file)) {
            var header = wb.getSheetAt(0).getRow(0);
            assertThat(header.getCell(2).getStringCellValue()).isEqualTo("address/zipcode");
            assertThat(header.getCell(3).getStringCellValue()).isEqualTo("address/city");
            assertThat(header.getCell(4).getStringCellValue()).isEqualTo("employment/title");
        }

        // Round-trip
        var employees = NestedObjectExample.read(file);
        assertThat(employees).hasSize(2);
        assertThat(employees.get(0).getAddress()).isEqualTo(new Address("12345", "Seoul"));
        assertThat(employees.get(0).getEmployment()).isEqualTo(new Employment("SRE", 80000));
    }
}
