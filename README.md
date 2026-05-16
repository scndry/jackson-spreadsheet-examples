# jackson-spreadsheet-examples

Example project for [jackson-dataformat-spreadsheet](https://github.com/scndry/jackson-dataformat-spreadsheet) — a lightweight Java library to read and write Excel files (XLSX, XLS) directly to/from Java objects.

An alternative to writing verbose Apache POI Sheet/Row/Cell code. If you know Jackson, you already know how to use this.

## Why jackson-dataformat-spreadsheet?

| | Jackson Spreadsheet | Apache POI (direct) | EasyExcel |
|---|---|---|---|
| **Lines to read/write Excel** | 2-3 | 20+ | 3-5 |
| **Learning curve** | Low (Jackson annotations) | Steep (Sheet/Row/Cell API) | Low |
| **Memory at 100K rows** | ~128MB (streaming default) | ~500MB+ (XSSF) | ~200MB |
| **Jackson ecosystem** | Native (@JsonView, @JsonIgnore, custom serializers) | None | Limited |
| **Spring Boot integration** | Built-in (OutputStream/InputStream) | Manual | Easy |
| **Template population** | Yes (via POI Sheet) | Yes | Yes |

**Key advantages:**
- Annotation-driven mapping — no manual cell iteration
- Streaming by default — handles 100K+ rows without OutOfMemoryError
- Full Jackson feature set — @JsonView, @JsonUnwrapped, MixIn, custom serializers all work
- Falls back to POI when needed — multi-sheet, formulas, charts, templates

## 5-Minute Quick Start

**1. Add dependency**

```gradle
implementation("io.github.scndry:jackson-dataformat-spreadsheet:1.6.3")
```

**2. Define your model**

```java
@Data @DataGrid
public class Product {
    private String name;
    private int quantity;
    private double price;
}
```

**3. Write and read Excel**

```java
var mapper = new SpreadsheetMapper();

// Export to Excel
mapper.writeValue(new File("products.xlsx"), products, Product.class);

// Import from Excel
List<Product> data = mapper.readValues(new File("products.xlsx"), Product.class);
```

All examples are runnable as JUnit tests: `./gradlew test`

## Examples

### Quick Start

| Example | Description |
|---------|-------------|
| [SimpleWriteExample](src/main/java/io/github/scndry/examples/quickstart/SimpleWriteExample.java) | Export Java objects to Excel in one line |
| [SimpleReadExample](src/main/java/io/github/scndry/examples/quickstart/SimpleReadExample.java) | Import Excel to Java objects in one line |

### Read — Import Excel Data

| Example | Description |
|---------|-------------|
| [BasicReadExample](src/main/java/io/github/scndry/examples/read/BasicReadExample.java) | Import all rows or first row into typed objects |
| [MultiSheetReadExample](src/main/java/io/github/scndry/examples/read/MultiSheetReadExample.java) | Read from specific sheet by name or index |
| [StreamingReadExample](src/main/java/io/github/scndry/examples/read/StreamingReadExample.java) | Stream large files row-by-row (100K+ rows, constant memory) |
| [DateHandlingExample](src/main/java/io/github/scndry/examples/read/DateHandlingExample.java) | Automatic date conversion (LocalDate, LocalDateTime, Date) |
| [ErrorHandlingExample](src/main/java/io/github/scndry/examples/read/ErrorHandlingExample.java) | Skip invalid rows, log errors with row location |

### Write — Export Excel Data

| Example | Description |
|---------|-------------|
| [BasicWriteExample](src/main/java/io/github/scndry/examples/write/BasicWriteExample.java) | Export to file, OutputStream, named sheet, byte array |
| [StyleWriteExample](src/main/java/io/github/scndry/examples/write/StyleWriteExample.java) | Number formats, fonts, borders, fills, header styles |
| [MergeWriteExample](src/main/java/io/github/scndry/examples/write/MergeWriteExample.java) | Vertical cell merging with nested lists |
| [SequenceWriteExample](src/main/java/io/github/scndry/examples/write/SequenceWriteExample.java) | Stream rows incrementally (database cursors, pagination) |
| [HeaderCommentExample](src/main/java/io/github/scndry/examples/write/HeaderCommentExample.java) | Attach hover comments to header cells via @DataColumn(comment = ...) |

### Nested Objects

| Example | Description |
|---------|-------------|
| [NestedObjectExample](src/main/java/io/github/scndry/examples/nested/NestedObjectExample.java) | Flatten nested POJOs to columns, reconstruct on read |
| [DataColumnGroupExample](src/main/java/io/github/scndry/examples/nested/DataColumnGroupExample.java) | Multi-row header — group flattened columns under a shared parent header |
| [DataColumnGroupListExample](src/main/java/io/github/scndry/examples/nested/DataColumnGroupListExample.java) | Multi-row header over `List<NestedType>` — group element columns, vertically merge outer fields |
| [DataColumnGroupCascadeExample](src/main/java/io/github/scndry/examples/nested/DataColumnGroupCascadeExample.java) | `@DataColumnGroup` cascade slots — group-level style / merge defaults flow into child columns, leaf `@DataColumn` overrides |
| [AttributeResolutionExample](src/main/java/io/github/scndry/examples/nested/AttributeResolutionExample.java) | Attribute resolution order — leaf → innermost group → outer group → declaring `@DataGrid` → enclosing `@DataGrid` |

### Jackson Annotations

| Example | Description |
|---------|-------------|
| [JacksonAnnotationExample](src/main/java/io/github/scndry/examples/jackson/JacksonAnnotationExample.java) | @JsonProperty, @JsonIgnore, @JsonPropertyOrder, enum mapping |
| [JsonViewExample](src/main/java/io/github/scndry/examples/jackson/JsonViewExample.java) | Export different column subsets per audience |
| [MixInExample](src/main/java/io/github/scndry/examples/jackson/MixInExample.java) | Export third-party classes without modifying source |
| [JsonUnwrappedExample](src/main/java/io/github/scndry/examples/jackson/JsonUnwrappedExample.java) | Flatten nested objects with leaf field names as headers |
| [CustomSerializerExample](src/main/java/io/github/scndry/examples/jackson/CustomSerializerExample.java) | Custom cell value conversion (Yes/No booleans) |
| [NullHandlingExample](src/main/java/io/github/scndry/examples/jackson/NullHandlingExample.java) | @JsonInclude(NON_NULL) — blank cells for null fields |
| [JsonAliasReorderingExample](src/main/java/io/github/scndry/examples/jackson/JsonAliasReorderingExample.java) | `@JsonAlias` + `columnReordering(true)` — accept legacy / alternate header names |

### Format Interop (CSV ↔ XLSX)

Compose `SpreadsheetMapper` with Jackson's `CsvMapper` — same POJO, two formats.

| Example | Description |
|---------|-------------|
| [Xlsx2CsvExample](src/main/java/io/github/scndry/examples/interop/Xlsx2CsvExample.java) | XLSX → CSV (load all rows into memory) |
| [StreamingXlsx2CsvExample](src/main/java/io/github/scndry/examples/interop/StreamingXlsx2CsvExample.java) | XLSX → CSV (constant memory, row-by-row streaming) |
| [Csv2XlsxExample](src/main/java/io/github/scndry/examples/interop/Csv2XlsxExample.java) | CSV → XLSX (load all rows into memory) |
| [StreamingCsv2XlsxExample](src/main/java/io/github/scndry/examples/interop/StreamingCsv2XlsxExample.java) | CSV → XLSX (constant memory, row-by-row streaming) |

### Styling

| Example | Description |
|---------|-------------|
| [SimpleStylesExample](src/main/java/io/github/scndry/examples/style/SimpleStylesExample.java) | One-line type-based formatting (StylesBuilder.simple()) |
| [CloneStyleExample](src/main/java/io/github/scndry/examples/style/CloneStyleExample.java) | Inherit and extend cell styles |

### Sheet-Level Features

| Example | Description |
|---------|-------------|
| [ConditionalFormattingExample](src/main/java/io/github/scndry/examples/sheet/ConditionalFormattingExample.java) | Highlight cells whose value matches a rule (column + style by name) |
| [ConditionalFormattingColumnRefExample](src/main/java/io/github/scndry/examples/sheet/ConditionalFormattingColumnRefExample.java) | Schema-aware row-relative column reference (e.g., price > minPrice per row) |
| [ConditionalFormattingFormulaExample](src/main/java/io/github/scndry/examples/sheet/ConditionalFormattingFormulaExample.java) | Raw Excel formula passthrough — reference a config cell outside the data grid (POI integration) |
| [ConditionalFormattingExpressionExample](src/main/java/io/github/scndry/examples/sheet/ConditionalFormattingExpressionExample.java) | Arbitrary boolean Excel formula for cross-column logic (AND, OR, ISBLANK) |
| [ConditionalFormattingColorScaleExample](src/main/java/io/github/scndry/examples/sheet/ConditionalFormattingColorScaleExample.java) | 3-color gradient visualization across a column's value range |
| [ConditionalFormattingRangeExample](src/main/java/io/github/scndry/examples/sheet/ConditionalFormattingRangeExample.java) | between / notBetween range comparison |
| [ConditionalFormattingDateExample](src/main/java/io/github/scndry/examples/sheet/ConditionalFormattingDateExample.java) | Date type comparison (LocalDate auto-converts to Excel DATE() formula) |
| [FreezePaneExample](src/main/java/io/github/scndry/examples/sheet/FreezePaneExample.java) | Keep header row visible while scrolling |
| [AutoFilterExample](src/main/java/io/github/scndry/examples/sheet/AutoFilterExample.java) | Enable Excel's filter dropdown on the header row |

### Configuration

| Example | Description |
|---------|-------------|
| [ConfigurationExample](src/main/java/io/github/scndry/examples/config/ConfigurationExample.java) | Origin, header, column reordering, blank row handling |

### Apache POI Integration

| Example | Description |
|---------|-------------|
| [POIIntegrationExample](src/main/java/io/github/scndry/examples/poi/POIIntegrationExample.java) | Multi-sheet workbook, formulas, direct Sheet read |
| [TemplateWriteExample](src/main/java/io/github/scndry/examples/poi/TemplateWriteExample.java) | Populate pre-formatted Excel templates |

### Spring Boot (Web)

| Example | Description |
|---------|-------------|
| [ExcelController](src/main/java/io/github/scndry/examples/web/ExcelController.java) | REST API for Excel download and upload |

### Large Files & Performance

| Example | Description |
|---------|-------------|
| [LargeFileExample](src/main/java/io/github/scndry/examples/advanced/LargeFileExample.java) | 100K+ rows, file-backed shared strings, encrypted store, POI fallback |
| [BackWriteOomGuardExample](src/main/java/io/github/scndry/examples/advanced/BackWriteOomGuardExample.java) | Risky vs safe field order around nested `List<T>` — back-write buffer triggers when outer fields trail the list |

## Troubleshooting

**Header row not found** — First row must contain column headers matching field names. Use `@DataColumn("Header Name")` to override, or `useHeader(false)` if there is no header.

**OutOfMemoryError on large files** — Use `StreamingReadExample` for reads (row-by-row iteration). For writes, enable `FILE_BACKED_SHARED_STRINGS` (see `LargeFileExample`).

**Column order mismatch** — Enable `columnReordering(true)` to match columns by header name instead of position. Or use `@JsonPropertyOrder` to control output order.

**Dates appear as numbers** — Use `StylesBuilder.simple()` to auto-format date columns, or apply a custom date format via `@DataColumn(style = "date")`.

**"No @DataGrid annotation found"** — The root POJO must be annotated with `@DataGrid`. This is what tells the mapper which class defines the spreadsheet schema.

**Formula cells return the cached computed value** — The reader binds the cached value (emitted when the formula was last evaluated by Excel/POI). To force re-evaluation, open the workbook with POI directly and invoke `FormulaEvaluator.evaluateAll()` before passing the `Sheet` to the mapper (see `POIIntegrationExample`).

**ClassNotFoundException: org.h2.mvstore.MVStore** — `FILE_BACKED_SHARED_STRINGS` requires H2 on the classpath. Add the dependency:
```gradle
implementation("com.h2database:h2:2.2.224")
```

## Visual Fixture Review (maintainers)

`./gradlew visualFixtures` regenerates XLSX from each example and renders to PNG in `build/visual-fixtures/` for visual review. Requires LibreOffice locally (`brew install --cask libreoffice`); the PNG step is skipped if `soffice` is not installed.

Limited to cell-embedded visuals (fill, font, border, format, merge). View-state features (autoFilter dropdown, freeze pane split) and width-dependent visuals don't render cleanly in headless PNG and rely on POI-based tests instead.

## Requirements

- Java 17+
- [jackson-dataformat-spreadsheet](https://github.com/scndry/jackson-dataformat-spreadsheet) 1.6.3
- Spring Boot 3.5 (web examples only)
- H2 (file-backed shared strings examples only)

## License

[Apache License 2.0](LICENSE)

---

<sub>Anonymous, aggregated usage tracking via [Scarf](https://about.scarf.sh/). No personal information is collected.</sub>
<img referrerpolicy="no-referrer-when-downgrade" src="https://static.scarf.sh/a.png?x-pxid=38da6441-2b3b-4756-b338-a0b5f2ffe640&page=README.md" />
