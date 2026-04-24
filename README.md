# jackson-spreadsheet-examples

Example project for [jackson-dataformat-spreadsheet](https://github.com/scndry/jackson-dataformat-spreadsheet) — a Jackson extension for reading and writing Excel (XLSX/XLS) as POJOs.

All examples are runnable as JUnit tests: `./gradlew test`

## Quick Start

| Example | Description |
|---------|-------------|
| [SimpleWriteExample](src/main/java/io/github/scndry/examples/quickstart/SimpleWriteExample.java) | Write POJOs to Excel in one line |
| [SimpleReadExample](src/main/java/io/github/scndry/examples/quickstart/SimpleReadExample.java) | Read Excel to POJOs in one line |

## Read

| Example | Description |
|---------|-------------|
| [BasicReadExample](src/main/java/io/github/scndry/examples/read/BasicReadExample.java) | Read all rows or first row |
| [MultiSheetReadExample](src/main/java/io/github/scndry/examples/read/MultiSheetReadExample.java) | Read from specific sheet by name or index |
| [StreamingReadExample](src/main/java/io/github/scndry/examples/read/StreamingReadExample.java) | Row-by-row iteration with location tracking, batch processing |

## Write

| Example | Description |
|---------|-------------|
| [BasicWriteExample](src/main/java/io/github/scndry/examples/write/BasicWriteExample.java) | Write to file, OutputStream, named sheet, byte array |
| [StyleWriteExample](src/main/java/io/github/scndry/examples/write/StyleWriteExample.java) | Data formats, fonts, borders, fills, header styles |
| [MergeWriteExample](src/main/java/io/github/scndry/examples/write/MergeWriteExample.java) | Vertical cell merging with nested lists |

## Nested Objects

| Example | Description |
|---------|-------------|
| [NestedObjectExample](src/main/java/io/github/scndry/examples/nested/NestedObjectExample.java) | Flatten nested POJOs to columns, reconstruct on read |

## Jackson Integration

| Example | Description |
|---------|-------------|
| [JacksonAnnotationExample](src/main/java/io/github/scndry/examples/jackson/JacksonAnnotationExample.java) | @JsonProperty, @JsonIgnore, @JsonPropertyOrder, enum mapping |

## Styling

| Example | Description |
|---------|-------------|
| [SimpleStylesExample](src/main/java/io/github/scndry/examples/style/SimpleStylesExample.java) | `StylesBuilder.simple()` — type-based default formats |
| [CloneStyleExample](src/main/java/io/github/scndry/examples/style/CloneStyleExample.java) | Derive styles from existing ones |

## Configuration

| Example | Description |
|---------|-------------|
| [ConfigurationExample](src/main/java/io/github/scndry/examples/config/ConfigurationExample.java) | Origin, header, column reordering, blank row handling |

## POI Integration

| Example | Description |
|---------|-------------|
| [POIIntegrationExample](src/main/java/io/github/scndry/examples/poi/POIIntegrationExample.java) | Multi-sheet workbook, formula post-processing, direct Sheet read |

## Web (Spring Boot)

| Example | Description |
|---------|-------------|
| [ExcelController](src/main/java/io/github/scndry/examples/web/ExcelController.java) | Download and upload endpoints |

## Advanced

| Example | Description |
|---------|-------------|
| [LargeFileExample](src/main/java/io/github/scndry/examples/advanced/LargeFileExample.java) | 100K+ rows, file-backed SST, encrypted store, POI User Model fallback |

## Requirements

- Java 17+
- jackson-dataformat-spreadsheet 1.2.0
- Spring Boot 3.5 (for web examples)
- H2 (for file-backed shared strings examples)
