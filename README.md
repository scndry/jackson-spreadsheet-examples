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
implementation("io.github.scndry:jackson-dataformat-spreadsheet:1.2.1")
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

### Nested Objects

| Example | Description |
|---------|-------------|
| [NestedObjectExample](src/main/java/io/github/scndry/examples/nested/NestedObjectExample.java) | Flatten nested POJOs to columns, reconstruct on read |

### Jackson Annotations

| Example | Description |
|---------|-------------|
| [JacksonAnnotationExample](src/main/java/io/github/scndry/examples/jackson/JacksonAnnotationExample.java) | @JsonProperty, @JsonIgnore, @JsonPropertyOrder, enum mapping |
| [JsonViewExample](src/main/java/io/github/scndry/examples/jackson/JsonViewExample.java) | Export different column subsets per audience |
| [MixInExample](src/main/java/io/github/scndry/examples/jackson/MixInExample.java) | Export third-party classes without modifying source |
| [JsonUnwrappedExample](src/main/java/io/github/scndry/examples/jackson/JsonUnwrappedExample.java) | Flatten nested objects with leaf field names as headers |
| [CustomSerializerExample](src/main/java/io/github/scndry/examples/jackson/CustomSerializerExample.java) | Custom cell value conversion (Yes/No booleans) |
| [NullHandlingExample](src/main/java/io/github/scndry/examples/jackson/NullHandlingExample.java) | @JsonInclude(NON_NULL) — blank cells for null fields |

### Styling

| Example | Description |
|---------|-------------|
| [SimpleStylesExample](src/main/java/io/github/scndry/examples/style/SimpleStylesExample.java) | One-line type-based formatting (StylesBuilder.simple()) |
| [CloneStyleExample](src/main/java/io/github/scndry/examples/style/CloneStyleExample.java) | Inherit and extend cell styles |

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

## Troubleshooting

**Header row not found** — First row must contain column headers matching field names. Use `@DataColumn("Header Name")` to override, or `useHeader(false)` if there is no header.

**OutOfMemoryError on large files** — Use `StreamingReadExample` for reads (row-by-row iteration). For writes, enable `FILE_BACKED_SHARED_STRINGS` (see `LargeFileExample`).

**Column order mismatch** — Enable `columnReordering(true)` to match columns by header name instead of position. Or use `@JsonPropertyOrder` to control output order.

**Dates appear as numbers** — Use `StylesBuilder.simple()` to auto-format date columns, or apply a custom date format via `@DataColumn(style = "date")`.

**"No @DataGrid annotation found"** — The root POJO must be annotated with `@DataGrid`. This is what tells the mapper which class defines the spreadsheet schema.

**Formula cells return formula text, not the computed value** — The default streaming reader parses raw XML and returns formula strings as-is. Use `USE_POI_USER_MODEL` to read via POI's `FormulaEvaluator`, or read through a POI `Sheet` directly (see `POIIntegrationExample`).

**ClassNotFoundException: org.h2.mvstore.MVStore** — `FILE_BACKED_SHARED_STRINGS` requires H2 on the classpath. Add the dependency:
```gradle
implementation("com.h2database:h2:2.2.224")
```

## Requirements

- Java 17+
- [jackson-dataformat-spreadsheet](https://github.com/scndry/jackson-dataformat-spreadsheet) 1.2.1
- Spring Boot 3.5 (web examples only)
- H2 (file-backed shared strings examples only)

## License

[Apache License 2.0](LICENSE)
