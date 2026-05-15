package io.github.scndry.examples.visualfixture;

import io.github.scndry.examples.nested.DataColumnGroupExample;
import io.github.scndry.examples.nested.DataColumnGroupListExample;
import io.github.scndry.examples.sheet.ConditionalFormattingColorScaleExample;
import io.github.scndry.examples.sheet.ConditionalFormattingColumnRefExample;
import io.github.scndry.examples.sheet.ConditionalFormattingDateExample;
import io.github.scndry.examples.sheet.ConditionalFormattingExample;
import io.github.scndry.examples.sheet.ConditionalFormattingExpressionExample;
import io.github.scndry.examples.sheet.ConditionalFormattingFormulaExample;
import io.github.scndry.examples.sheet.ConditionalFormattingRangeExample;
import io.github.scndry.examples.style.CloneStyleExample;
import io.github.scndry.examples.style.SimpleStylesExample;
import io.github.scndry.examples.write.MergeWriteExample;
import io.github.scndry.examples.write.StyleWriteExample;

import java.io.File;
import java.util.List;

/**
 * Writes XLSX fixtures from each example to an output directory.
 * Used by the {@code visualFixtures} Gradle task — output is then converted
 * to PNG via LibreOffice for visual review.
 *
 * <p>Only examples whose output is meaningful in a static PNG render belong here —
 * cell-embedded visuals (fill, font, border, format, merge). View-state features
 * (autoFilter dropdown, freeze pane split) don't render in headless PNG and are
 * verified by POI-based tests instead.</p>
 *
 * <p>Width-dependent visuals are also limited: the default streaming writer doesn't
 * apply autoSize (per spec — see library GUIDE), so wide content like dates or nested
 * headers can render as {@code ###} or truncate. This reflects the actual default
 * output a user sees before manually resizing in Excel.</p>
 */
public class VisualFixtureGenerator {

    public static void main(String[] args) throws Exception {
        File outDir = new File(args.length > 0 ? args[0] : "build/visual-fixtures");
        if (outDir.exists()) {
            File[] stale = outDir.listFiles();
            if (stale != null) for (File f : stale) f.delete();
        }
        outDir.mkdirs();

        ConditionalFormattingColorScaleExample.write(new File(outDir, "conditional-formatting-color-scale.xlsx"));
        ConditionalFormattingColumnRefExample.write(new File(outDir, "conditional-formatting-column-ref.xlsx"));
        ConditionalFormattingDateExample.write(new File(outDir, "conditional-formatting-date.xlsx"));
        ConditionalFormattingExample.write(new File(outDir, "conditional-formatting.xlsx"));
        ConditionalFormattingExpressionExample.write(new File(outDir, "conditional-formatting-expression.xlsx"));
        ConditionalFormattingFormulaExample.write(new File(outDir, "conditional-formatting-formula.xlsx"));
        ConditionalFormattingRangeExample.write(new File(outDir, "conditional-formatting-range.xlsx"));
        SimpleStylesExample.write(new File(outDir, "simple-styles.xlsx"));
        CloneStyleExample.write(new File(outDir, "clone-style.xlsx"));
        StyleWriteExample.write(new File(outDir, "style-write.xlsx"), List.of(
                new StyleWriteExample.Invoice("Alice", 100, 1999.99),
                new StyleWriteExample.Invoice("Bob", 25, 49.50),
                new StyleWriteExample.Invoice("Carol", 1234, 56789.01)
        ));
        MergeWriteExample.write(new File(outDir, "merge-write.xlsx"));
        DataColumnGroupExample.write(new File(outDir, "data-column-group.xlsx"));
        DataColumnGroupListExample.write(new File(outDir, "data-column-group-list.xlsx"));

        System.out.println("Generated fixtures in: " + outDir.getAbsolutePath());
    }
}
