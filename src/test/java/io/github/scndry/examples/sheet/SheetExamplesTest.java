package io.github.scndry.examples.sheet;

import org.apache.poi.ss.usermodel.ComparisonOperator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class SheetExamplesTest {

    @TempDir
    Path tempDir;

    @Test
    void conditionalFormatting() throws Exception {
        var file = tempDir.resolve("conditional-formatting.xlsx").toFile();

        ConditionalFormattingExample.write(file);

        try (var wb = new XSSFWorkbook(file)) {
            var scf = wb.getSheetAt(0).getSheetConditionalFormatting();
            assertThat(scf.getNumConditionalFormattings()).isEqualTo(2);

            var rule1 = scf.getConditionalFormattingAt(0).getRule(0);
            assertThat(rule1.getComparisonOperation()).isEqualTo(ComparisonOperator.GE);
            assertThat(rule1.getFormula1()).isEqualTo("90");

            var rule2 = scf.getConditionalFormattingAt(1).getRule(0);
            assertThat(rule2.getComparisonOperation()).isEqualTo(ComparisonOperator.LT);
            assertThat(rule2.getFormula1()).isEqualTo("60");
        }
    }

    @Test
    void freezePane() throws Exception {
        var file = tempDir.resolve("freeze-pane.xlsx").toFile();

        FreezePaneExample.write(file);

        try (var wb = new XSSFWorkbook(file)) {
            var pane = wb.getSheetAt(0).getPaneInformation();
            assertThat(pane.getHorizontalSplitPosition()).isEqualTo((short) 1);
            assertThat(pane.getVerticalSplitPosition()).isEqualTo((short) 0);
        }
    }

    @Test
    void autoFilter() throws Exception {
        var file = tempDir.resolve("auto-filter.xlsx").toFile();

        AutoFilterExample.write(file);

        try (var wb = new XSSFWorkbook(file)) {
            var ctAutoFilter = ((XSSFSheet) wb.getSheetAt(0)).getCTWorksheet().getAutoFilter();
            assertThat(ctAutoFilter).isNotNull();
            // streaming writer applies the filter over the maximum row range; column span (A..C) is the schema width
            assertThat(ctAutoFilter.getRef()).startsWith("A1:C");
        }
    }
}
