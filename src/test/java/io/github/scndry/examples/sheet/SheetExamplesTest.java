package io.github.scndry.examples.sheet;

import org.apache.poi.ss.usermodel.ComparisonOperator;
import org.apache.poi.ss.usermodel.ConditionType;
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

    @Test
    void conditionalFormattingColumnRef() throws Exception {
        var file = tempDir.resolve("cf-column-ref.xlsx").toFile();
        ConditionalFormattingColumnRefExample.write(file);
        try (var wb = new XSSFWorkbook(file)) {
            var scf = wb.getSheetAt(0).getSheetConditionalFormatting();
            assertThat(scf.getNumConditionalFormattings()).isEqualTo(1);
            var rule = scf.getConditionalFormattingAt(0).getRule(0);
            assertThat(rule.getComparisonOperation()).isEqualTo(ComparisonOperator.GT);
            assertThat(rule.getFormula1()).isEqualTo("$C2");
        }
    }

    @Test
    void conditionalFormattingFormula() throws Exception {
        var file = tempDir.resolve("cf-formula.xlsx").toFile();
        ConditionalFormattingFormulaExample.write(file);
        try (var wb = new XSSFWorkbook(file)) {
            var scf = wb.getSheetAt(0).getSheetConditionalFormatting();
            assertThat(scf.getNumConditionalFormattings()).isEqualTo(1);
            var rule = scf.getConditionalFormattingAt(0).getRule(0);
            assertThat(rule.getComparisonOperation()).isEqualTo(ComparisonOperator.GT);
            assertThat(rule.getFormula1()).isEqualTo("$D$2");

            var sheet = wb.getSheetAt(0);
            assertThat(sheet.getRow(0).getCell(3).getStringCellValue()).isEqualTo("Benchmark");
            assertThat(sheet.getRow(1).getCell(3).getNumericCellValue()).isEqualTo(1.50);
        }
    }

    @Test
    void conditionalFormattingExpression() throws Exception {
        var file = tempDir.resolve("cf-expression.xlsx").toFile();
        ConditionalFormattingExpressionExample.write(file);
        try (var wb = new XSSFWorkbook(file)) {
            var scf = wb.getSheetAt(0).getSheetConditionalFormatting();
            assertThat(scf.getNumConditionalFormattings()).isEqualTo(1);
            var rule = scf.getConditionalFormattingAt(0).getRule(0);
            assertThat(rule.getConditionType()).isEqualTo(ConditionType.FORMULA);
            assertThat(rule.getFormula1()).isEqualTo("AND($B2>500, $C2<5)");
        }
    }

    @Test
    void conditionalFormattingColorScale() throws Exception {
        var file = tempDir.resolve("cf-color-scale.xlsx").toFile();
        ConditionalFormattingColorScaleExample.write(file);
        try (var wb = new XSSFWorkbook(file)) {
            var scf = wb.getSheetAt(0).getSheetConditionalFormatting();
            assertThat(scf.getNumConditionalFormattings()).isEqualTo(1);
            var rule = scf.getConditionalFormattingAt(0).getRule(0);
            assertThat(rule.getConditionType()).isEqualTo(ConditionType.COLOR_SCALE);
            assertThat(rule.getColorScaleFormatting().getNumControlPoints()).isEqualTo(3);
        }
    }

    @Test
    void conditionalFormattingRange() throws Exception {
        var file = tempDir.resolve("cf-range.xlsx").toFile();
        ConditionalFormattingRangeExample.write(file);
        try (var wb = new XSSFWorkbook(file)) {
            var scf = wb.getSheetAt(0).getSheetConditionalFormatting();
            assertThat(scf.getNumConditionalFormattings()).isEqualTo(2);

            var rule1 = scf.getConditionalFormattingAt(0).getRule(0);
            assertThat(rule1.getComparisonOperation()).isEqualTo(ComparisonOperator.BETWEEN);
            assertThat(rule1.getFormula1()).isEqualTo("60");
            assertThat(rule1.getFormula2()).isEqualTo("80");

            var rule2 = scf.getConditionalFormattingAt(1).getRule(0);
            assertThat(rule2.getComparisonOperation()).isEqualTo(ComparisonOperator.NOT_BETWEEN);
        }
    }

    @Test
    void conditionalFormattingDate() throws Exception {
        var file = tempDir.resolve("cf-date.xlsx").toFile();
        ConditionalFormattingDateExample.write(file);
        try (var wb = new XSSFWorkbook(file)) {
            var scf = wb.getSheetAt(0).getSheetConditionalFormatting();
            assertThat(scf.getNumConditionalFormattings()).isEqualTo(1);
            var rule = scf.getConditionalFormattingAt(0).getRule(0);
            assertThat(rule.getComparisonOperation()).isEqualTo(ComparisonOperator.GT);
            assertThat(rule.getFormula1()).isEqualTo("DATE(2026,1,1)");
        }
    }
}
