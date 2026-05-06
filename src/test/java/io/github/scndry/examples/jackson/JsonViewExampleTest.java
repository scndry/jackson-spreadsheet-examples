package io.github.scndry.examples.jackson;

import io.github.scndry.examples.jackson.JsonViewExample.Report;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JsonViewExampleTest {

    @TempDir
    Path tempDir;

    final List<Report> reports = List.of(
            new Report("Q1", 1000, "detail-breakdown", "some notes"),
            new Report("Q2", 2000, "detail-breakdown-2", "more notes"));

    @Test
    void summaryView() throws Exception {
        var file = tempDir.resolve("summary.xlsx").toFile();

        JsonViewExample.writeSummary(file, reports);

        try (var wb = new XSSFWorkbook(file)) {
            var header = wb.getSheetAt(0).getRow(0);
            // Summary: only name, total
            assertThat(header.getLastCellNum()).isEqualTo((short) 2);
            assertThat(header.getCell(0).getStringCellValue()).isEqualTo("name");
            assertThat(header.getCell(1).getStringCellValue()).isEqualTo("total");
        }
    }

    @Test
    void detailView() throws Exception {
        var file = tempDir.resolve("detail.xlsx").toFile();

        JsonViewExample.writeDetail(file, reports);

        // Detail view includes Summary fields (name, total) plus the Detail-only
        // fields (breakdown, notes). Asserting specific column names — not just
        // the count — catches a regression where the view returns a different
        // set of fields with the same cardinality.
        try (var wb = new XSSFWorkbook(file)) {
            var header = wb.getSheetAt(0).getRow(0);
            assertThat(header.getLastCellNum()).isEqualTo((short) 4);
            var names = new HashSet<String>();
            for (int i = 0; i < 4; i++) {
                names.add(header.getCell(i).getStringCellValue());
            }
            assertThat(names).containsExactlyInAnyOrder(
                    "name", "total", "breakdown", "notes");
        }
    }
}
