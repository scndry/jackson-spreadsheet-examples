package io.github.scndry.examples.visualfixture;

import io.github.scndry.examples.sheet.ConditionalFormattingExample;

import java.io.File;

/**
 * Writes XLSX fixtures from each example to an output directory.
 * Used by the {@code visualFixtures} Gradle task — output is then converted
 * to PNG via LibreOffice for visual review.
 *
 * <p>Only examples whose output is meaningful in a static PNG render belong here —
 * cell-embedded visuals (fill, font, border, format, merge). View-state features
 * (autoFilter dropdown, freeze pane split) don't render in headless PNG and are
 * verified by POI-based tests instead.</p>
 */
public class VisualFixtureGenerator {

    public static void main(String[] args) throws Exception {
        File outDir = new File(args.length > 0 ? args[0] : "build/visual-fixtures");
        if (outDir.exists()) {
            File[] stale = outDir.listFiles();
            if (stale != null) for (File f : stale) f.delete();
        }
        outDir.mkdirs();

        ConditionalFormattingExample.write(new File(outDir, "conditional-formatting.xlsx"));

        System.out.println("Generated fixtures in: " + outDir.getAbsolutePath());
    }
}
